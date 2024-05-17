package me.dvyy.tasks.stateholder

import androidx.compose.foundation.text.KeyboardActions
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.TaskListInteractions
import me.dvyy.tasks.ui.elements.week.TaskListKey
import me.dvyy.tasks.ui.elements.week.TaskWithIDState

class MutableTaskList {
    private val taskIndices = mutableMapOf<Uuid, Int>()
    private val tasksInOrder = mutableListOf<TaskWithIDState>()

    private val tasksFlow = MutableStateFlow(tasksInOrder.toList())
    fun tasksFlow(): StateFlow<List<TaskWithIDState>> = tasksFlow

    fun emitUpdate() = tasksFlow.update { tasksInOrder.toList() }

    operator fun set(uuid: Uuid, task: TaskState) {
        // TODO adding new task
        val index = taskIndices[uuid] ?: error("Task not in list")
        tasksInOrder[index] = tasksInOrder[index].copy(state = task)
        emitUpdate()
    }
}

class TasksStateHolder {
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
        val existingList = lists[tasksToList[uuid]] ?: error("Task not in any list!")
        val updated = TaskState.fromModel(existing, task.key).copy(
            name = task.name,
            completed = task.completed,
            highlight = task.highlight,
        )
        tasks[uuid] = updated.toModel(uuid)
        // TODO queue save
        // TODO synchronized editing
        existingList[uuid] = updated
        existingList.emitUpdate()
    }

    fun taskStateFor(uuid: Uuid): TaskState {
        return TaskState.fromModel(
            tasks[uuid] ?: error("Task uuid was edited after being removed"),
            key = tasksToList[uuid] ?: error("List not found")
        )
    }

    fun listInteractionsFor(key: TaskListKey) = TaskListInteractions(
        createNewTask = { TODO() }
    )

    fun interactionsFor(uuid: Uuid): TaskInteractions {
        fun update(update: (TaskState) -> TaskState) =
            updateTask(uuid, update(taskStateFor(uuid)))
        return TaskInteractions(
            onTitleChanged = { name -> update { it.copy(name = name) } },
            onDateChanged = { date -> update { it.copy(key = TaskListKey.Date(date)) } },
            onCheckChanged = { completed -> update { it.copy(completed = completed) } },
            onHighlightChanged = { highlight -> update { it.copy(highlight = highlight) } },
            onSelect = { selectTask(uuid) },
            onDelete = { TODO() },
//            onKeyEvent = { event ->
//                if (event.key == Key.Backspace) {
//                    if (task.name.value.isEmpty()) {
//                        focusManager.moveFocus(FocusDirection.Up)
//                        task.delete(app)
//                    }
//                    return@TaskInteractions false
//                }
//                if (event.type != KeyEventType.KeyDown) return@TaskInteractions false
//                when {
//                    event.isCtrlPressed && event.key == Key.E -> {
//                        task.highlight.update {
//                            Highlight.entries[(it.ordinal + 1) % Highlight.entries.size]
//                        }
//                        true
//                    }
//
//                    event.key == Key.Escape -> {
//                        selectTask(null)
//                        true
//                    }
//
//                    event.key == Key.Enter -> {
//                        nextTaskOrNew()
//                        true
//                    }
//
//                    else -> false
//                }
//            },
            keyboardActions = KeyboardActions(onNext = { TODO("nextTaskOrNew") }),
        )
    }

    fun tasksFor(key: TaskListKey): StateFlow<List<TaskWithIDState>> {
        return lists[key]?.tasksFlow() ?: MutableStateFlow(emptyList())
    }

    fun keyFor(date: LocalDate): TaskListKey = TaskListKey.Date(date)
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
