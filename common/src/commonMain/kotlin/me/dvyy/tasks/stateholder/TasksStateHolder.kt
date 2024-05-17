package me.dvyy.tasks.stateholder

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.TaskListInteractions
import me.dvyy.tasks.ui.elements.week.TaskListKey
import me.dvyy.tasks.ui.elements.week.TaskWithIDState

class TasksStateHolder : ViewModel() {
    // Careful to update both task map and tasks per list, I'd like a SSOT but we really want both!
    private val tasks = mutableMapOf<Uuid, TaskModel>()
    private val tasksToList = mutableMapOf<Uuid, TaskListKey>()
    private val lists = mutableMapOf<TaskListKey, MutableTaskList>()

    val selectedTask = MutableStateFlow<Uuid?>(null)

//    suspend fun getOrLoadDate(date: LocalDate): DateState = withContext(Tasks.ioThread.coroutineContext) {
//        lists.getOrPut(date) {
//            DateState(date).also { state ->
//                store.loadTasksForDay(date)
//                    .onSuccess { it.forEach { state.createTask(this@getOrLoadDate, it) } }
//                    .onFailure { it.printStackTrace() }
//            }
//        }
//    }

    fun selectTask(uuid: Uuid?) {
        selectedTask.update { uuid }
    }

    private fun updateTask(uuid: Uuid, task: TaskState) {
        val existing = tasks[uuid] ?: error("Updating a task that doesn't exist")
        val existingListKey = tasksToList[uuid]
        val existingList = lists[existingListKey] ?: error("Task not in any list!")
        val updated = TaskState.fromModel(existing, task.key).copy(
            name = task.name,
            completed = task.completed,
            highlight = task.highlight,
        )
        tasks[uuid] = updated.toModel(uuid)

        if (existingListKey != task.key) {
            existingList.remove(uuid)
            tasksToList[uuid] = task.key
            lists.getOrPut(task.key) { MutableTaskList() }[uuid] = TaskWithIDState(
                updated,
                uuid,
                interactionsFor(uuid)
            )
        } else {
            existingList.update(uuid, updated)
        }
        // TODO queue save
        // TODO synchronized editing
    }

    fun taskStateFor(uuid: Uuid): TaskState {
        return TaskState.fromModel(
            tasks[uuid] ?: error("Task uuid was edited after being removed"),
            key = tasksToList[uuid] ?: error("List not found")
        )
    }

    fun createTask(list: TaskListKey) {
        val state = TaskState(
            name = "",
            completed = false,
            key = list,
            highlight = Highlight.Unmarked,
        )
        val model = state.toModel(uuid4())
        tasks[model.uuid] = model
        tasksToList[model.uuid] = list
        lists.getOrPut(list) { MutableTaskList() }[model.uuid] = TaskWithIDState(
            state,
            model.uuid,
            interactionsFor(model.uuid)
        )
    }

    fun deleteTask(uuid: Uuid) {
        tasks.remove(uuid)
        val list = lists[tasksToList[uuid]] ?: return
        list.remove(uuid)
        tasksToList.remove(uuid)
    }

    private val reorderState = ReorderState<Uuid>()

    fun reorderInteractions() = TaskReorderInteractions(
        draggedState = reorderState,
        onDragEnterItem = { targetTask, dragged ->
            //TODO change order in list
        },
        onDragEnterColumn = { targetList, dragged ->
            val id = dragged.data
            updateTask(id, taskStateFor(id).copy(key = targetList))
        }
    )

    fun listInteractionsFor(key: TaskListKey) = TaskListInteractions(
        createNewTask = { createTask(key) }
    )

    fun interactionsFor(uuid: Uuid): TaskInteractions {
        fun update(update: (TaskState) -> TaskState) =
            updateTask(uuid, update(taskStateFor(uuid)))
        return TaskInteractions(
            onTitleChanged = { name -> update { it.copy(name = name) } },
            onListChanged = { date -> update { it.copy(key = TaskListKey.Date(date)) } },
            onCheckChanged = { completed -> update { it.copy(completed = completed) } },
            onHighlightChanged = { highlight -> update { it.copy(highlight = highlight) } },
            onSelect = { selectTask(uuid) },
            onDelete = { deleteTask(uuid) },
            onKeyEvent = { event ->
                if (event.key == Key.Backspace) {
                    if (taskStateFor(uuid).name.isEmpty()) {
                        //TODO select previous task
//                        focusManager.moveFocus(FocusDirection.Up)
                        deleteTask(uuid)
                    }
                    return@TaskInteractions false
                }
                if (event.type != KeyEventType.KeyDown) return@TaskInteractions false
                when {
//                    event.isCtrlPressed && event.key == Key.E -> {
//                        task.highlight.update {
//                            Highlight.entries[(it.ordinal + 1) % Highlight.entries.size]
//                        }
//                        true
//                    }

                    event.key == Key.Escape -> {
                        selectTask(null)
                        true
                    }

                    event.key == Key.Enter -> {
                        selectNextTaskOrNew(uuid)
                        true
                    }

                    else -> false
                }
            },
            keyboardActions = KeyboardActions(onNext = { selectNextTaskOrNew(uuid) }),
        )
    }

    fun selectNextTaskOrNew(uuid: Uuid) {
        val listKey = tasksToList[uuid] ?: return
        val list = lists[listKey] ?: return
        val nextTask = list.taskAfter(uuid)
        if (nextTask != null) {
            selectTask(nextTask)
        } else if (tasks[uuid]?.name?.isEmpty() != true) {
            createTask(listKey)
        }
    }

    fun tasksFor(key: TaskListKey): StateFlow<List<TaskWithIDState>?> {
        return lists.getOrPut(key) { MutableTaskList() }.tasksFlow()
    }

//    fun saveTasks() {
//        loadedDates.values.forEach { state -> saveDay(state) }
//    }
//
//    fun saveDay(state: DateState) {
//        store.saveDay(state.date, state.tasks.value.map {
//            it.toModel()
//        })
//    }
//    fun queueSaveDay(state: DateState) {
//        Tasks.singleThread.launch {
//            daysToSave.add(state)
//            if (saveQueued) return@launch
//            saveQueued = true
//            delay(AppConstants.bufferTaskSaves)
//            daysToSave.forEach { saveDay(it) }
//            daysToSave.clear()
//            saveQueued = false
//        }
//    }
}
