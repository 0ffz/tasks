package me.dvyy.tasks.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.geometry.Offset
import java.awt.datatransfer.StringSelection

@PublishedApi
internal var transferObject: Any? = null

@PublishedApi
internal const val TASKS_DND_MARKER = "|tasksData|"

@OptIn(ExperimentalComposeUiApi::class)
actual inline fun <reified T> MultiplatformDragAndDropData(data: T, offset: Offset): DragAndDropTransferData {
    transferObject = data //TODO make this a bit safer, we might not use the data result right away
    return DragAndDropTransferData(
        transferable = DragAndDropTransferable(
            StringSelection(TASKS_DND_MARKER),
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
    return transferObject as? T
//    val prefix = "tasksData|${typeOf<T>()}|"
//    val string = awtTransferable.getTransferData(DataFlavor.stringFlavor) as String
//    val removed = string.removePrefix(prefix).also { if (string == it) return null }
//    return runCatching { AppFormats.json.decodeFromString(serializer<T>(), removed) }.getOrNull()
}

@OptIn(ExperimentalComposeUiApi::class)
actual inline fun <reified T> DragAndDropEvent.isOfType(): Boolean {
    return transferObject is T
//    val prefix = "tasksData|${typeOf<T>()}|"
//    val string = awtTransferable.getTransferData(DataFlavor.stringFlavor) as String
//    return string.startsWith(prefix)
}
