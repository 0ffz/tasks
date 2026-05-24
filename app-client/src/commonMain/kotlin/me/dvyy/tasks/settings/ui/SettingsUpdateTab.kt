package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import kotlinx.coroutines.launch
import me.dvyy.tasks.BuildKonfig
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.data.UpdateViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.LeadingIcon
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsUpdateTab(
    updates: UpdateViewModel = koinViewModel(),
): Unit = Column(verticalArrangement = Arrangement.spacedBy(UI.padding.md)) {
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val releasesUrl = "https://github.com/0ffz/tasks/releases"
    val latestVersion by updates.latestVersion.collectAsState()
    val updateUrl by updates.updateUrl.collectAsState()

    BoxedList("App Info") {
        SettingItem("Version", description = BuildKonfig.version)
        SettingItem("Latest version", description = latestVersion ?: "-")
        SettingButton("Releases", onClick = {
            uriHandler.openUri(releasesUrl)
        }, description = releasesUrl)
    }
    SettingsButtonGroup {
        updateUrl?.let {
            PrimaryButton(onClick = { uriHandler.openUri(it) }) {
                LeadingIcon(AppIcons.Update, "Update available") {
                    Text(text = "Download Update")
                }
            }
        }
        SecondaryButton(onClick = {
            scope.launch {
                updates.fetchUpdates()
            }
        }) {
            Text(text = "Check for updates")
        }
    }
}

@Composable
fun PrimaryButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = UI.shapes.roundedExtra
    ) {
        content()
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = UI.shapes.roundedExtra,
        content = { content() }
    )
}

@Composable
fun ErrorButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors(
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = UI.shapes.roundedExtra,
        content = { content() }
    )
}