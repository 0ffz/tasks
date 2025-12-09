package me.dvyy.tasks.core.ui

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

actual inline fun <reified T> MultiplatformDragAndDropData(data: T, offset: Offset): DragAndDropTransferData {
    return DragAndDropTransferData(
        clipData = ClipData.newPlainText("Data", "task data"),
        localState = data,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun Modifier.platformDragAndDropSource(transferData: (Offset) -> DragAndDropTransferData?): Modifier {
    val primary = MaterialTheme.colorScheme.primary
    // TODO Android's default decoration causes issues with composables not recomposing correctly
    //  try to get this to look nice instead
    return dragAndDropSource(drawDragDecoration = {
        val size = size.copy(width = size.width.coerceAtMost(300f))
        drawRoundRect(
            topLeft = center.minus(Offset(size.width, size.height) / 2f),
            size = size,
            color = primary,
            cornerRadius = CornerRadius(16.dp.toPx()),
        )
    }, transferData = transferData)
}

actual inline fun <reified T> DragAndDropEvent.dataOrNull(): T? {
    return toAndroidDragEvent().localState as? T
}

actual inline fun <reified T> DragAndDropEvent.isOfType(): Boolean {
    return toAndroidDragEvent().localState is T
}
