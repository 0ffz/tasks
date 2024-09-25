package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.components.ResponsiveNavigationDrawer

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
fun SettingsScreen() {
    var screen by remember { mutableStateOf<SettingsTab>(SettingsTab.Sync) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ResponsiveNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceDim,
            ) {
                SettingsTab.tabs.forEach { tab ->
                    NavigationDrawerItem(
                        selected = screen == tab,
                        onClick = { screen = tab },
                        icon = { Icon(tab.icon, tab.title) },
                        label = { Text(tab.title) },
                        shape = RectangleShape,
                    )
                }
            }
        }
    ) {
        val ui = LocalUIState.current
        if (ui.isSingleColumn) {
            val scope = rememberCoroutineScope()
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Open menu")
            }
        }

        Column(Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
            if (ui.isSingleColumn) Spacer(Modifier.height(32.dp))
            Text(screen.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            when (screen) {
                SettingsTab.Theme -> SettingsThemeTab()
                SettingsTab.Sync -> SettingsSyncTab()
                SettingsTab.BulkAdd -> SettingsBulkAddTab()
            }
        }
    }
}
