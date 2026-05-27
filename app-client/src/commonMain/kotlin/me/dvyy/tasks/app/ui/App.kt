package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.PlatformSpecifics
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.components.FloatingSurface
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.utils.LocalDragAndDropState
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.viewmodel.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    contentModifier: Modifier = Modifier,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = { AppTopBar(it) },
    extras: @Composable () -> Unit = { },
) = AppTheme {
    val ui = rememberAppUIState()
    val tasksViewModel by rememberViewModel<TasksViewModel>()

    CompositionLocalProvider(
        LocalUIState provides ui,
        LocalDragAndDropState provides rememberDragAndDropState(
            dragAfterLongPress = PlatformSpecifics.preferLongPressDrag
        ),
    ) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val isTabOverviewOpen = navBackStackEntry?.destination?.route == AppDest.TabSwitcher::class.qualifiedName

        DragAndDropContainer(LocalDragAndDropState.current) {
            rememberViewModel<SyncViewModel>().value // Ensure sync inits at start
            Scaffold(
                floatingActionButtonPosition = FabPosition.Center,
                snackbarHost = {
                    val host by rememberInstance<SnackbarHostState>()
                    SnackbarHost(host)
                },
                floatingActionButton = {
                    if (UI.isSmall) FloatingSurface {
                        TaskActionsToolbar(
                            isTabOverviewOpen = isTabOverviewOpen,
                            onNavigateToTabSwitcher = { navController.navigate(AppDest.TabSwitcher) },
                            onBack = { navController.popBackStack() },
                            onNavigate = { navController.navigate(it) }
                        )

                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv1),
                modifier = contentModifier.fillMaxSize()
            ) { paddingValues ->
                AppDrawer(navController) {
                    Box(Modifier.padding(paddingValues).clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                        AppNavigation(navController, topBar) // Integration point for the NavHost
//                        Surface(
//                            Modifier.align(Alignment.TopEnd),
//                            tonalElevation = ui.elevation.lv1,
//                        ) {
//                            PlatformSpecificTopBarActions()
//                        }
                    }
                }
            }
        }
    }
    extras()
}
