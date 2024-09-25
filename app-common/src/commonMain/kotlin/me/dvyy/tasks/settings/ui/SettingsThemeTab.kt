package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.theme.Fonts
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsThemeTab(
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
