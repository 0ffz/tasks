package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.buttons.SettingsButton
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.Layout
import me.dvyy.tasks.sync.ui.SyncIndicator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppDrawer(
    app: AppState = koinInject(),
    layout: LayoutViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val ui = LocalUIState.current
    ModalNavigationDrawer(
        // Prevent swipe to open on desktop, but allow swipe to close.
        gesturesEnabled = ui.isSmall || app.drawerState.isOpen,
        drawerState = app.drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Scaffold(
                    Modifier.padding(top = UI.padding.sm),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        HorizontalDivider()
                        Row(Modifier.padding(UI.padding.sm)) {
                            val buttons by layout.layoutButtonLocations.collectAsState()
                            val selected by layout.mobileLeftSidebar.collectAsState()
                            buttons.left.forEach { button ->
                                val isSelected = button.structure == selected
                                LayoutToggleButton(button, isSelected) {
                                    layout.setLeftSidebar(
                                        if (isSelected) LayoutStructure.Remove
                                        else button.structure
                                    )
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            SyncIndicator()
                            SettingsButton()
                        }
                    }
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
