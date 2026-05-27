package me.dvyy.tasks.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Home
import dev.seyfarth.tablericons.outlined.LayoutSidebarLeftCollapse
import dev.seyfarth.tablericons.outlined.LayoutSidebarLeftExpand
import dev.seyfarth.tablericons.outlined.SquareNumber1
import dev.seyfarth.tablericons.outlined.SquarePlus
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.elements.NavigationButtons
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import org.kodein.di.compose.rememberInstance

//TODO test out vs inline toolbar on touchscreen
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskActionsToolbar(
    modifier: Modifier = Modifier,
    isTabOverviewOpen: Boolean = false,
    onBack: () -> Unit,
    onNavigateToTabSwitcher: () -> Unit,
    onNavigate: (Any) -> Unit,
) {
    val app: AppState by rememberInstance()
    var expanded by remember { mutableStateOf(true) }
    val layout: LayoutViewModel by rememberGlobalViewModel()
//    val selectedTask by tasksViewModel.selectedTask.collectAsState()
    val scope = rememberCoroutineScope()
    val isDrawerOpen = !UI.isSmall || (app.drawerState.targetValue == DrawerValue.Open)

    val showMiddleActions = !isTabOverviewOpen && !isDrawerOpen

    Row(modifier, horizontalArrangement = Arrangement.Center) {
        AnimatedVisibility(isDrawerOpen) {
//            FloatingSurface(Modifier.padding(end = UI.padding.sm)) {
            ButtonRow {
                BoxButton(
                    icon = AppIcons.LayoutSidebarLeftCollapse,
                    onClick = { scope.launch { app.drawerState.close() } },
                    tooltip = "Close Menu"
                )
                NavigationButtons(spacer = { TintedVerticalDivider(Modifier.height(UI.tabHeight * 0.5f)) }, onNavigate = onNavigate)
            }
//            }
        }
//        FloatingSurface {
        Row {
            AnimatedVisibility(showMiddleActions) {
                BoxButton(
                    icon = AppIcons.LayoutSidebarLeftExpand,
                    onClick = {
                        scope.launch {
                            if (app.drawerState.isOpen) app.drawerState.close() else app.drawerState.open()
                        }
                    },
                    tooltip = "Open Menu",
                    modifier = Modifier.padding(end = UI.padding.sm)
                )
            }
//            AnimatedVisibility(selectedTask != null) {
//                ButtonRow(horizontalPadding = 0.dp) {
//                    BoxButton(icon = Icons.Outlined.FormatBold, onClick = { expanded = !expanded }, contentDescription = "Format Bold")
//                    BoxButton(icon = Icons.Outlined.FormatItalic, onClick = { expanded = !expanded }, contentDescription = "Format Italic")
//                    BoxButton(icon = Icons.Outlined.FormatUnderlined, onClick = { expanded = !expanded }, contentDescription = "Format Underlined")
//                }
//            }
            AnimatedVisibility(showMiddleActions) {
                ButtonRow(horizontalPadding = 0.dp) {
                    BoxButton(
                        icon = Icons.Outlined.Search,
                        onClick = { expanded = !expanded },
                        tooltip = "Search"
                    )
                    BoxButton(
                        icon = TablerIcons.Outlined.SquareNumber1,
                        onClick = { scope.launch { onNavigateToTabSwitcher() } },
                        tooltip = "Tab Switcher"
                    )
                }
            }
        }
//        }
        AnimatedVisibility(isTabOverviewOpen) {
//            FloatingSurface(Modifier.padding(start = UI.padding.sm)) {
            ButtonRow {
//                    onOpenNewTab = {
//                        val index = layoutViewModel.openTab(LayoutStructure.Empty)
//                        layoutViewModel.switchTab(index)
//                        navController.popBackStack()
//                    }
                BoxButton(
                    icon = TablerIcons.Outlined.SquarePlus,
                    onClick = { layout.openTab(LayoutDefinition.Empty) },
                    tooltip = "New tab",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                BoxButton(
                    icon = TablerIcons.Outlined.Home,
                    onClick = { scope.launch { onBack() } },
                    tooltip = "Home"
                )
            }
//            }
        }
    }
}