package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Plus
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.LayoutTab
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton

@Composable
fun AppTabBar(modifier: Modifier = Modifier) {
    val layout by rememberGlobalViewModel<LayoutViewModel>()
    val tabs by layout.tabs.collectAsState()

    Row(modifier) {
        BoxWithConstraints(Modifier.weight(1f, fill = false)) {
            val tabWidth = (maxWidth / tabs.size.coerceAtLeast(1)).coerceAtMost(256.dp)
            Row {
                tabs.forEachIndexed { index, definition ->
                    definition.operations
                    Surface(Modifier.height(UI.tabHeight).width(tabWidth), tonalElevation = UI.elevation.lv1) {
                        LayoutTab(selected = false, definition, trailingOptions = {
                            BoxButton(onClick = {
                                layout.closeTab(index)
                            }) {
                                Icon(TablerIcons.Outlined.X, "Close tab", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        })
                    }
                }
            }
        }
        BoxButton(onClick = {
            layout.openTab(LayoutDefinition.Empty)
        }) {
            Icon(TablerIcons.Outlined.Plus, "Add tab", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}