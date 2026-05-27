package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Crop11
import dev.seyfarth.tablericons.outlined.Minus
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.topbar.BetterWindowDraggableArea
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
actual fun PlatformSpecificTopBarActions() = PlatformTopBarContainer(Modifier, {
    ButtonRow(Modifier.height(UI.tabHeight)) {
        val viewModel: TopbarViewModel by rememberViewModel()
        BoxButton(TablerIcons.Outlined.Minus, onClick = { viewModel.minimize() }, "Minimize")
        BoxButton(TablerIcons.Outlined.Crop11, onClick = { viewModel.toggleMaximized() }, "Maximize")
        BoxButton(TablerIcons.Outlined.X, onClick = { viewModel.closeWindow() }, "Close")
    }
})

@Composable
actual fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit) {
    val viewModel: TopbarViewModel by rememberViewModel()

    viewModel.windowScope.BetterWindowDraggableArea(
        modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { viewModel.toggleMaximized() })
        }
    ) {
        content()
    }
}
