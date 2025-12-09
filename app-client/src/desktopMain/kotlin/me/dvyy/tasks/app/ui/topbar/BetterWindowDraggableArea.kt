package me.dvyy.tasks.app.ui.topbar

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.WindowScope
import me.dvyy.tasks.app.data.TopbarViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WindowScope.BetterWindowDraggableArea(
    modifier: Modifier = Modifier,
    viewModel: TopbarViewModel = koinViewModel(),
    content: @Composable () -> Unit = {},
) {
    WindowDraggableArea(modifier.pointerInput(Unit) {
        detectDragGestures(onDragStart = { viewModel.ensureFloating() }) { _, _ -> }
    }) {
        content()
    }
}
