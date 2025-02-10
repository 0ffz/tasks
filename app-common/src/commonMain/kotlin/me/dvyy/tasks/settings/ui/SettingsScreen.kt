package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.alorma.compose.settings.ui.base.internal.LocalSettingsTileColors
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.ResponsiveNavigationDrawer
import me.dvyy.tasks.core.ui.fade

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

    companion object {
        val tabs = listOf(Theme, BulkAdd)
    }
}

@Composable
fun SettingsScreen() {
    var screen by remember { mutableStateOf<SettingsTab>(SettingsTab.Theme) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ResponsiveNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = UI.elevation.lv1,
            ) {
                SettingsTab.tabs.forEach { tab ->
                    NavigationDrawerItem(
                        selected = screen == tab,
                        onClick = { screen = tab },
                        icon = { Icon(tab.icon, tab.title) },
                        label = { Text(tab.title) },
                        shape = RectangleShape,
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    ) {
        val ui = LocalUIState.current
        if (ui.isSmall) {
            val scope = rememberCoroutineScope()
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Open menu")
            }
        }

        Column(Modifier/*.padding(32.dp)*/.verticalScroll(rememberScrollState())) {
            /*if (ui.isSmall) */Spacer(Modifier.height(32.dp))
//            Text(screen.title, style = MaterialTheme.typography.headlineMedium)
//            Spacer(Modifier.height(16.dp))
            CompositionLocalProvider(
                LocalSettingsTileColors provides SettingsTileDefaults.colors(
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    subtitleColor = MaterialTheme.colorScheme.onSurface.fade(0.75f),
                )
            ) {
                when (screen) {
                    SettingsTab.Theme -> SettingsThemeTab()
                    SettingsTab.BulkAdd -> SettingsBulkAddTab()
                }
            }
        }
    }
}
