package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import me.dvyy.tasks.app.ui.UI

@Composable
fun SettingsUpdateTab(
): Unit = Column(verticalArrangement = Arrangement.spacedBy(UI.padding.md)) {
    val uriHandler = LocalUriHandler.current
    val uri = "Check GitHub at https://github.com/0ffz/tasks/releases"
    BoxedList("Updates") {
        SettingButton("View releases", onClick = {
            uriHandler.openUri(uri)
        }, description = uri)
    }
}
