package me.dvyy.tasks.stateholder

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.data.TaskRepository
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.TaskListInteractions
import me.dvyy.tasks.ui.elements.week.TaskListKey
import me.dvyy.tasks.ui.elements.week.TaskWithIDState

class TasksViewModel(
    private val tasks: TaskRepository,
) : ViewModel() {
    // Careful to update both task map and tasks per list, I'd like a SSOT but we really want both!
//    private val tasks = mutableMapOf<Uuid, TaskModel>()
    val scope = CoroutineScope(Dispatchers.Default)
    val selectedTask = MutableStateFlow<Uuid?>(null)
    val requestedSelectTask = MutableStateFlow<Uuid?>(null)

    fun selectTask(uuid: Uuid?) {
        if (selectedTask.value == uuid) return
        println("updating selected to $uuid")
        requestedSelectTask.update { uuid }
    }

    sealed interface TaskList {
        data object Loading : TaskList

        @Immutable
        data class Data(val tasks: List<TaskWithIDState>) : TaskList
    }

    @Composable
    fun tasksFor(key: TaskListKey): StateFlow<TaskList> = remember(key) {
        tasks
            .tasksFor(key)
            .map { list ->
                TaskList.Data(list.map { model ->
                    TaskWithIDState( //TODO maybe cache to avoid so many object recreations?
                        TaskState.fromModel(model, key),
                        model.uuid,
                    )
                })
            }
            .stateIn(scope, SharingStarted.Eagerly, TaskList.Loading)
    }


    private val reorderState = ReorderState<Uuid>()

    fun reorderInteractions() = TaskReorderInteractions(
        draggedState = reorderState,
        onDragEnterItem = { targetTask, dragged ->
            println(tasks.getModel(targetTask)?.name)
            scope.launch {
                tasks.reorderTask(dragged.data, to = targetTask)
            }
        },
        onDragEnterColumn = { targetList, dragged ->
            val id = dragged.data
            scope.launch { tasks.moveTask(id, targetList) }
        }
    )

    fun listInteractionsFor(key: TaskListKey) = TaskListInteractions(
        createNewTask = { scope.launch { selectTask(tasks.createTask(key)) } }
    )

    fun interactionsFor(uuid: Uuid): TaskInteractions {
        fun update(updater: (TaskModel) -> TaskModel) = tasks.updateTask(uuid, updater)
        return TaskInteractions(
            onTitleChanged = { name -> update { it.copy(name = name) } },
            onListChanged = { date ->
                scope.launch { tasks.moveTask(uuid, TaskListKey.Date(date)) }
            },
            onCheckChanged = { completed -> update { it.copy(completed = completed) } },
            onHighlightChanged = { highlight -> update { it.copy(highlight = highlight) } },
            onSelect = { selectTask(uuid) },
            onDelete = { tasks.deleteTask(uuid) },
            onKeyEvent = { event ->
                if (event.key == Key.Backspace) {
                    val list = tasks.getListFor(uuid) ?: return@TaskInteractions false
                    //TODO double check is it != false?
                    if (list[uuid]?.name?.isEmpty() == true) {
                        selectTask(list.taskBefore(uuid))
                        tasks.deleteTask(uuid)
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
        val list = tasks.getListFor(uuid) ?: return
        val nextTask = list.taskAfter(uuid)
        if (nextTask != null) {
            selectTask(nextTask)
        } else if (list[uuid]?.name?.isEmpty() != true) {
            scope.launch {
                selectTask(tasks.createTask(list.key))
            }
        }
    }
}
