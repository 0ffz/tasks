package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppDialog
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.theme.Fonts
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.auth.ui.LoginState
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.sync.ui.SyncStatusIcon
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel

sealed interface SettingsTab {
    val title: String
    val icon: ImageVector

    data object Theme : SettingsTab {
        override val title = "Theme"
        override val icon = Icons.Outlined.Palette
    }

    data object BulkAdd : SettingsTab {
        override val title = "Bulk add"
        override val icon = Icons.Outlined.UploadFile
    }

    data object Sync : SettingsTab {
        override val title = "Sync"
        override val icon = Icons.Outlined.Sync
    }

    companion object {
        val tabs = listOf(Sync, Theme, BulkAdd)
    }
}

@Composable
fun LeadingIcon(icon: @Composable () -> Unit, content: @Composable () -> Unit) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(8.dp))
        content()
    }

@Composable
fun LeadingIcon(icon: ImageVector, contentDescription: String, content: @Composable (() -> Unit)) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = contentDescription)
        Spacer(Modifier.width(8.dp))
        content()
    }

@Composable
fun ResponsiveNavigationDrawer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val ui = LocalUIState.current
    if (ui.isSingleColumn) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { drawerContent() }
        ) {
            content()
        }
    } else {
        PermanentNavigationDrawer(
            drawerContent = { drawerContent() }
        ) {
            content()
        }
    }
}

@Composable
fun SettingsScreen() {
    var screen by remember { mutableStateOf<SettingsTab>(SettingsTab.Sync) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ResponsiveNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceDim,
            ) {
                SettingsTab.tabs.forEach { tab ->
                    NavigationDrawerItem(
                        selected = screen == tab,
                        onClick = { screen = tab },
                        icon = { Icon(tab.icon, tab.title) },
                        label = { Text(tab.title) },
                        shape = RectangleShape,
                    )
                }
            }
        }
    ) {
        val ui = LocalUIState.current
        if (ui.isSingleColumn) {
            val scope = rememberCoroutineScope()
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Open menu")
            }
        }

        Column(Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
            if (ui.isSingleColumn) Spacer(Modifier.height(32.dp))
            Text(screen.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            when (screen) {
                SettingsTab.Theme -> ThemeSettings()
                SettingsTab.Sync -> SyncSettings()
                SettingsTab.BulkAdd -> BulkAddSettings()
            }
        }
    }
}

@Composable
fun ThemeSettings(
    prefs: PreferencesViewModel = koinViewModel(),
) {
    val prefsTheme by prefs.theme.collectAsState()
    var theme by remember { mutableStateOf(prefsTheme) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Task color theme in JSON format:")

        OutlinedTextField(
            theme,
            onValueChange = { theme = it },
            minLines = 8,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontFamily = Fonts.monospaced()),
        )

        TextButton(onClick = {
            prefs.theme.update { theme }
        }) {
            Text("Save")
        }
    }
}

@Composable
fun BulkAddSettings(
    tasks: TasksViewModel = koinViewModel(),
) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(buildAnnotatedString {
            append("Enter tasks, one per line, use ^ for date, ! for highlight, : for text, ex")
            appendLine()
            withStyle(SpanStyle(fontFamily = Fonts.monospaced())) { append("!1 ^2024-12-31 :Do something important!") }
        })
        OutlinedTextField(
            text,
            onValueChange = { text = it },
            minLines = 8,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontFamily = Fonts.monospaced()),
        )

        TextButton(onClick = {
            tasks.bulkAdd(text.lines())
            text = ""
        }) {
            Text("Done")
        }
    }
}


@Composable
fun SyncSettings(
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
        TextButton(onClick = { sync.sync() }) {
            LeadingIcon({ SyncStatusIcon() }) {
                Text(text = "Sync")
            }
        }
        TextButton(
            onClick = { sync.forcePull() }
        ) {
            LeadingIcon(Icons.Outlined.CloudDownload, contentDescription = "Pull all") {
                Text(text = "Pull all")
            }
        }
        TextButton(
            onClick = { sync.fullSync() }
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

    FilledTonalButton(onClick = { auth.logout() }) {
        LeadingIcon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Account") {
            Text(text = "Logout")
        }
    }
}
