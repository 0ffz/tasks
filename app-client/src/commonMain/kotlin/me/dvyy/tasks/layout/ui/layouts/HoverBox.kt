package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HoverBox(
    modifier: Modifier = Modifier,
    onDropped: (LayoutStructure) -> Unit = {},
    hoverableModifier: BoxScope.() -> Modifier = { Modifier.fillMaxSize() },
) {
    var dragTargetVisible by remember { mutableStateOf(false) }

    val hoverable = Modifier.dropTarget(
        state = LocalDragAndDropState.current,
        onDragEnter = {
            dragTargetVisible = true
        },
        shouldStartDragAndDrop = { it.data is Dragged.Layout },
        onDragExit = { dragTargetVisible = false },
        onDrop = {
            dragTargetVisible = false
            val layout = (it.data as? Dragged.Layout)?.layout ?: return@dropTarget
            onDropped(layout)
        },
        onHoverDraw = {}
//        shouldStartDragAndDrop = { it.isOfType<LayoutStructure.Single>() },
//        target = remember(onDropped) {
//            object : DragAndDropTarget {
//                override fun onDrop(event: DragAndDropEvent): Boolean {
//                }
//
//                override fun onEntered(event: DragAndDropEvent) {
//                    dragTargetVisible = true
//                }
//
//                override fun onExited(event: DragAndDropEvent) {
//                    dragTargetVisible = false
//                }
//
//                override fun onEnded(event: DragAndDropEvent) {
//                    dragTargetVisible = false
//                }
//            }
//        }
    )

    Box(modifier
        .fillMaxSize()
        .optional(dragTargetVisible) { background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) }
    ) {
        Box(hoverableModifier().then(hoverable))
    }
}
