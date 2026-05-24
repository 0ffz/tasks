package me.dvyy.tasks.app.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import me.dvyy.tasks.app.ui.elements.LeftNavigationRail
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.AppFileTree
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.layout.ui.layouts.Layout
import me.dvyy.tasks.layout.ui.layouts.Split
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.settings.ui.SettingsScreen
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import org.kodein.di.compose.viewmodel.rememberViewModel

@Serializable
data object Home

@Serializable
object TabSwitcher

@Serializable
data object Settings

@Serializable
sealed interface AppDialog {
    @Serializable
    data object Auth : AppDialog

    @Serializable
    data class ConfirmDeleteProject(val key: ListId) : AppDialog
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
) {
    val layoutViewModel: LayoutViewModel by rememberViewModel()
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            val tab by layoutViewModel.activeTab.collectAsState()
//            if (UI.isSmall) {
////                val structure by layoutViewModel.mobileLayout.collectAsState(LayoutStructure.Remove)
//                Row {
//                    Layout(tab, onLayoutUpdate = { new ->
//                        layoutViewModel.setTab(tabId, new)
////                        val main = (new as LayoutStructure.Split).first
////                        layoutViewModel.setMainView(main)
//                    })
//                }
//            } else {
//                val structure by layoutViewModel.desktopLayout.collectAsState(LayoutStructure.Remove)
            Row {
                if (!UI.isSmall) {
                    LeftNavigationRail(onNavigate = { navController.navigate(it) })
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
                    second = {
                        Layout(tab, onLayoutUpdate = { new ->
                            layoutViewModel.replaceTab(new)
                        })
                    }
                )
            }
//            }
        }
        composable<TabSwitcher> {
            val active by layoutViewModel.selectedTab.collectAsState()
            val tabs by layoutViewModel.tabs.collectAsState()
            TabSwitcherScreen(
                tabs = tabs,
                activeTabIndex = active,
                onNavigateToTab = {
                    layoutViewModel.switchTab(it)
                    navController.popBackStack()
                }, onOpenNewTab = {
                    val index = layoutViewModel.openTab(LayoutStructure.Empty)
                    layoutViewModel.switchTab(index)
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