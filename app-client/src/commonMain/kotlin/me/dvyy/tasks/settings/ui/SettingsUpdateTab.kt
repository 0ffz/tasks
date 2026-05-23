package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalUriHandler
import me.dvyy.tasks.BuildKonfig
import me.dvyy.tasks.app.data.UpdateRepository
import me.dvyy.tasks.app.ui.UI
import org.koin.compose.koinInject

@Composable
fun SettingsUpdateTab(
    updates: UpdateRepository = koinInject(),
): Unit = Column(verticalArrangement = Arrangement.spacedBy(UI.padding.md)) {
    val uriHandler = LocalUriHandler.current
    val uri = "https://github.com/0ffz/tasks/releases"

    var latestVersion by rememberSaveable { mutableStateOf("Loading...") }
    LaunchedEffect(Unit) {
        latestVersion = updates.getLatestAppVersion(true)
    }

    BoxedList("Updates") {
        SettingButton("View releases", onClick = {
            uriHandler.openUri(uri)
        }, description = uri)
        SettingItem("Current version", description = BuildKonfig.version) {}
        SettingItem("Latest version", description = latestVersion) {}
    }
}
