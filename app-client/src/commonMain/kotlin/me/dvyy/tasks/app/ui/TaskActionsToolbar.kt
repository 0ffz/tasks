package me.dvyy.tasks.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ChevronRight
import dev.seyfarth.tablericons.outlined.Menu2
import dev.seyfarth.tablericons.outlined.SquareNumber1
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.elements.NavigationButtons
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
    onNavigateToTabSwitcher: () -> Unit,
    onNavigate: (Any) -> Unit,
) {
    val tasksViewModel: TasksViewModel by rememberViewModel()
    val app: AppState by rememberInstance()
    var expanded by remember { mutableStateOf(true) }
    val selectedTask by tasksViewModel.selectedTask.collectAsState()
    val scope = rememberCoroutineScope()

    val isDrawerOpen = (app.drawerState.targetValue == DrawerValue.Open)

    Surface(shape = UI.shapes.roundedExtra, tonalElevation = UI.elevation.lv2, border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv3))) {
        ButtonRow(spacedBy = 0.dp) {
            AnimatedVisibility(!isDrawerOpen) {
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
            AnimatedVisibility(isDrawerOpen) {
                ButtonRow(horizontalPadding = 0.dp) {
                    NavigationButtons(spacer = { TintedVerticalDivider(Modifier.height(UI.tabHeight * 0.5f)) }, onNavigate = onNavigate)
                }
            }
            AnimatedVisibility(selectedTask != null) {
                ButtonRow(horizontalPadding = 0.dp) {
                    BoxButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Outlined.FormatBold, contentDescription = "Add task")
                    }
                    BoxButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Outlined.FormatItalic, contentDescription = "Add task")
                    }
                    BoxButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Outlined.FormatUnderlined, contentDescription = "Add task")
                    }
                }
            }
            AnimatedVisibility(!isDrawerOpen) {
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
                BoxButton(onClick = { scope.launch { app.drawerState.close() } }, modifier = Modifier.padding(start = UI.padding.sm)) {
                    Icon(TablerIcons.Outlined.ChevronRight, contentDescription = "Close Menu")
                }
            }
        }
    }
}