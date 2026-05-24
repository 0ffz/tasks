package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Minus
import dev.seyfarth.tablericons.outlined.Square
import dev.seyfarth.tablericons.outlined.X
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
        WindowButton(TablerIcons.Outlined.Minus) {
            viewModel.minimize()
        }
        WindowButton(TablerIcons.Outlined.Square) {
            viewModel.toggleMaximized()
        }
        WindowButton(TablerIcons.Outlined.X) {
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
