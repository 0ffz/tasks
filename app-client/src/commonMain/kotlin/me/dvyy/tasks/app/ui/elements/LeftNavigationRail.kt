package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.buttons.SettingsButton
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.TintedHorizontalDivider
import me.dvyy.tasks.sync.ui.SyncIndicator
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeftNavigationRail(
    layout: LayoutViewModel = koinViewModel(),
) {
    Surface(
        Modifier.fillMaxHeight().width(UI.sideBarWidth),
        tonalElevation = UI.elevation.lv1,
    ) {
        Column(
            Modifier/*.padding(UI.sideBarPadding)*/.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(UI.tabHeight), contentAlignment = Alignment.Center) {
                AppIcon(Modifier.size(28.dp))
            }

            TintedHorizontalDivider()

            val buttons by layout.layoutButtonLocations.collectAsState()
            val selected by layout.leftSidebar.collectAsState()
            buttons.left.forEach { button ->
                val isSelected = button.structure == selected
                LayoutToggleButton(button, isSelected) {
                    layout.setLeftSidebar(
                        if (isSelected) LayoutStructure.Empty
                        else button.structure
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            val bottomSelected by layout.bottomBar.collectAsState()
            buttons.bottom.forEach { button ->
                val isSelected = button.structure == bottomSelected
                LayoutToggleButton(button, isSelected) {
                    layout.bottomBar.update {
                        if (isSelected) LayoutStructure.Empty
                        else button.structure
                    }
                }
            }
            SyncIndicator()
            SettingsButton()
        }
    }
}
