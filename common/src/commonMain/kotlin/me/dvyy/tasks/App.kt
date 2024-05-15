package me.dvyy.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.ui.screens.HomeScreen
import me.dvyy.tasks.ui.screens.auth.AuthDialog

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        val state = remember { AppState() }
        CompositionLocalProvider(AppStateProvider provides state) {
            Scaffold(Modifier.fillMaxSize()) {
                Box(Modifier.statusBarsPadding()) {
                    HomeScreen()
                }
            }
            AuthDialog()
        }
    }
}
