package me.dvyy.tasks.core.ui.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import me.dvyy.tasks.app.ui.LocalUIState

@Composable
fun ResponsiveNavigationDrawer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val ui = LocalUIState.current
    if (ui.isSingleColumn) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { drawerContent() }
        ) {
            content()
        }
    } else {
        PermanentNavigationDrawer(
            drawerContent = { drawerContent() }
        ) {
            content()
        }
    }
}
