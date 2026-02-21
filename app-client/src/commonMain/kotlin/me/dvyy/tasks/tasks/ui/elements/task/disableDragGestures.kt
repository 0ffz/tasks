package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.disableDragGestures() = pointerInput(Unit) {
    detectDragGestures { _, _ -> }
}