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
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import kotlin.uuid.Uuid


@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun ReorderableTask(
    key: Uuid,
    reorderInteractions: TaskReorderInteractions,
    content: @Composable () -> Unit,
) {
    val dragAndDropModifier = Modifier.platformDragAndDropSource {
        MultiplatformDragAndDropData(TaskId(key), Offset.Zero)
    }
        .dragAndDropTarget(
            shouldStartDragAndDrop = { it.isOfType<TaskId>() },
            target = remember(key) {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        return true
                    }

                    override fun onEntered(event: DragAndDropEvent) {
                        println("Entered task $event")
                        //TODO dragging
//                        val draggedKey = event.dataOrNull<TaskId>()
//                        if (key != draggedKey) reorderInteractions.onDragEnterItem(key, draggedKey ?: return)
                    }
                }
            }
        )
    Box(dragAndDropModifier) {
        content()
    }
}
