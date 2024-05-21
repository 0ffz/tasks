package me.dvyy.tasks.tasks.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import me.dvyy.tasks.data.PersistentStore
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.state.AppConstants
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.stateholder.MutableTaskList
import me.dvyy.tasks.ui.elements.week.TaskListKey

class TaskRepository(
    private val localStore: PersistentStore,
    private val ioDispatcher: CoroutineDispatcher,
) {
    val queueSaveMutex = Mutex()
    private val tasksToList = mutableMapOf<Uuid, TaskListKey>()
    private val lists = mutableStateMapOf<TaskListKey, MutableTaskList>()

    suspend fun moveTask(uuid: Uuid, newList: TaskListKey) = withContext(ioDispatcher) {
        val existingListKey = tasksToList[uuid]
        if (existingListKey == newList) return@withContext
        val existingList = lists[existingListKey] ?: return@withContext
        val task = existingList.remove(uuid) ?: return@withContext
        val list = getOrLoadList(newList)
        tasksToList[uuid] = newList
        list[uuid] = task
    }

    suspend fun reorderTask(uuid: Uuid, to: Uuid) = withContext(ioDispatcher) {
        val targetList = tasksToList[to] ?: return@withContext
        if (tasksToList[uuid] != targetList) {
            moveTask(uuid, targetList)
        }
        val list = lists[tasksToList[uuid]] ?: return@withContext
        list.reorder(uuid, to)
    }

    fun updateTask(uuid: Uuid, updater: (TaskModel) -> TaskModel) {
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        val existingTask = existingList[uuid] ?: error("Task not found in list!")
        val newTask = updater(existingTask)
        existingList[uuid] = newTask
    }

    fun updateTask(uuid: Uuid, task: TaskModel) {
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        existingList[uuid] = task
        // TODO queue save
        // TODO synchronized editing
    }

    fun getModel(uuid: Uuid): TaskModel? {
        return lists[tasksToList[uuid]]?.get(uuid)
    }

    //TODO leaking mutable state
    fun getListFor(uuid: Uuid): MutableTaskList? {
        return lists[tasksToList[uuid]]
    }

    // TODO mutex
    suspend fun createTask(key: TaskListKey): Uuid = withContext(ioDispatcher) {
        val state = TaskState(
            name = "",
            completed = false,
            key = key,
            highlight = Highlight.Unmarked,
        )
        val model = state.toModel(uuid4())
        tasksToList[model.uuid] = key
        getOrLoadList(key)[model.uuid] = model
        model.uuid
    }


    fun tasksFor(key: TaskListKey): Flow<List<TaskModel>> = flow {
        emitAll(getOrLoadList(key).tasksFlow())
    }
    fun projects(): Flow<List<TaskListKey.Project>> = flow {

        localStore.getProjects().getOrNull()?.forEach { key ->
            getOrLoadList(key)
        }
        emitAll(snapshotFlow { lists.keys.filterIsInstance<TaskListKey.Project>() })
    }



    private suspend fun getOrLoadList(key: TaskListKey): MutableTaskList = withContext(ioDispatcher) {
        lists.getOrPut(key) {
            val tasks = localStore
                .loadTasksForList(key)
                .getOrElse {
                    println("Failed to load tasks for $key")
                    it.printStackTrace()
                    emptyList()
                }
            tasks.forEach { tasksToList[it.uuid] = key }

            MutableTaskList(key, localStore, tasks)
        }
    }

    private val listsToSave = mutableSetOf<TaskListKey>()
    private var saveQueued = false

    suspend fun queueSaveList(key: TaskListKey) = withContext(ioDispatcher) {
        queueSaveMutex.withLock {
            listsToSave.add(key)
            if (!saveQueued) {
                saveQueued = true
                launch {
                    delay(AppConstants.bufferTaskSaves)
                    listsToSave.forEach {
                        val tasks = lists[it]?.models() ?: return@forEach
                        localStore.saveList(it, tasks)
                    }
                    listsToSave.clear()
                    saveQueued = false
                }
            }
        }
    }

    fun deleteTask(uuid: Uuid) {
        val list = lists[tasksToList[uuid]] ?: return
        list.remove(uuid)
        tasksToList.remove(uuid)
    }

    suspend fun createProject(key: TaskListKey) {
        val list = MutableTaskList(key, localStore, emptyList())
        lists[key] = list
        queueSaveList(key)
    }
}
