package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.geometry.Offset
import me.dvyy.tasks.core.ui.MultiplatformDragAndDropData
import me.dvyy.tasks.core.ui.dataOrNull
import me.dvyy.tasks.core.ui.isOfType
import me.dvyy.tasks.core.ui.platformDragAndDropSource
import me.dvyy.tasks.model.TaskId


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableTask(
    key: TaskId,
    onDropTask: (TaskId) -> Unit,
    content: @Composable () -> Unit,
) {
    val dragAndDropModifier = Modifier.platformDragAndDropSource {
        MultiplatformDragAndDropData(key, Offset.Zero)
    }
        .dragAndDropTarget(
            shouldStartDragAndDrop = { it.isOfType<TaskId>() },
            target = remember(key) {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        val draggedKey = event.dataOrNull<TaskId>()
                        println("Entered task $event with data $draggedKey")
                        if (key != draggedKey) onDropTask(draggedKey ?: return false)
                        return true
                    }
                }
            }
        )
    Box(dragAndDropModifier) {
        content()
    }
}
