package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.topbar.BetterWindowDraggableArea
import me.dvyy.tasks.app.ui.topbar.WindowButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun PlatformSpecificTopBarActions() = PlatformTopBarContainer(Modifier, {
    Row(Modifier.height(UI.tabHeight)) {
        val viewModel: TopbarViewModel = koinViewModel()

        VerticalDivider(Modifier.padding(UI.padding.md))
        WindowButton(Icons.Rounded.Minimize) {
            viewModel.minimize()
        }
        WindowButton(Icons.Rounded.CropSquare) {
            viewModel.toggleMaximized()
        }
        WindowButton(Icons.Rounded.Close) {
            viewModel.closeWindow()
        }
    }
})

@Composable
actual fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit) {
    val viewModel: TopbarViewModel = koinViewModel()

    viewModel.windowScope.BetterWindowDraggableArea(
        modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { viewModel.toggleMaximized() })
        }
    ) {
        content()
    }
}
