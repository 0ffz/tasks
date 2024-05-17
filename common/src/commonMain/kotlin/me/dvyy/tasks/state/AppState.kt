package me.dvyy.tasks.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import me.dvyy.tasks.data.Auth
import me.dvyy.tasks.data.SyncClient

@Stable
class AppState {
    val time = TimeState()

    val auth = Auth()

    val sync = SyncClient("http://localhost:4000", this, Dispatchers.Default)
    val snackbarHostState = SnackbarHostState()
    val activeDialog = MutableStateFlow<AppDialog?>(null)
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)
}

@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}
