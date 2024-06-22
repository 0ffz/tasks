package me.dvyy.tasks.tasks.data

import androidx.compose.runtime.mutableStateMapOf
import com.benasher44.uuid.uuid4
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.data.AppConstants
import me.dvyy.tasks.model.*
import me.dvyy.tasks.model.sync.TaskListNetworkModel
import me.dvyy.tasks.model.sync.TaskNetworkModel
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import kotlin.collections.set

private const val KEY_LAST_EDIT = "app-last-edit"
//private const val KEY_DELETED_TASKS = "app-deleted-tasks"

class TaskRepository(
    private val localStore: TasksLocalDataSource,
    private val network: TasksNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskEditDispatcher = ioDispatcher.limitedParallelism(1)
    private val tasksToList = mutableMapOf<TaskId, ListId>()
    private val lists = mutableStateMapOf<ListId, MutableTaskList>()
    private var lastAppSync = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))

    val projects = localStore.getProjects()

    suspend fun createTask(list: ListId): TaskId = withContext(taskEditDispatcher) {
        val state = TaskUiState(
            text = "",
            completed = false,
            highlight = Highlight.Unmarked,
        )
        val model = state.toModel(TaskId(uuid4()))
        getOrLoadList(list)[model.id] = model
        tasksToList[model.id] = list
//        localStore.saveMessage(Message.Type.Update, model.id)
        model.id
    }

    suspend fun updateTask(
        taskId: TaskId,
        updater: (TaskModel) -> TaskModel,
    ) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[taskId]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        val existingTask = existingList[taskId] ?: error("Task not found in list!")
        val newTask = updater(existingTask)
        existingList[taskId] = newTask
        localStore.saveMessage(Message.Type.Update, taskId)
    }

    suspend fun upsertTask(message: Message.Update<TaskNetworkModel>) {
        val model = message.data
        val uuid = TaskId(message.uuid)
        val listKey = model.list
        val currentListKey = tasksToList[uuid]
        if (currentListKey == null) {
            val list = getOrLoadList(listKey)
            list[uuid] = model.toTaskModel(uuid) //TODO preserve order
            tasksToList[uuid] = listKey
            return
        }
        val list = getOrLoadList(listKey)
        if (listKey != currentListKey) {
            moveTask(uuid, listKey)
        }
        list[uuid] = model.toTaskModel(uuid)
    }

    suspend fun moveTask(taskKey: TaskId, listKey: ListId) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[taskKey]
        if (existingListKey == listKey) return@withContext
        val existingList = lists[existingListKey] ?: return@withContext
        val task = existingList.remove(taskKey) ?: return@withContext
        val list = getOrLoadList(listKey)
        tasksToList[taskKey] = listKey
        list[taskKey] = task
    }

    suspend fun reorderTask(uuid: TaskId, to: TaskId) = withContext(taskEditDispatcher) {
        val targetList = tasksToList[to] ?: return@withContext
        if (tasksToList[uuid] != targetList) {
            moveTask(uuid, targetList)
        }
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.reorder(uuid, to)
    }

    fun getModel(uuid: TaskId): TaskModel? {
        return listFor(uuid)?.get(uuid)
    }

    fun taskBefore(uuid: TaskId) = listFor(uuid)?.taskBefore(uuid)
    fun taskAfter(uuid: TaskId) = listFor(uuid)?.taskAfter(uuid)

    private fun listFor(uuid: TaskId): MutableTaskList? {
        return lists[tasksToList[uuid]]
    }


    fun tasksFor(key: ListId): Flow<List<TaskModel>> = localStore.getTasksForList(key)

    fun listInfo(key: ListId): Flow<TaskListModel> = flow {
        val list = getOrLoadList(key)
        emit(list.toListModel())
    }

    fun listProperties(key: ListId): Flow<TaskListProperties> = localStore.getListProperties(key)


    suspend fun setListProperties(key: ListId, properties: TaskListProperties) {
        localStore.saveList(key, getOrLoadList(key).toListModel())
    }

    private suspend fun getOrLoadList(key: ListId): MutableTaskList = withContext(taskEditDispatcher) {
        lists.getOrPut(key) { //TODO remove
            val list = /*localStore
                .loadTasksForList(key)
                .getOrElse {
                    println("Failed to load tasks for $key")
                    it.printStackTrace()
                    null
                } ?: */TaskListModel(TaskListProperties(date = key.date))
            list.tasks.forEach { tasksToList[it.id] = key }
            MutableTaskList(key, list, queueSave = { queueSaveList(key) })
        }
    }

    private val listsToSave = mutableSetOf<ListId>()
    private var saveQueued = false

    private fun queueSaveList(key: ListId) = CoroutineScope(taskEditDispatcher).launch {
        listsToSave.add(key)
        if (!saveQueued) {
            saveQueued = true
            delay(AppConstants.bufferTaskSaves)
            listsToSave.forEach {
                val list = lists[it]?.toListModel() ?: return@forEach
                localStore.saveList(it, list)
            }
            listsToSave.clear()
            saveQueued = false
        }
    }

    suspend fun deleteTask(uuid: TaskId) = withContext(taskEditDispatcher) {
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.remove(uuid)
        tasksToList.remove(uuid)
        // TODO queue save?
        localStore.saveMessage(Message.Type.Delete, uuid, Clock.System.now())
    }

    suspend fun createProject(key: ListId, properties: TaskListProperties) = withContext(taskEditDispatcher) {
        localStore.saveList(key, TaskListModel(properties))
        getOrLoadList(key)
    }

    suspend fun deleteProject(listId: ListId) = withContext(taskEditDispatcher) {
        val list = lists.remove(listId)
        list?.models()?.forEach { tasksToList.remove(it.id) }
        localStore.deleteList(listId)
        localStore.saveMessage(Message.Type.Delete, listId)
    }

    suspend fun upsertProject(list: ListId, properties: TaskListProperties) {
        localStore.setListProperties(list, properties)
//        val key = ListId(message.uuid)
//        TODO()
//        lists[key]?.setProperties(ListTitle.Project(message.data.title)) ?: run {
//            createProject(key, ListTitle.Project(message.data.title))
//            return
//        }
    }

    fun listIdFor(uuid: TaskId): ListId? = tasksToList[uuid]
    fun listIdFor(date: LocalDate): ListId = TODO()

    fun projectChangesSinceLastSync(): Changelist<TaskListNetworkModel> {
        TODO()
    }

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = withContext(ioDispatcher) {
        val now = Clock.System.now()
        Synchronizer.sync(
            getLocalChanges = { projectChangesSinceLastSync() },
            fetchServerChanges = { network.sync.pullProjectChanges(lastAppSync.value) },
            pushChangelist = { network.sync.pushProjectChanges(it) },
            applyChanges = {
                it.forEach { message ->
                    val key = message.uuid.asList()
                    TODO()
//                    when (message) {
//                        is Message.Delete -> deleteProject(key)
//                        is Message.Update -> upsertProject(message)
//                    }
                }
            }
        )
        lists.forEach { (key, list) ->
            Synchronizer.sync<TaskNetworkModel>(
                getLocalChanges = list::changesSinceLastSync,
                fetchServerChanges = { network.sync.pullTaskChanges(list.key, list.properties.value.lastSynced) },
                pushChangelist = { network.sync.pushTaskChanges(it) },
                applyChanges = {
                    it.forEach { message ->
                        when (message) {
                            is Message.Delete -> deleteTask(message.uuid.asTask())
                            is Message.Update -> upsertTask(message)
                        }
                    }
                }
            )
        }
        // TODO we need to clear local changelists after sync
        // TODO do we want separate functions for handling message and updating models locally?
        //  The latter requires saving a message but other doesn't since we're applying a sync.
    }

    private fun updateSyncTime() {
        val time = Clock.System.now()
        lastAppSync.value = time
        settings.encodeValue(Instant.serializer(), KEY_LAST_EDIT, time)
    }
}
