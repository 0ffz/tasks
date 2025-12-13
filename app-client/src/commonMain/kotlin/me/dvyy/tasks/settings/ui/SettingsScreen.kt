package me.dvyy.tasks.settings.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.dialogs.ScreenContainer
import me.dvyy.tasks.tasks.ui.elements.list.optional

sealed interface SettingsTab {
    val title: String
    val icon: ImageVector

    data object Theme : SettingsTab {
        override val title = "Theme"
        override val icon = Icons.Outlined.Palette
    }

    data object BulkAdd : SettingsTab {
        override val title = "Bulk add"
        override val icon = Icons.Outlined.UploadFile
    }

    data object Sync : SettingsTab {
        override val title = "Sync"
        override val icon = Icons.Outlined.Sync
    }

    companion object {
        val tabs = listOf(Sync, Theme, BulkAdd)
    }
}


@Composable
fun RowOrBox(isRow: Boolean, content: @Composable () -> Unit) {
    if (isRow) Row(Modifier.fillMaxWidth()) { content() }
    else Box(Modifier.fillMaxWidth()) { content() }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen() {
    val ui = LocalUIState.current
    val scope = rememberCoroutineScope()
    val expanded = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Expanded)
    var screen by remember { mutableStateOf<SettingsTab>(SettingsTab.Sync) }
    ScreenContainer("Settings - ${screen.title}", extraItems = {

        if (ui.isSmall) {
            IconButton(onClick = { scope.launch { expanded.toggle() } }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Open menu")
            }
        }
    }) {

        RowOrBox(!ui.isSmall) {

            AnimatedVisibility(
                !ui.isSmall || expanded.targetValue == WideNavigationRailValue.Expanded,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {

//                WideNavigationRail(
//                    state = expanded,
//                    modifier = Modifier.fillMaxWidth()
////                colors = MaterialTheme.colorScheme.surface,
//
////                tonale = UI.elevation.lv1,
//                ) {
//                    SettingsTab.tabs.forEach { tab ->
//                        WideNavigationRailItem(
//                            railExpanded = expanded.targetValue == WideNavigationRailValue.Expanded,
//                            selected = screen == tab,
//                            onClick = { screen = tab },
//                            icon = { Icon(tab.icon, tab.title) },
//                            label = { Text(tab.title) },
////                        shape = RectangleShape,
////                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
//                        )
//                    }
//                }
                PermanentDrawerSheet(
                    modifier = Modifier.optional(ui.isSmall) { fillMaxWidth() }.padding(UI.padding.md),
                ) {
                    SettingsTab.tabs.forEach { tab ->
                        NavigationDrawerItem(
                            selected = screen == tab,
                            onClick = {
                                screen = tab
                                if (ui.isSmall) scope.launch { expanded.collapse() }
                            },
                            icon = { Icon(tab.icon, tab.title) },
                            label = { Text(tab.title) },
//                            shape = RectangleShape,
                            colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                        )
                    }
                }
            }
//        }
//    ) {
            AnimatedVisibility(
                !ui.isSmall || expanded.targetValue == WideNavigationRailValue.Collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    if (ui.isSmall) Spacer(Modifier.height(32.dp))
//                Text(screen.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    when (screen) {
                        SettingsTab.Theme -> SettingsThemeTab()
                        SettingsTab.Sync -> SettingsSyncTab()
                        SettingsTab.BulkAdd -> SettingsBulkAddTab()
                    }
                }
            }
        }
    }
}
