package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import me.dvyy.tasks.core.ui.*
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableTask(
    key: TaskId,
    reorderInteractions: TaskReorderInteractions,
    content: @Composable () -> Unit
) = Column {
    Box(
        Modifier.platformDragAndDropSource {
            detectPlatformDrag {
                startTransfer(MultiplatformDragAndDropData(key, it))
            }
        }.dragAndDropTarget(
            shouldStartDragAndDrop = { it.isOfType<TaskId>() },
            target = remember(key) {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        return true
                    }

                    override fun onEntered(event: DragAndDropEvent) {
                        val draggedKey = event.dataOrNull<TaskId>()
                        if (key != draggedKey) reorderInteractions.onDragEnterItem(key, draggedKey ?: return)
                    }
                }
            },
        )
    ) {
        content()
    }
}
