package me.dvyy.tasks.app.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import me.dvyy.tasks.settings.ui.SettingsScreen
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.ProjectListContent
import me.dvyy.tasks.tasks.ui.elements.list.WeekView
import me.dvyy.tasks.tasks.ui.elements.list.thenOptional
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
                    bottomBar = {
                        Box(Modifier.navigationBarsPadding()) {
                            if (responsive.isSingleColumn) BottomNavigationBar()
                        }
                    }
                ) { paddingValues ->
                    val reorderInteractions = tasksViewModel.reorderInteractions()
                    ReorderContainer(state = reorderInteractions.draggedState) {
                        Box(
                            Modifier.padding(paddingValues)
                                .clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                            Row {
                                LaunchedEffect(Unit) {
                                    layoutViewModel.mainView.update {
                                        ViewStructure.Single { WeekView() }
                                    }
                                }
                                if (responsive.isSingleColumn) {
                                    val structure by layoutViewModel.mobileLayout.collectAsState(ViewStructure.Empty)
                                    Views(structure)
                                } else {
                                    val structure by layoutViewModel.desktopLayout.collectAsState(ViewStructure.Empty)
                                    LeftNavigationRail()
                                    Views(structure)
                                }
                            }
                        }
                    }
                }
                AppScreens()
                AppDialogs()
            }
        }
        extras()
    }
}

@Composable
private fun Screens(
    app: DialogViewModel = koinViewModel()
) {
    val screenState by app.screen.collectAsState()
    val screen = screenState ?: return
    Box {
        when (screen) {
            AppScreen.Settings -> SettingsScreen()
        }
        IconButton(
            onClick = { app.screen.update { null } },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Rounded.Close, "Close")
        }
    }
}

@Composable
fun AppScreens(app: DialogViewModel = koinViewModel()) {
    val ui = LocalUIState.current
    val screenState by app.screen.collectAsState()
    screenState ?: return

    if (ui.isSingleColumn) Surface {
        Box(Modifier.systemBarsPadding()) {
            Screens()
        }
    } else Dialog(
        onDismissRequest = { app.screen.update { null } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize().clickableWithoutRipple { app.screen.update { null } })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val padding = if (ui.isSingleColumn) 0.dp else 32.dp
            Surface(
                Modifier
                    .widthIn(max = 1600.dp)
                    .thenOptional(!ui.isSingleColumn) { heightIn(max = 1200.dp) }
                    .fillMaxSize().padding(padding),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp
            ) {
                Screens()
            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    layout: LayoutViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    Surface(
        Modifier.height(ui.sideBarWidth).fillMaxWidth(),
        tonalElevation = 2.dp,
    ) {
        val buttons by layout.viewButtons.collectAsState()
        val selected by layout.bottomBar.collectAsState()
        buttons.bottom.forEach { button ->
            val isSelected = button.structure == selected
            ViewButton(button, isSelected) {
                layout.bottomBar.update {
                    if (isSelected) ViewStructure.Empty
                    else button.structure
                }
            }
        }
    }
}

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
            val buttons by layout.viewButtons.collectAsState()
            val selected by layout.leftSidebar.collectAsState()
            buttons.left.forEach { button ->
                val isSelected = button.structure == selected
                ViewButton(button, isSelected) {
                    layout.leftSidebar.update {
                        if (isSelected) ViewStructure.Empty
                        else button.structure
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            val bottomSelected by layout.bottomBar.collectAsState()
            buttons.bottom.forEach { button ->
                val isSelected = button.structure == bottomSelected
                ViewButton(button, isSelected) {
                    layout.bottomBar.update {
                        if (isSelected) ViewStructure.Empty
                        else button.structure
                    }
                }
            }
        }
    }
}
