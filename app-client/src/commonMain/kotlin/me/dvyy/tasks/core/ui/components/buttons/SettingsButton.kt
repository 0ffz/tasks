package me.dvyy.tasks.core.ui.components.buttons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Settings
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton

@Composable
fun SettingsButton(
    navigateToSettings: () -> Unit,
) {
    BoxButton(onClick = navigateToSettings) {
        Icon(TablerIcons.Outlined.Settings, contentDescription = "Settings")
    }
}
