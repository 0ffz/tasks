package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    BoxedList {
        var checked by remember { mutableStateOf(prefs.appTheme.value == TaskAppTheme.Material) }
        SettingToggle(
            "Use colorful theme",
            description = "Enables Material color theme, will use system colors on Android",
            checked = checked,
            onCheckedChange = { toggle ->
                checked = toggle
                prefs.appTheme.update {
                    when (toggle) {
                        true -> TaskAppTheme.Material
                        false -> TaskAppTheme.JetbrainsLike
                    }
                }
            }
        )

        SettingItem("Theme style") {
            val darkModePref by prefs.darkMode.collectAsState()
            ButtonGroup(
                overflowIndicator = { menuState ->
                    ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
                },
                Modifier.widthIn(max = 500.dp).height(44.dp),
            ) {
//            SingleChoiceSegmentedButtonRow(Modifier.widthIn(max = 500.dp).fillMaxWidth()) {

                fun option(pref: DarkModePref, label: String, icon: ImageVector, index: Int, count: Int) =
                    toggleableItem(
                        darkModePref == pref,
                        onCheckedChange = { prefs.darkMode.update { pref } },
//                        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
                        label = label,
                        weight = 1f,
                        icon = { Icon(icon, "Mode indicator") }
                    )
                option(DarkModePref.AUTO, "Auto", AppIcons.AutoMode, 0, 3)
                option(DarkModePref.DARK, "Dark", AppIcons.DarkMode, 1, 3)
                option(DarkModePref.LIGHT, "Light", AppIcons.LightMode, 2, 3)
            }
        }

        MultilineSettingItem("Tag color scheme", description = "Task color theme in JSON format:", isLast = true) {
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
}
