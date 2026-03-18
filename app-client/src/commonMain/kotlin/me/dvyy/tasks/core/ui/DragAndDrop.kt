package me.dvyy.tasks.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import co.touchlab.kermit.Logger
import kotlinx.coroutines.coroutineScope


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


@Composable
fun Modifier.platformDragAndDropSource(
    onClick: () -> Unit,
    transferData: (Offset) -> DragAndDropTransferData?,
): Modifier {
    return platformDragAndDropSource(transferData).pointerInput(onClick) {
        coroutineScope {
            awaitEachGesture {
                val firstDown = awaitFirstDown()
                if (!PlatformSpecifics.preferLongPressDrag)
                    firstDown.consume()
                val waited = waitForUpOrCancellation()
                waited?.consume()
                Logger.v { waited?.type.toString() }
                if (waited?.type != null) onClick()
            }
        }
    }
}
