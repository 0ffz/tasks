package me.dvyy.tasks.settings.ui

import TasksViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.base.internal.SettingsTileScaffold
import me.dvyy.tasks.app.ui.theme.Fonts
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsTagsTab(
    tasks: TasksViewModel = koinViewModel(),
) {
    SettingsGroup(title = { Text("Tags") }) {
        SettingsTileScaffold(
            title = { Text("Tag colors") },
            subtitle = {
                Column {
                    Text("TODO")
                }
            },
        )
    }
}
