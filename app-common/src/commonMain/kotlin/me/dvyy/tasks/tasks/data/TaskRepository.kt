package me.dvyy.tasks.tasks.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.app.data.AppConstants
import me.dvyy.tasks.app.ui.Task
import me.dvyy.tasks.model.*
import me.dvyy.tasks.model.sync.ProjectNetworkModel
import me.dvyy.tasks.model.sync.TaskNetworkModel
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle
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
    private val tasksToList = mutableMapOf<Uuid, ListKey>()
    private val lists = mutableStateMapOf<ListKey, MutableTaskList>()
    private var lastAppSync = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))

    suspend fun createTask(key: ListKey): Uuid = withContext(taskEditDispatcher) {
        val state = Task(
            name = "",
            completed = false,
            key = key,
            highlight = Highlight.Unmarked,
        )
        val model = state.toModel(uuid4())
        getOrLoadList(key)[model.uuid] = model
        tasksToList[model.uuid] = key
        localStore.saveMessage(Message.Type.Update, model.uuid)
        model.uuid
    }

    suspend fun updateTask(
        uuid: Uuid,
        updater: (TaskModel) -> TaskModel,
    ) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        val existingTask = existingList[uuid] ?: error("Task not found in list!")
        val newTask = updater(existingTask)
        existingList[uuid] = newTask
        localStore.saveMessage(Message.Type.Update, uuid)
    }

    suspend fun upsertTask(message: Message.Update<TaskNetworkModel>) {
        val (model, uuid) = message
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

    suspend fun moveTask(uuid: Uuid, newList: ListKey) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[uuid]
        if (existingListKey == newList) return@withContext
        val existingList = lists[existingListKey] ?: return@withContext
        val task = existingList.remove(uuid) ?: return@withContext
        val list = getOrLoadList(newList)
        tasksToList[uuid] = newList
        list[uuid] = task
    }

    suspend fun reorderTask(uuid: Uuid, to: Uuid) = withContext(taskEditDispatcher) {
        val targetList = tasksToList[to] ?: return@withContext
        if (tasksToList[uuid] != targetList) {
            moveTask(uuid, targetList)
        }
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.reorder(uuid, to)
    }

    fun getModel(uuid: Uuid): TaskModel? {
        return listFor(uuid)?.get(uuid)
    }

    fun taskBefore(uuid: Uuid) = listFor(uuid)?.taskBefore(uuid)
    fun taskAfter(uuid: Uuid) = listFor(uuid)?.taskAfter(uuid)

    private fun listFor(uuid: Uuid): MutableTaskList? {
        return lists[tasksToList[uuid]]
    }


    fun tasksFor(key: ListKey): Flow<List<TaskModel>> = flow {
        emitAll(getOrLoadList(key).tasksFlow())
    }

    fun projects(): Flow<List<ListKey.Project>> = flow {
        localStore.getProjects().forEach { key ->
            getOrLoadList(key)
        }
        emitAll(snapshotFlow { lists.keys.filterIsInstance<ListKey.Project>() })
    }

    fun project(key: ListKey.Project): Flow<ProjectNetworkModel> = flow {
        val list = getOrLoadList(key)
        emitAll(list.customTitle.map {
            ProjectNetworkModel(key, it?.name)
        })
    }

    private suspend fun getOrLoadList(key: ListKey): MutableTaskList = withContext(taskEditDispatcher) {
        lists.getOrPut(key) {
            val list = localStore
                .loadTasksForList(key)
                .getOrElse {
                    println("Failed to load tasks for $key")
                    it.printStackTrace()
                    null
                } ?: TaskListModel(null, emptyList())
            list.tasks.forEach { tasksToList[it.uuid] = key }
            MutableTaskList(key, list, queueSave = { queueSaveList(key) })
        }
    }

    private val listsToSave = mutableSetOf<ListKey>()
    private var saveQueued = false

    private fun queueSaveList(key: ListKey) = CoroutineScope(taskEditDispatcher).launch {
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

    suspend fun deleteTask(uuid: Uuid) = withContext(taskEditDispatcher) {
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.remove(uuid)
        tasksToList.remove(uuid)
        // TODO queue save?
        localStore.saveMessage(Message.Type.Delete, uuid, Clock.System.now())
    }

    suspend fun createProject(key: ListKey, title: ListTitle.Project) = withContext(taskEditDispatcher) {
        val list = MutableTaskList(key, TaskListModel(title = title), queueSave = { queueSaveList(key) })
        lists[key] = list
        queueSaveList(key)
    }

    suspend fun deleteProject(key: ListKey.Project) = withContext(taskEditDispatcher) {
        val list = lists.remove(key)
        list?.models()?.forEach { tasksToList.remove(it.uuid) }
        localStore.deleteList(key)
        localStore.saveMessage(Message.Type.Delete, key.uuid)
    }

    suspend fun upsertProject(message: Message.Update<ProjectNetworkModel>) {
        val key = ListKey.Project(message.uuid)
        lists[key]?.setCustomTitle(ListTitle.Project(message.data.title)) ?: run {
            createProject(key, ListTitle.Project(message.data.title))
            return
        }
    }

    fun listKeyFor(uuid: Uuid): ListKey? = tasksToList[uuid]

    fun projectChangesSinceLastSync(): Changelist<ProjectNetworkModel> {
        TODO()
    }

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = withContext(ioDispatcher) {
        val now = Clock.System.now()
        Synchronizer.sync<ProjectNetworkModel>(
            getLocalChanges = { projectChangesSinceLastSync() },
            fetchServerChanges = { network.sync.pullProjectChanges(lastAppSync.value) },
            pushChangelist = { network.sync.pushProjectChanges(it) },
            applyChanges = {
                it.forEach { message ->
                    val key = ListKey.Project(message.uuid)
                    when (message) {
                        is Message.Delete -> deleteProject(key)
                        is Message.Update -> upsertProject(message)
                    }
                }
            }
        )
        lists.forEach { (key, list) ->
            Synchronizer.sync<TaskNetworkModel>(
                getLocalChanges = list::changesSinceLastSync,
                fetchServerChanges = { network.sync.pullTaskChanges(list.key, list.lastSynced.value) },
                pushChangelist = { network.sync.pushTaskChanges(it) },
                applyChanges = {
                    it.forEach { message ->
                        when (message) {
                            is Message.Delete -> deleteTask(message.uuid)
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
