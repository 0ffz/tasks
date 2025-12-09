package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.ui.dialogs.AppDialogs
import me.dvyy.tasks.app.ui.dialogs.AppScreens
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.elements.LeftNavigationRail
import me.dvyy.tasks.app.ui.elements.PlatformSpecificTopBarActions
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    contentModifier: Modifier = Modifier,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = { AppTopBar(it) },
    extras: @Composable () -> Unit = { },
) = AppTheme {
    val ui = rememberAppUIState()
    val tasksViewModel = koinViewModel<TasksViewModel>()
    val layoutViewModel = koinViewModel<LayoutViewModel>()
    CompositionLocalProvider(
        LocalUIState provides ui,
    ) {
        koinViewModel<SyncViewModel>() // Ensure sync inits at start
        val scrollBehavior = if (ui.isSmall)
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        AppDrawer {
            Scaffold(
                topBar = { topBar(scrollBehavior) },
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv1),
                modifier = contentModifier.fillMaxSize()
            ) { paddingValues ->
                Box(
                    Modifier
                        .padding(paddingValues)
                        .clickableWithoutRipple { tasksViewModel.selectTask(null) }
                ) {
                    if (ui.isSmall) {
                        val structure by layoutViewModel.mobileLayout.collectAsState(LayoutStructure.Empty)
                        Row {
                            Layout(structure, onLayoutUpdate = { new ->
                                val main = (new as LayoutStructure.Split).first
                                layoutViewModel.setMainView(main)
                            })
                        }
                    } else {
                        val structure by layoutViewModel.desktopLayout.collectAsState(LayoutStructure.Empty)
                        Row {
                            LeftNavigationRail()
                            TintedVerticalDivider(Modifier.padding(top = UI.tabHeight))
                            Layout(structure, onLayoutUpdate = {
                                val main = ((it as? LayoutStructure.Split)?.first as? LayoutStructure.Split)?.second
                                if (main != null) layoutViewModel.setMainView(main)
                            })
                        }
                    }
                    Surface(
                        Modifier.align(Alignment.TopEnd),
                        tonalElevation = ui.elevation.lv1,
                    ) {
                        PlatformSpecificTopBarActions()
                    }
                }
            }
            AppScreens()
            AppDialogs()
        }
    }
    extras()
}
