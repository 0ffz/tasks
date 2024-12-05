package me.dvyy.tasks.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset


expect inline fun <reified T> MultiplatformDragAndDropData(
    data: T,
    offset: Offset,
): DragAndDropTransferData

@OptIn(ExperimentalFoundationApi::class)
@Composable
expect fun Modifier.platformDragAndDropSource(
    transferData: (Offset) -> DragAndDropTransferData?,
): Modifier

expect inline fun <reified T> DragAndDropEvent.dataOrNull(): T?

expect inline fun <reified T> DragAndDropEvent.isOfType(): Boolean
