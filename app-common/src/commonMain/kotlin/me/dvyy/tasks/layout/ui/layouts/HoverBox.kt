package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import me.dvyy.tasks.core.ui.dataOrNull
import me.dvyy.tasks.core.ui.isOfType
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.tasks.ui.elements.list.optional
import me.dvyy.tasks.tree.ui.FileStructure

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HoverBox(
    modifier: Modifier = Modifier,
    onDropped: (LayoutStructure.Single) -> Unit = {},
    hoverableModifier: BoxScope.() -> Modifier,
) {
    var dragTargetVisible by remember { mutableStateOf(false) }

    val hoverable = Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { it.isOfType<FileStructure>() },
        target = remember(onDropped) {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    val file = event.dataOrNull<FileStructure>() ?: return false
                    val layout = (file as? FileStructure.File)?.opensLayout ?: return false
                    onDropped(layout)
                    return true
                }

                override fun onEntered(event: DragAndDropEvent) {
                    dragTargetVisible = true
                }

                override fun onExited(event: DragAndDropEvent) {
                    dragTargetVisible = false
                }

                override fun onEnded(event: DragAndDropEvent) {
                    dragTargetVisible = false
                }
            }
        })

    Box(modifier
        .fillMaxSize()
        .optional(dragTargetVisible) { background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) }
    ) {
        Box(hoverableModifier().then(hoverable))
    }
}
