package me.dvyy.tasks.app.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Stable

@Stable
class AppState {
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)
}

