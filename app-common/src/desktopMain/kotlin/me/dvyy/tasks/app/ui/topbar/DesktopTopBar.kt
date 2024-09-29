package me.dvyy.tasks.app.ui.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.AppTopBarActions
import me.dvyy.tasks.app.ui.elements.AppTopBarTitle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DesktopTopBar(
    transparent: Boolean = false,
    showTitle: Boolean = true,
    viewModel: TopbarViewModel = koinViewModel(),
) {
    val UI = UI
    viewModel.windowScope.BetterWindowDraggableArea(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { viewModel.toggleMaximized() })
        }
    ) {
        Box(
            Modifier.fillMaxWidth().height(UI.size.xl)
        ) {
            AnimatedVisibility(
                !transparent,
                enter = slideIn(initialOffset = { IntOffset(0, -UI.size.xl.value.toInt()) }),
                exit = slideOut(targetOffset = { IntOffset(0, -UI.size.xl.value.toInt()) })
            ) {
                Surface(tonalElevation = UI.elevation.lv1, modifier = Modifier.fillMaxSize()) {}
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (UI.isSmall) AppDrawerButton()
                    else Spacer(Modifier.width(UI.padding.md))

                    AnimatedVisibility(showTitle) {
                        Row {
                            AppTitle()
                            AppTopBarTitle(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Row {
                    if (!UI.isSmall) Box(Modifier.padding(UI.padding.sm)) {
                        AppTopBarActions()
                    }
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
            }
        }
    }
}

