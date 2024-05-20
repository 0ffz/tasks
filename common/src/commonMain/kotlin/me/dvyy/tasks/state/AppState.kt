package me.dvyy.tasks.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.data.Auth
import me.dvyy.tasks.data.SyncClient

@Stable
class AppState {
    val auth = Auth()
    val sync = SyncClient("http://localhost:4000", Dispatchers.Default, auth)
    val snackbarHostState = SnackbarHostState()
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)
}

