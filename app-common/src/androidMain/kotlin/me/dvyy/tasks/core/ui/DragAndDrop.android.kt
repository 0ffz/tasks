package me.dvyy.tasks.core.ui

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset

actual inline fun <reified T> MultiplatformDragAndDropData(data: T, offset: Offset): DragAndDropTransferData {
    return DragAndDropTransferData(
        clipData = ClipData.newPlainText("Data", "task data"),
        localState = data,
    )
}

@OptIn(ExperimentalFoundationApi::class)
actual suspend fun DragAndDropSourceScope.detectPlatformDrag(onDragStart: (Offset) -> Unit) {
    detectDragGesturesAfterLongPress { change, dragAmount ->
        onDragStart(dragAmount)
    }
}

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.platformDragAndDropSource(block: suspend DragAndDropSourceScope.() -> Unit): Modifier {
    return dragAndDropSource(block)
}

actual inline fun <reified T> DragAndDropEvent.dataOrNull(): T? {
    return toAndroidDragEvent().localState as? T
}

actual inline fun <reified T> DragAndDropEvent.isOfType(): Boolean {
    return toAndroidDragEvent().localState is T
}
