package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.core.snap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import co.touchlab.kermit.Logger
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.drop.DropTargetState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.utils.LocalDragAndDropState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableTask(
    enabled: Boolean,
    key: TaskId,
    onDropTask: (TaskId) -> Unit,
    content: @Composable () -> Unit,
) {
    Modifier/*.platformDragAndDropSource {
        MultiplatformDragAndDropData(key, Offset.Zero)
    }*/
//        .dragAndDropTarget(
//            shouldStartDragAndDrop = { it.isOfType<TaskId>() },
//            target = remember(key, onDropTask) {
//                object : DragAndDropTarget {
//                    override fun onDrop(event: DragAndDropEvent): Boolean {
//                        val draggedKey = event.dataOrNull<TaskId>()
//                        Logger.v { "Entered task $key with data $draggedKey" }
//                        if (key != draggedKey) onDropTask(draggedKey ?: return false)
//                        return true
//                    }
//                }
//            }
//        )
    Box(
        Modifier.dropTarget(key, LocalDragAndDropState.current, onDrop = {
            val task = it.data.asTask()
            if (task != key) {
                Logger.i { "Dropping task ${it.data} on ${key.uuid}" }
                onDropTask(task)
            }
        }),
//        contentAlignment = Alignment.CenterStart
    ) {
        DraggableItem(
            enabled = enabled,
            dropAnimationSpec = snap(0),
            dropStrategy = LeftDistance,
            state = LocalDragAndDropState.current, key = key, data = key.uuid,
//            draggableContent = {
//                 Box(Modifier.size(20.dp).background(Color.Red))
//            }
        ) {

            content()
//            Row(Modifier.width(50.dp).height(UI.tasks.height), verticalAlignment = Alignment.CenterVertically) {
//                DragHandle()
//            }
        }
//        Box(Modifier.padding(start = 20.dp)) {
//        }
    }
}


@Composable
fun DragHandle(modifier: Modifier = Modifier) {
    Icon(
        AppIcons.DragIndicator,
        "Drag handle",
        modifier = modifier,
        tint = MaterialTheme.colorScheme.onSurface.fade(0.5f)
    )
}

object LeftDistance : DropStrategy {
    override fun <T> getHoveredDropTarget(
        draggedItemTopLeft: Offset,
        draggedItemSize: Size,
        dropTargets: List<DropTargetState<T>>,
    ): DropTargetState<T>? {
        val p1 = Offset(
            x = draggedItemTopLeft.x,
            y = draggedItemTopLeft.y + draggedItemSize.height / 2f,
        )
        return dropTargets
            .minByOrNull {
                val p2 = Offset(
                    x = it.topLeft.x,
                    y = it.topLeft.y + it.size.height / 2f,
                )
                (p1 - p2).getDistanceSquared()
            }
    }
}