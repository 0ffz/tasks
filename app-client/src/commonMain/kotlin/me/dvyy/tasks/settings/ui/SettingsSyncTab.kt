package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.dialogs.AppDialog
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.auth.ui.LoginState
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.sync.ui.SyncStatusIcon
import me.dvyy.tasks.sync.ui.SyncViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSyncTab(
    auth: AuthViewModel = koinViewModel(),
    dialogs: DialogViewModel = koinViewModel(),
    sync: SyncViewModel = koinViewModel(),
) = Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    val loginState by auth.loginState.collectAsState()
    val login = loginState // Smart casts

    if (login !is LoginState.Success) {
        Text("Please login to a sync server to use sync features.")
        FilledTonalButton(onClick = { dialogs.show(AppDialog.Auth) }) {
            Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Switch account")
            Text(text = "Login")
        }
        return
    }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = { sync.sync() }, shapes = ButtonDefaults.shapes()) {
            LeadingIcon({ SyncStatusIcon() }) {
                Text(text = "Sync")
            }
        }
        TextButton(
            onClick = { sync.forcePull() }, shapes = ButtonDefaults.shapes()
        ) {
            LeadingIcon(Icons.Outlined.CloudDownload, contentDescription = "Pull all") {
                Text(text = "Pull all")
            }
        }
        TextButton(
            onClick = { sync.fullSync() }, shapes = ButtonDefaults.shapes()
        ) {
            LeadingIcon(Icons.Outlined.CloudUpload, contentDescription = "Push all") {
                Text(text = "Push all")
            }
        }
    }

    HorizontalDivider()

    Text("Account", style = MaterialTheme.typography.headlineMedium)

    LeadingIcon(Icons.Outlined.AccountCircle, contentDescription = "Account") {
        Text(text = "${login.username}@${login.serverURL}")
    }

    FilledTonalButton(onClick = { auth.logout() }, colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
        LeadingIcon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Account") {
            Text(text = "Logout")
        }
    }
}
