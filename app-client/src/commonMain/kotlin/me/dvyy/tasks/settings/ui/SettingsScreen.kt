package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.dialogs.ScreenContainer

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

    data object Update : SettingsTab {
        override val title = "Update"
        override val icon = Icons.Outlined.Update
    }

    data object Logs : SettingsTab {
        override val title = "Logs"
        override val icon = Icons.AutoMirrored.Outlined.List
    }

    companion object {
        val tabs = listOf(Sync, Theme, BulkAdd, Update, Logs)
    }
}


@Composable
fun RowOrBox(isRow: Boolean, content: @Composable () -> Unit) {
    if (isRow) Row(Modifier.fillMaxWidth()) { content() }
    else Box(Modifier.fillMaxWidth()) { content() }
}

@Composable
fun SettingsScreen(screen: SettingsTab, onChangeTab: (SettingsTab) -> Unit) {
    val ui = LocalUIState.current
    val scope = rememberCoroutineScope()
    ScreenContainer(screen.title, utilityPane = { setExpanded ->
        SettingsTab.tabs.forEach { tab ->
            NavigationDrawerItem(
                selected = screen == tab && !ui.isSmall,
                onClick = {
                    onChangeTab(tab)
                    if (ui.isSmall) scope.launch { setExpanded(false) }
                },
                icon = { Icon(tab.icon, tab.title) },
                modifier = Modifier.height(ui.tabHeight),
                label = { Text(tab.title) },
                shape = RectangleShape,
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    }, utilityPaneText = "Settings") {
        val scrollState = rememberScrollState()
        val modifier = Modifier.padding(horizontal = 16.dp).let {
            if (screen != SettingsTab.Logs) it.verticalScroll(scrollState) else it
        }
        Column(modifier, verticalArrangement = Arrangement.spacedBy(UI.padding.md)) {
            when (screen) {
                SettingsTab.Theme -> SettingsThemeTab()
                SettingsTab.Sync -> SettingsSyncTab()
                SettingsTab.BulkAdd -> SettingsBulkAddTab()
                SettingsTab.Update -> SettingsUpdateTab()
                SettingsTab.Logs -> LogsTab()
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
