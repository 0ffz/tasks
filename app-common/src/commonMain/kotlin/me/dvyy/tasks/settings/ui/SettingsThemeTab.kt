package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSwitch
import com.alorma.compose.settings.ui.base.internal.SettingsTileScaffold
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

    SettingsGroup(
        title = { Text("Theme") }
    ) {
        var checked by remember { mutableStateOf(prefs.appTheme.value == TaskAppTheme.Material) }
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Column(Modifier.weight(1f)) {
//                Text("Use colorful theme", style = MaterialTheme.typography.titleLarge)
//                Text("Enables Material color theme, will use system colors on Android")
//            }
//            Switch(checked, onCheckedChange = {)
//        }
        SettingsSwitch(
            checked,
            onCheckedChange = { toggle ->
                checked = toggle
                prefs.appTheme.update {
                    when (toggle) {
                        true -> TaskAppTheme.Material
                        false -> TaskAppTheme.JetbrainsLike
                    }
                }
            },
            title = { Text("Use colorful theme") },
            subtitle = { Text("Enables Material color theme, will use system colors on Android") }
        )
    }

    SettingsTileScaffold(
        title = { Text("Theme style") },
        subtitle = {
            SingleChoiceSegmentedButtonRow(Modifier.widthIn(max = 500.dp).fillMaxWidth()) {
                val darkModePref by prefs.darkMode.collectAsState()

                @Composable
                fun option(pref: DarkModePref, label: String, icon: ImageVector, index: Int, count: Int) =
                    SegmentedButton(
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
        }
    )

    SettingsTileScaffold(
        title = { Text("Tag color scheme") },
        subtitle = {
            Column {
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
    )
    return
}
