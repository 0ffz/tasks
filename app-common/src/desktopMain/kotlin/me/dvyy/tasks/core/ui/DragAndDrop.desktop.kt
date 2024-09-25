package me.dvyy.tasks.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.*
import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.serializer
import me.dvyy.tasks.model.serializers.AppFormats
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual inline fun <reified T> MultiplatformDragAndDropData(data: T, offset: Offset): DragAndDropTransferData {
    return DragAndDropTransferData(
        transferable = DragAndDropTransferable(
            StringSelection(AppFormats.json.encodeToString(serializer<T>(), data)),
        ),
        supportedActions = listOf(
            DragAndDropTransferAction.Copy,
            DragAndDropTransferAction.Move,
            DragAndDropTransferAction.Link,
        ),
        dragDecorationOffset = offset
    )
}

@OptIn(ExperimentalFoundationApi::class)
actual suspend fun DragAndDropSourceScope.detectPlatformDrag(onDragStart: (Offset) -> Unit) {
    detectDragGestures(
        onDragStart = onDragStart,
        onDrag = { _, _ -> },
    )
}

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.platformDragAndDropSource(block: suspend DragAndDropSourceScope.() -> Unit): Modifier {
    return dragAndDropSource(drawDragDecoration = {}, block)
}

@OptIn(ExperimentalComposeUiApi::class)
actual inline fun <reified T> DragAndDropEvent.dataOrNull(): T? {
    val string =  awtTransferable.getTransferData(DataFlavor.stringFlavor) as String
    return runCatching { AppFormats.json.decodeFromString(serializer<T>(), string) }.getOrNull()
}
