package me.dvyy.tasks.app.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.dialogs.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.di.*
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.ViewButton
import me.dvyy.tasks.layout.ui.ViewStructure
import me.dvyy.tasks.layout.ui.Views
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.ProjectListContent
import me.dvyy.tasks.tasks.ui.elements.list.WeekView
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun createAppKoinApplication(extras: KoinAppDeclaration = {}) = koinApplication {
    extras()
    modules(
        appModule(),
        repositoriesModule(),
        authModule(),
        syncModule(),
        viewModelsModule(),
    )
}


object AppViewButtons {
    val projects = ViewButton(
        "Projects", "projects",
        ViewStructure.Tabbed(
            name = "Projects",
            tabs = listOf(
                ViewStructure.Tab(
                    "All",
                    ViewStructure.Single {
                        ProjectListContent(modifier = Modifier.fillMaxHeight())
                    }
                )
            ),
            selected = 0,
        ),
        icon = Icons.AutoMirrored.Outlined.ViewList
    )

    val fileTree = ViewButton(
        "File tree", "file_tree",
        ViewStructure.Scrollable(
            (1..10).map {
                ViewStructure.Single {
                    FileList(
                        listOf(
                            FileStructure.File("File 1"),
                            FileStructure.File("File 2")
                        )
                    )
                }
            },
            orientation = Orientation.Vertical
        ),
        icon = Icons.Rounded.FolderOpen
    )
}

@Composable
fun ViewButton(
    button: ViewButton,
    enabled: Boolean,
    onClick: (Boolean) -> Unit = {},
) {
    IconToggleButton(checked = enabled, onCheckedChange = onClick) {
        Icon(button.icon, button.displayName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = { AppTopBar(it) },
    extras: @Composable () -> Unit = { },
) {
    AppTheme {
        val responsive = rememberAppUIState()
        val tasksViewModel = koinViewModel<TasksViewModel>()
        val layoutViewModel = koinViewModel<LayoutViewModel>()
        CompositionLocalProvider(
            LocalUIState provides responsive,
        ) {
            val scrollBehavior = if (responsive.isSingleColumn)
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
            else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            AppDrawer {
                Scaffold(
                    topBar = { topBar(scrollBehavior) },
                ) { paddingValues ->
                    var firstEnabled by remember { mutableStateOf(true) }
                    var projectsEnabled by remember { mutableStateOf(true) }
                    val reorderInteractions = tasksViewModel.reorderInteractions()
                    ReorderContainer(state = reorderInteractions.draggedState) {
                        Box(
                            Modifier.padding(paddingValues)
                                .clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                            Row {
                                Surface(
                                    Modifier.fillMaxHeight().width(responsive.sideBarWidth),
                                    tonalElevation = 2.dp,
                                ) {
                                    Column(Modifier.padding(responsive.sideBarPadding).fillMaxHeight()) {
                                        val buttons by layoutViewModel.viewButtons.collectAsState()
                                        buttons.left.forEach { button ->
                                            ViewButton(button, firstEnabled) {
                                                firstEnabled = it
                                                layoutViewModel.leftSidebar.update {
                                                    if (it == button.structure) ViewStructure.Empty
                                                    else button.structure
                                                }
                                            }
                                        }
                                        Spacer(Modifier.weight(1f))
                                        buttons.bottom.forEach { button ->
                                            ViewButton(button, firstEnabled) {
                                                firstEnabled = it
                                                layoutViewModel.bottomBar.update {
                                                    if (it == button.structure) ViewStructure.Empty
                                                    else button.structure
                                                }
                                            }
                                        }
                                    }
                                }
                                val structure by layoutViewModel.structure.collectAsState(ViewStructure.Empty)
                                LaunchedEffect(Unit) {
                                    layoutViewModel.mainView.update {
                                        ViewStructure.Single { WeekView(scrollBehavior) }
                                    }
                                }
                                Views(structure)
                            }
                        }
                    }
                }
                AppDialogs()
            }
        }
        extras()
    }
}

