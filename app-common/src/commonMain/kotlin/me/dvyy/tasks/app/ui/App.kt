package me.dvyy.tasks.app.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.dialogs.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.di.*
import me.dvyy.tasks.layout.ui.ViewStructure
import me.dvyy.tasks.layout.ui.Views
import me.dvyy.tasks.tasks.ui.TasksViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = { AppTopBar(it) },
    extras: @Composable () -> Unit = { },
) {
    AppTheme {
        val responsive = rememberAppUIState()
        val tasksViewModel = koinViewModel<TasksViewModel>()
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
                    Box(
                        Modifier.padding(paddingValues)
                            .clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                        Row {
                            Surface(
                                Modifier.fillMaxHeight().width(responsive.sideBarWidth),
                                tonalElevation = 2.dp,
                            ) {
                                Column(Modifier.padding(responsive.sideBarPadding)) {
                                    IconToggleButton(checked = firstEnabled, onCheckedChange = { firstEnabled = !firstEnabled }) {
                                        Icon(Icons.Rounded.FolderOpen, "File tree")
                                    }
                                }
                            }
                            Views(
                                ViewStructure.Split(
                                    first = ViewStructure.Scrollable(
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
                                    second = ViewStructure.Single { WeekView(scrollBehavior) },
                                    orientation = Orientation.Horizontal,
                                    firstEnabled = firstEnabled
                                )
                            )
                        }
                    }
                }
                AppDialogs()
            }
        }
        extras()
    }
}

