package me.dvyy.tasks.app.ui.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.elements.AppTopBarActions
import me.dvyy.tasks.app.ui.elements.AppTopBarTitle
import me.dvyy.tasks.di.koinViewModel
import org.koin.compose.koinInject

@Composable
fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxHeight().width(44.dp),
        contentColor = Color.White,
        color = Color.Transparent
    ) {
        Icon(icon, "", Modifier.padding(10.dp))
    }
}

@Composable
fun DesktopTopBar(
    transparent: Boolean = false,
    showTitle: Boolean = true,
    showBackButton: Boolean = false,
    viewModel: TopbarViewModel = koinViewModel(),
    onBackButtonClicked: (() -> Unit) = {},
) {
    viewModel.windowScope.BetterWindowDraggableArea(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { viewModel.toggleMaximized() })
        }
    ) {
        Box(
            Modifier.fillMaxWidth().height(40.dp)
        ) {
            AnimatedVisibility(
                !transparent,
                enter = slideIn(initialOffset = { IntOffset(0, -40) }),
                exit = slideOut(targetOffset = { IntOffset(0, -40) })
            ) {
                Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxSize()) {}
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppDrawerButton()

//                    AnimatedVisibility(showBackButton/*, enter = fadeIn(animationSpec = tween(300, 300))*/) {
//                        WindowButton(Icons.AutoMirrored.Rounded.ArrowBack) {
//                            onBackButtonClicked()
//                        }
//                        Spacer(Modifier.width(5.dp))
//                    }
                    AnimatedVisibility(showTitle) {
                        Row {
//                            Spacer(Modifier.width(8.dp))
                            AppTitle()
                            AppTopBarTitle(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Row {
                    Box(Modifier.padding(4.dp)) {
                        AppTopBarActions()
                    }
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

@Composable
fun AppTitle() {
    Row {
//        Icon(
//            Icons.Rounded.Checklist,
//            contentDescription = "Tasks",
//            tint = MaterialTheme.colorScheme.primary
//        )
        Spacer(Modifier.width(4.dp))
        Text(
            "Tasks — ",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

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

@Composable
fun AppDrawerButton(
    app: AppState = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    WindowButton(Icons.Rounded.Menu) {
        coroutineScope.launch { app.drawerState.open() }
    }
}
