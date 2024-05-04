package me.dvyy.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.ui.screens.HomeScreen

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        CompositionLocalProvider(AppStateProvider provides AppState()) {
            Scaffold(Modifier.fillMaxSize()) {
                HomeScreen()
            }
        }
    }
}
