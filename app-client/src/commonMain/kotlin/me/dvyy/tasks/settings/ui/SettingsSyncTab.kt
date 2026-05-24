package me.dvyy.tasks.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.auth.ui.AuthDialog
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.auth.ui.LoginState
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.sync.ui.SyncViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun SettingsSyncTab() {
    val auth: AuthViewModel by rememberViewModel()
    val sync: SyncViewModel by rememberViewModel()
    val loginState by auth.loginState.collectAsState()
    val login = loginState // Smart casts


    BoxedList("Sync") {
        val count by sync.queuedActionCount.collectAsState("Unknown")
        SettingItem("Unsynced actions", description = count.toString())
    }

    if (login !is LoginState.Success) {
        BoxedList {
            MultilineSettingItem(
                "Enable sync",
                description = "Login to a sync server to use sync features.",
            ) {
                AuthDialog()
            }
        }
        return
    }

    BoxedList("Account") {
        SettingItem("User", description = login.username)
        SettingItem("Server", description = login.serverURL)
    }
    SettingsButtonGroup {
        ErrorButton(onClick = { auth.logout() }) {
            LeadingIcon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Account") {
                Text(text = "Logout")
            }
        }
        ErrorButton(onClick = { sync.clearLocalActions() }) {
            LeadingIcon(Icons.Outlined.ClearAll, contentDescription = "Clear") {
                Text(text = "Clear local actions")
            }
        }
    }
}
