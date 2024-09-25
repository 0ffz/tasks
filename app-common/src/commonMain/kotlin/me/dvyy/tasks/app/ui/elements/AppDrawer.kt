package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.core.ui.components.buttons.SettingsButton
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.sync.ui.SyncViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppDrawer(
    app: AppState = koinInject(),
    auth: AuthViewModel = koinViewModel(),
    sync: SyncViewModel = koinViewModel(),
    dialogs: DialogViewModel = koinViewModel(),
    layout: LayoutViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val ui = LocalUIState.current
    ModalNavigationDrawer(
        // Prevent swipe to open on desktop, but allow swipe to close.
        gesturesEnabled = ui.isSingleColumn || app.drawerState.isOpen,
        drawerState = app.drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Scaffold(
                    Modifier.padding(16.dp),
                    bottomBar = {
                        HorizontalDivider()
                        Row {
                            val buttons by layout.layoutButtonLocations.collectAsState()
                            val selected by layout.leftSidebar.collectAsState()
                            buttons.left.forEach { button ->
                                val isSelected = button.structure == selected
                                LayoutToggleButton(button, isSelected) {
                                    layout.leftSidebar.update {
                                        if (isSelected) LayoutStructure.Empty
                                        else button.structure
                                    }
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            SettingsButton()
                        }
                    }
                ) {
                    Box(Modifier.padding(it)) {
                        Layout(layout.leftSidebar.collectAsState().value)
                    }
                }
            }
        },
    ) {
        content()
    }
}

@Composable
fun AppDrawerIconButton(app: AppState = koinInject()) {
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
