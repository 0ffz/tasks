package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Plus
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.LayoutTab
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow

@Composable
fun AppTabBar(modifier: Modifier = Modifier) {
    val layout by rememberGlobalViewModel<LayoutViewModel>()
    val tabs by layout.tabs.collectAsStateWithLifecycle()
    val selectedTab by layout.selectedTab.collectAsStateWithLifecycle()

    Row(modifier) {
        BoxWithConstraints(Modifier.weight(1f, fill = false)) {
            val tabWidth = (maxWidth / tabs.size.coerceAtLeast(1)).coerceAtMost(256.dp)
            ButtonRow(horizontalPadding = 0.dp) {
                tabs.forEachIndexed { index, definition ->
                    definition.operations
                    var visible by remember { mutableStateOf(false) }
                    val selected = index == selectedTab
                    Surface(
                        Modifier
                            .height(UI.tabHeight)
                            .width(tabWidth)
                            .onHoverIfAvailable(onEnter = { visible = true }, onExit = { visible = false }),
                        tonalElevation = UI.elevation.lv1
                    ) {
                        LayoutTab(selected = selected, definition, onClick = { layout.switchTab(index) }, trailingOptions = {
                            if (visible || selected) BoxButton(TablerIcons.Outlined.X, onClick = {
                                layout.closeTab(index)
                            }, tooltip = "Close tab")
                        })
                    }
                }
            }
        }
        BoxButton(
            TablerIcons.Outlined.Plus,
            onClick = { layout.openTab(LayoutDefinition.Empty) },
            tooltip = "Add tab"
        )
    }
}