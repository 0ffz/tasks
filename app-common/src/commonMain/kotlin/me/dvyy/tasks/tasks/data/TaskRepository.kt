package me.dvyy.tasks.tasks.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.*
import me.dvyy.tasks.model.sync.TaskListNetworkModel

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

    //    private val tasksToList = mutableMapOf<TaskId, ListId>()
//    private val lists = mutableStateMapOf<ListId, MutableTaskList>()
    private var lastAppSync = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))

    fun getProjects() = localStore.observeProjects()

    suspend fun createTask(list: ListId): Task = withContext(taskEditDispatcher) {
        localStore.createTask(list)
    }

    suspend fun updateTask(
        taskId: TaskId,
        updater: (Task) -> Task,
    ) = withContext(taskEditDispatcher) {
        val task = localStore.getTask(taskId) ?: return@withContext
        localStore.upsertTask(updater(task))
        localStore.saveMessage(Message.Type.Update, taskId)
    }

    suspend fun moveTask(taskKey: TaskId, listKey: ListId) = withContext(taskEditDispatcher) {
        localStore.moveTask(taskKey, listKey)
    }

    suspend fun reorderTask(from: TaskId, to: TaskId) = withContext(taskEditDispatcher) {
        localStore.swapRank(from, to)
    }

//    fun getModel(uuid: TaskId): TaskModel? {
//        return listFor(uuid)?.get(uuid)
//    }

//    fun taskBefore(uuid: TaskId) = listFor(uuid)?.taskBefore(uuid)
//    fun taskAfter(uuid: TaskId) = listFor(uuid)?.taskAfter(uuid)

    fun tasksFor(key: ListId): Flow<List<Task>> = localStore.observeListTasks(key)

    fun getListProperties(key: ListId): Flow<TaskListProperties> = localStore.observeListProperties(key)

    suspend fun deleteTask(taskId: TaskId) = withContext(taskEditDispatcher) {
        localStore.deleteTask(taskId)
        localStore.saveMessage(Message.Type.Delete, taskId, Clock.System.now())
    }

    suspend fun createList(key: ListId, properties: TaskListProperties) = withContext(taskEditDispatcher) {
        localStore.createList(key, TaskListModel(properties))
    }

    suspend fun upsertProject(list: ListId, properties: TaskListProperties) = withContext(taskEditDispatcher) {
        localStore.setListProperties(list, properties)
//        val key = ListId(message.uuid)
//        TODO()
//        lists[key]?.setProperties(ListTitle.Project(message.data.title)) ?: run {
//            createProject(key, ListTitle.Project(message.data.title))
//            return
//        }
    }

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
//        lists.forEach { (key, list) ->
//            Synchronizer.sync<TaskNetworkModel>(
//                getLocalChanges = list::changesSinceLastSync,
//                fetchServerChanges = { network.sync.pullTaskChanges(list.key, list.properties.value.lastSynced) },
//                pushChangelist = { network.sync.pushTaskChanges(it) },
//                applyChanges = {
//                    it.forEach { message ->
//                        when (message) {
//                            is Message.Delete -> deleteTask(message.uuid.asTask())
//                            is Message.Update -> upsertTask(message)
//                        }
//                    }
//                }
//            )
//        }
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
