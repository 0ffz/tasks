package me.dvyy.tasks.tasks.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message
import me.dvyy.tasks.model.TaskId

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
//    private var lastAppSync = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))

    suspend fun create(list: ListId): Task = withContext(taskEditDispatcher) {
        localStore.createTask(list)
    }

    suspend fun update(
        taskId: TaskId,
        updater: (Task) -> Task,
    ) = withContext(taskEditDispatcher) {
        val task = localStore.getTask(taskId) ?: return@withContext
        localStore.upsertTask(updater(task))
        localStore.saveMessage(Message.Type.Update, taskId)
    }

    suspend fun delete(taskId: TaskId) = withContext(taskEditDispatcher) {
        localStore.deleteTask(taskId)
        localStore.saveMessage(Message.Type.Delete, taskId, Clock.System.now())
    }

    suspend fun move(taskKey: TaskId, listKey: ListId) = withContext(taskEditDispatcher) {
        localStore.moveTask(taskKey, listKey)
    }

    suspend fun reorder(from: TaskId, to: TaskId) = withContext(taskEditDispatcher) {
        localStore.swapRank(from, to)
    }

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = withContext(ioDispatcher) {
        val now = Clock.System.now()
//        Synchronizer.sync(
//            getLocalChanges = { projectChangesSinceLastSync() },
//            fetchServerChanges = { network.sync.pullProjectChanges(lastAppSync.value) },
//            pushChangelist = { network.sync.pushProjectChanges(it) },
//            applyChanges = {
//                it.forEach { message ->
//                    val key = message.uuid.asList()
//                    TODO()
////                    when (message) {
////                        is Message.Delete -> deleteProject(key)
////                        is Message.Update -> upsertProject(message)
////                    }
//                }
//            }
//        )
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

//    private fun updateSyncTime() {
//        val time = Clock.System.now()
//        lastAppSync.value = time
//        settings.encodeValue(Instant.serializer(), KEY_LAST_EDIT, time)
//    }
}
