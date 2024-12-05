package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.theme.DarkModePref
import me.dvyy.tasks.app.ui.theme.Fonts
import me.dvyy.tasks.app.ui.theme.TaskAppTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsThemeTab(
    prefs: PreferencesViewModel = koinViewModel(),
) {
    val prefsTheme by prefs.theme.collectAsState()
    var theme by remember { mutableStateOf(prefsTheme) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        var checked by remember { mutableStateOf(prefs.appTheme.value == TaskAppTheme.Material) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Use colorful theme", style = MaterialTheme.typography.titleLarge)
                Text("Enables Material color theme, will use system colors on Android")
            }
            Switch(checked, onCheckedChange = { toggle ->
                checked = toggle
                prefs.appTheme.update {
                    when (toggle) {
                        true -> TaskAppTheme.Material
                        false -> TaskAppTheme.JetbrainsLike
                    }
                }
            })
        }

        Text("Theme style", style = MaterialTheme.typography.titleLarge)
        SingleChoiceSegmentedButtonRow(Modifier.widthIn(max = 500.dp).fillMaxWidth()) {
            val darkModePref by prefs.darkMode.collectAsState()

            @Composable
            fun option(pref: DarkModePref, label: String, icon: ImageVector, index: Int, count: Int) = SegmentedButton(
                darkModePref == pref,
                onClick = { prefs.darkMode.update { pref } },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
                label = { Text(label) },
                icon = { Icon(icon, "Mode indicator") }
            )
            option(DarkModePref.AUTO, "Auto", AppIcons.AutoMode, 0, 3)
            option(DarkModePref.DARK, "Dark", AppIcons.DarkMode, 1, 3)
            option(DarkModePref.LIGHT, "Light", AppIcons.LightMode, 2, 3)
        }
        HorizontalDivider()

        Text("Tag color scheme", style = MaterialTheme.typography.titleLarge)

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
