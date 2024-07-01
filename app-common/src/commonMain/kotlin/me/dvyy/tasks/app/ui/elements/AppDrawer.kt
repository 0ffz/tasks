package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppDialog
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.auth.ui.LoginState
import me.dvyy.tasks.core.ui.modifiers.NoRippleInteractionSource
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.sync.ui.SyncStatusIcon
import me.dvyy.tasks.sync.ui.SyncViewModel
import org.koin.compose.koinInject

@Composable
fun AppDrawer(
    app: AppState = koinInject(),
    auth: AuthViewModel = koinViewModel(),
    sync: SyncViewModel = koinViewModel(),
    dialogs: DialogViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val ui = LocalUIState.current
    ModalNavigationDrawer(
        // Prevent swipe to open on desktop, but allow swipe to close.
        gesturesEnabled = ui.isSingleColumn || app.drawerState.isOpen,
        drawerState = app.drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(16.dp)) {
                    Text("Tasks", modifier = Modifier.padding(16.dp))
                    val loginState by auth.loginState.collectAsState()
                    val login = loginState // Smart casts
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text(text = "Settings") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Palette, contentDescription = "Theme") },
                        label = { Text(text = "Theme") },
                        selected = false,
                        onClick = { dialogs.show(AppDialog.Theme) }
                    )
                    Spacer(Modifier.weight(1f))
                    if (login !is LoginState.Success) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Switch account") },
                            label = { Text(text = "Login") },
                            selected = false,
                            onClick = { dialogs.show(AppDialog.Auth) }
                        )
                    } else {
                        NavigationDrawerItem(
                            icon = { SyncStatusIcon() },
                            label = { Text(text = "Sync") },
                            selected = false,
                            onClick = { sync.sync() },
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.CloudDownload, contentDescription = "Pull all") },
                            label = { Text(text = "Pull all") },
                            selected = false,
                            onClick = { sync.forcePull() },
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.CloudUpload, contentDescription = "Push all") },
                            label = { Text(text = "Push all") },
                            selected = false,
                            onClick = { sync.fullSync() },
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "Account") },
                            label = { Text(text = login.username) },
                            selected = false,
                            onClick = { },
                            interactionSource = NoRippleInteractionSource()
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Account") },
                            label = { Text(text = "Logout") },
                            selected = false,
                            onClick = { auth.logout() }
                        )
                    }

                }
            }
        },
    ) {
        content()
    }
}

@Composable
fun AppDrawerIconButton(app: AppState = koinInject()) {
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                app.drawerState.open()
            }
        },
    ) {
        Icon(
            Icons.Outlined.Menu,
            contentDescription = "Menu",
        )
    }
}
