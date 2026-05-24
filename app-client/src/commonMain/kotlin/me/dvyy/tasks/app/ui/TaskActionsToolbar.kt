package me.dvyy.tasks.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Search
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
import dev.seyfarth.tablericons.outlined.Menu2
import dev.seyfarth.tablericons.outlined.SquareNumber1
import kotlinx.coroutines.launch
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
) {
    val tasksViewModel: TasksViewModel by rememberViewModel()
    val app: AppState by rememberInstance()
    var expanded by remember { mutableStateOf(true) }
    val selectedTask by tasksViewModel.selectedTask.collectAsState()
    val scope = rememberCoroutineScope()
    Surface(shape = UI.shapes.roundedExtra, tonalElevation = UI.elevation.lv2, border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv3))) {
        ButtonRow {
            BoxButton(onClick = { scope.launch { app.drawerState.open() } }) {
                Icon(TablerIcons.Outlined.Menu2, contentDescription = "Add task")
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
            BoxButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Outlined.Search, contentDescription = "Add task")
            }
            BoxButton(onClick = { scope.launch { onNavigateToTabSwitcher() } }) {
                Icon(TablerIcons.Outlined.SquareNumber1, contentDescription = "Add task")
            }
        }
    }
}