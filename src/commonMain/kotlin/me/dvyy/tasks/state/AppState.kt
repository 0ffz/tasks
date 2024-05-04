package me.dvyy.tasks.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val AppStateProvider = compositionLocalOf<AppState> { error("No local versions provided") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current

class AppState {
}
