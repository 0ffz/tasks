package me.dvyy.tasks.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset


expect inline fun <reified T> MultiplatformDragAndDropData(
    data: T,
    offset: Offset,
): DragAndDropTransferData

@OptIn(ExperimentalFoundationApi::class)
expect suspend fun DragAndDropSourceScope.detectPlatformDrag(
    onDragStart: (Offset) -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
expect fun Modifier.platformDragAndDropSource(
    block: suspend DragAndDropSourceScope.() -> Unit,
): Modifier

expect inline fun <reified T> DragAndDropEvent.dataOrNull(): T?

expect inline fun <reified T> DragAndDropEvent.isOfType(): Boolean
