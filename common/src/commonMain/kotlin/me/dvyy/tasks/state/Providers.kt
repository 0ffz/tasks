package me.dvyy.tasks.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val AppStateProvider = compositionLocalOf<AppState> { error("No local app state provided") }
val LocalResponsiveUI = compositionLocalOf<AppResponsiveUI> { error("No local responsive UI") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current
