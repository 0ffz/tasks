package me.dvyy.tasks.ui.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.state.LocalAppState

@Composable
fun WelcomeText() {
    val app = LocalAppState
    val username by app.auth.username.collectAsState()

    if (username.isEmpty()) {
        return
    }

    Text("Welcome, $username!", style = MaterialTheme.typography.headlineMedium)
}
