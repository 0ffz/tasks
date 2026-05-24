package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.Settings
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.buttons.SettingsButton
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.sync.ui.SyncIndicator
import me.dvyy.tasks.sync.ui.UpdateIndicator
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonColumn
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun TopBarContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) = PlatformTopBarContainer(
    Modifier,
    {
        Column(modifier) {
            Box(Modifier.height(UI.tabHeight)) {
                content()
            }
        }
    })

@Composable
fun LeftNavigationRail(
    onNavigate: (Any) -> Unit,
) {
    val layout: LayoutViewModel by rememberViewModel()
    Surface(
        Modifier.fillMaxHeight().width(UI.sideBarWidth),
        tonalElevation = UI.elevation.lv1,
    ) {
        Column(
            Modifier/*.padding(UI.sideBarPadding)*/.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarContainer {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    AppIcon(Modifier.size(28.dp))
                }
            }
            ButtonColumn {
                NavigationButtons(spacer = { Spacer(Modifier.weight(1f)) }, onNavigate)
            }
            //            ButtonColumn {
//                buttons.bottom.forEach { button ->
//                    val isSelected = button.structure == bottomSelected
//                    LayoutToggleButton(button, isSelected) {
//                        layout.bottomBar.update {
//                            if (isSelected) LayoutStructure.Remove
//                            else button.structure
//                        }
//                    }
//                }
//            }
        }
    }
}

@Composable
fun NavigationButtons(
    spacer: @Composable () -> Unit,
    onNavigate: (Any) -> Unit,
) {
    val layout: LayoutViewModel by rememberViewModel()
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
    spacer()
    UpdateIndicator()
    SyncIndicator()
    SettingsButton(navigateToSettings = { onNavigate(Settings) })

}