package me.dvyy.tasks.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.ChevronRight
import dev.seyfarth.tablericons.outlined.Menu2
import dev.seyfarth.tablericons.outlined.SquareNumber1
import dev.seyfarth.tablericons.outlined.SquarePlus
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.elements.NavigationButtons
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.viewmodel.rememberViewModel

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

    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        AnimatedVisibility(isDrawerOpen) {
//            FloatingSurface(Modifier.padding(end = UI.padding.sm)) {
            ButtonRow {
                NavigationButtons(spacer = { TintedVerticalDivider(Modifier.height(UI.tabHeight * 0.5f)) }, onNavigate = onNavigate)
            }
//            }
        }
//        FloatingSurface {
        ButtonRow(spacedBy = 0.dp) {
            AnimatedVisibility(showMiddleActions) {
                BoxButton(onClick = {
                    scope.launch {
                        if (app.drawerState.isOpen) app.drawerState.close() else app.drawerState.open()
                    }
                }, modifier = Modifier.padding(end = UI.padding.sm)) {
//                Box(modifier = Modifier.rotate(rotation)) {
//                    if (rotation > 90f) {
//                    } else {
                    Icon(TablerIcons.Outlined.Menu2, contentDescription = "Open Menu")
//                    }
                }
            }
//            AnimatedVisibility(selectedTask != null) {
//                ButtonRow(horizontalPadding = 0.dp) {
//                    BoxButton(onClick = { expanded = !expanded }) {
//                        Icon(Icons.Outlined.FormatBold, contentDescription = "Add task")
//                    }
//                    BoxButton(onClick = { expanded = !expanded }) {
//                        Icon(Icons.Outlined.FormatItalic, contentDescription = "Add task")
//                    }
//                    BoxButton(onClick = { expanded = !expanded }) {
//                        Icon(Icons.Outlined.FormatUnderlined, contentDescription = "Add task")
//                    }
//                }
//            }
            AnimatedVisibility(showMiddleActions) {
                ButtonRow(horizontalPadding = 0.dp) {
                    BoxButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Add task")
                    }
                    BoxButton(onClick = { scope.launch { onNavigateToTabSwitcher() } }) {
                        Icon(TablerIcons.Outlined.SquareNumber1, contentDescription = "Add task")
                    }
                }
            }

            AnimatedVisibility(isDrawerOpen) {
                BoxButton(onClick = { scope.launch { app.drawerState.close() } }) {
                    Icon(TablerIcons.Outlined.ChevronRight, contentDescription = "Close Menu")
                }
            }
            AnimatedVisibility(isTabOverviewOpen) {
                BoxButton(onClick = { scope.launch { onBack() } }) {
                    Icon(TablerIcons.Outlined.ChevronLeft, contentDescription = "Close Menu")
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
                BoxButton(onClick = { layout.openTab(LayoutDefinition.Empty) }, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(TablerIcons.Outlined.SquarePlus, contentDescription = "New tab")
                }
            }
//            }
        }
    }
}