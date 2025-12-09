package me.dvyy.tasks.app.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import org.koin.compose.koinInject

@Composable
fun AppDrawerButton(
    app: AppState = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    WindowButton(Icons.Rounded.Menu) {
        coroutineScope.launch { app.drawerState.open() }
    }
}
