package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.Layout
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun AppDrawer(
    content: @Composable () -> Unit,
) {
    val app: AppState by rememberInstance()
    val layout: LayoutViewModel by rememberViewModel()
    val ui = LocalUIState.current
    DismissibleNavigationDrawer(
        // Prevent swipe to open on desktop, but allow swipe to close.
        gesturesEnabled = ui.isSmall || app.drawerState.isOpen,
        drawerState = app.drawerState,
        drawerContent = {
            DismissibleDrawerSheet(drawerTonalElevation = 2.dp) {
                Scaffold(
                    Modifier.padding(top = UI.padding.sm),
                    containerColor = Color.Transparent,
                ) {
                    Box(Modifier.padding(it)) {
                        Layout(layout.mobileLeftSidebar.collectAsState().value)
                    }
                }
            }
        },
    ) {
        content()
    }
}

@Composable
fun AppDrawerIconButton() {
    val app: AppState by rememberInstance()
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                app.drawerState.open()
            }
        },
    ) {
        Icon(
            Icons.Outlined.Menu,
            contentDescription = "Menu",
        )
    }
}
