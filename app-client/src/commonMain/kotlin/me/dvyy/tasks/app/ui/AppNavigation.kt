package me.dvyy.tasks.app.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.AppFileTree
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.layout.ui.layouts.CalculateLayout
import me.dvyy.tasks.layout.ui.layouts.Split
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.settings.ui.SettingsScreen
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import org.kodein.di.compose.viewmodel.rememberViewModel

@Serializable
data object Home

@Serializable
object TabSwitcher

@Serializable
data object Settings

@Serializable
data class ConfirmDeleteProject(val key: ListId)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit,
) {
    val layoutViewModel: LayoutViewModel by rememberGlobalViewModel()
    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable<Home> {
            Row {
                if (!UI.isSmall) {
//                    LeftNavigationRail(onNavigate = { navController.navigate(it) })
                    TintedVerticalDivider(Modifier.padding(top = UI.tabHeight))
                }

                var splitAmount: SplitAmount by remember { mutableStateOf(SplitAmount.Fixed(200.dp)) }
                Split(
                    splitAmount,
                    onSplitAmountChange = { splitAmount = it },
                    orientation = Orientation.Horizontal,
                    first = {
                        Surface(
                            tonalElevation = UI.elevation.lv1
                        ) { AppFileTree() }
                    },
                    firstEnabled = !UI.isSmall,
                    second = {
                        Column {
                            val scrollBehavior = if (UI.isSmall)
                                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                            else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                            val layoutViewModel: LayoutViewModel by rememberGlobalViewModel()
//                            val list = LayoutDefinition.Empty.replace(
//                                0,
//                                LayoutOperation.Split(SplitAmount.Percent(0.5f), Orientation.Vertical),
//                                ScreenDest.Week(),
//                                ScreenDest.Projects(horizontal = true, staggered = false)
//                            )
                            val list by layoutViewModel.activeTab.collectAsStateWithLifecycle()
                            topBar(scrollBehavior)
                            CalculateLayout(0, list, onLayoutChange = { layoutViewModel.replaceTab(it) }) //TODO on layout change
                        }
                    }
                )
            }
        }
        composable<TabSwitcher> {
            val active by layoutViewModel.selectedTab.collectAsStateWithLifecycle()
            val tabs by layoutViewModel.tabs.collectAsStateWithLifecycle()
            TabSwitcherScreen(
                tabs = tabs,
                selectedTab = active,
                onNavigateToTab = {
                    layoutViewModel.switchTab(it)
                    navController.popBackStack()
                }, onCloseTab = {
                    layoutViewModel.closeTab(it)
                }
            )
        }
        dialog<Settings>(dialogProperties = DialogProperties(usePlatformDefaultWidth = false)) {
            val ui = UI
            Box(Modifier.fillMaxSize().clickableWithoutRipple {
                navController.popBackStack()
            }, contentAlignment = Alignment.Center) {
                val padding = if (ui.isSmall) 0.dp else 32.dp
                Surface(
                    Modifier
                        .widthIn(max = 1280.dp)
                        .optional(!ui.isSmall) { heightIn(max = 1200.dp) }
                        .optional(ui.isSmall) { padding(top = UI.tabHeight * 0.75f) }
                        .clickableWithoutRipple {} // consume clicks
                        .fillMaxSize().padding(padding),
                    shape = ui.shapes.roundedExtra,
                    shadowElevation = 1.dp
                ) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteProjectDialog(
    key: ListId,
    onDismiss: () -> Unit,
) {
    val tasks: TasksViewModel by rememberViewModel()
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(AppIcons.Delete, contentDescription = "Delete") },
        title = { Text("Delete project") },
        text = { Text("This will delete the projects and any tasks in it. Are you sure?") },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                tasks.deleteProject(key.uuid)
            }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        },
        modifier = Modifier.imePadding()
    )
}
