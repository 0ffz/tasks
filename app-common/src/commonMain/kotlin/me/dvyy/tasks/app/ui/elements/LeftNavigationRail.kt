package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeftNavigationRail(
    layout: LayoutViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current

    Surface(
        Modifier.fillMaxHeight().width(ui.sideBarWidth),
        tonalElevation = 2.dp,
    ) {
        Column(Modifier.padding(ui.sideBarPadding).fillMaxHeight()) {
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
        }
    }
}
