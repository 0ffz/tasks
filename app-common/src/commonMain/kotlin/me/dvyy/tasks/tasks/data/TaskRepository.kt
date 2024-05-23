package me.dvyy.tasks.tasks.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.SetSerializer
import me.dvyy.tasks.app.data.AppConstants
import me.dvyy.tasks.app.ui.Task
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.UuidSerializer
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle
import kotlin.collections.set

private const val KEY_LAST_EDIT = "app-last-edit"
private const val KEY_DELETED_TASKS = "app-deleted-tasks"

class TaskRepository(
    private val localStore: TasksLocalDataSource,
    private val network: TasksNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskEditDispatcher = ioDispatcher.limitedParallelism(1)
    private val queueSaveMutex = Mutex()
    private val tasksToList = mutableMapOf<Uuid, ListKey>()
    private val lists = mutableStateMapOf<ListKey, MutableTaskList>()
    private var lastSyncTime = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))

    suspend fun moveTask(uuid: Uuid, newList: ListKey) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[uuid]
        if (existingListKey == newList) return@withContext
        val existingList = lists[existingListKey] ?: return@withContext
        val task = existingList.remove(uuid) ?: return@withContext
        val list = getOrLoadList(newList)
        tasksToList[uuid] = newList
        list[uuid] = task
        if (existingListKey != null) queueSaveList(existingListKey)
        queueSaveList(newList)
    }

    suspend fun reorderTask(uuid: Uuid, to: Uuid) = withContext(taskEditDispatcher) {
        val targetList = tasksToList[to] ?: return@withContext
        if (tasksToList[uuid] != targetList) {
            moveTask(uuid, targetList)
        }
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.reorder(uuid, to)
    }

    suspend fun updateTask(
        uuid: Uuid,
        updater: (TaskModel) -> TaskModel
    ) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        val existingTask = existingList[uuid] ?: error("Task not found in list!")
        val newTask = updater(existingTask).copy(lastModified = Clock.System.now())
        existingList[uuid] = newTask
        if (existingListKey != null) queueSaveList(existingListKey)
    }

    suspend fun createOrUpdateTask(
        listKey: ListKey,
        model: TaskModel,
    ) {
        val uuid = model.uuid
        val existingListKey = tasksToList[uuid]
        if (existingListKey == null) {
            val list = getOrLoadList(listKey)
            list[uuid] = model //TODO preserve order
            tasksToList[uuid] = listKey
            return
        }
        val list = getOrLoadList(listKey)
        if (listKey != existingListKey) {
            moveTask(uuid, listKey)
        }
        list[uuid] = model
        queueSaveList(listKey)
    }

    suspend fun updateTask(uuid: Uuid, task: TaskModel) = withContext(taskEditDispatcher) {
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        existingList[uuid] = task.copy(lastModified = Clock.System.now())
    }

    fun getModel(uuid: Uuid): TaskModel? {
        return listFor(uuid)?.get(uuid)
    }

    fun taskBefore(uuid: Uuid) = listFor(uuid)?.taskBefore(uuid)
    fun taskAfter(uuid: Uuid) = listFor(uuid)?.taskAfter(uuid)

    private fun listFor(uuid: Uuid): MutableTaskList? {
        return lists[tasksToList[uuid]]
    }

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
        queueSaveList(key)
        model.uuid
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
            MutableTaskList(key, list.title, list.tasks)
        }
    }

    private val listsToSave = mutableSetOf<ListKey>()
    private var saveQueued = false

    suspend fun queueSaveList(key: ListKey) = withContext(ioDispatcher) {
        queueSaveMutex.withLock {
            listsToSave.add(key)
            if (!saveQueued) {
                saveQueued = true
                launch {
                    delay(AppConstants.bufferTaskSaves)
                    listsToSave.forEach {
                        val list = lists[it]?.toListModel() ?: return@forEach
                        localStore.saveList(it, list)
                    }
                    listsToSave.clear()
                    saveQueued = false
                }
            }
        }
    }

    private fun getLocalDeletions() = settings.decodeValue(SetSerializer(UuidSerializer), KEY_DELETED_TASKS, emptySet())

    suspend fun deleteTask(uuid: Uuid) = withContext(taskEditDispatcher) {
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.remove(uuid)
        tasksToList.remove(uuid)
        // TODO queue load and save
        val deletions = getLocalDeletions()
        settings.encodeValue(SetSerializer(UuidSerializer), KEY_DELETED_TASKS, deletions + uuid)
        queueSaveList(list.key)
    }

    suspend fun createProject(key: ListKey, title: ListTitle.Project) = withContext(taskEditDispatcher) {
        val list = MutableTaskList(key, title, emptyList())
        lists[key] = list
        queueSaveList(key)
    }

    suspend fun deleteProject(key: ListKey.Project) = withContext(taskEditDispatcher) {
        val list = lists.remove(key)
        list?.models()?.forEach { tasksToList.remove(it.uuid) }
        localStore.deleteList(key)
    }

    fun listKeyFor(uuid: Uuid): ListKey? = tasksToList[uuid]


    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = withContext(ioDispatcher) {
        val now = Clock.System.now()
        TasksSynchronizer().sync(
            getLocalTasks = { lists.values.associate { it.key to it.toListModel() } },
            getLocalDeletions = { getLocalDeletions() },
            pullChangelist = { network.pullChangelist(lastSyncTime.value, now) },
            pushChangelist = { network.pushChangelist(it) },
            applyChangelist = { changelist ->
                changelist.apply {
                    deletedProjects.forEach { deleteProject(it) }
                    createdProjects.forEach { createProject(it.key, ListTitle.Project(it.name)) }
                    updatedTasks.forEach {
                        it.value.forEach { task ->
                            createOrUpdateTask(it.key, task)
                        }
                    }
                    deletedTasks.forEach { deleteTask(it.data) }
                }
            },
        )
    }

    private fun updateSyncTime() {
        val time = Clock.System.now()
        lastSyncTime.value = time
        settings.encodeValue(Instant.serializer(), KEY_LAST_EDIT, time)
    }
}
