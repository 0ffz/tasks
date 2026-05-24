package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Moon
import dev.seyfarth.tablericons.outlined.Sun
import dev.seyfarth.tablericons.outlined.SunMoon
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.theme.DarkModePref
import me.dvyy.tasks.app.ui.theme.Fonts
import me.dvyy.tasks.app.ui.theme.TaskAppTheme
import org.kodein.di.compose.viewmodel.rememberViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsThemeTab() {
    val prefs: PreferencesViewModel by rememberViewModel()
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
                option(DarkModePref.AUTO, "Auto", TablerIcons.Outlined.SunMoon, 0, 3)
                option(DarkModePref.DARK, "Dark", TablerIcons.Outlined.Moon, 1, 3)
                option(DarkModePref.LIGHT, "Light", TablerIcons.Outlined.Sun, 2, 3)
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
            Spacer(Modifier.height(UI.padding.sm))
            SettingsButtonGroup {
                SecondaryButton(onClick = {
                    prefs.theme.update { theme }
                }) { Text("Save") }
            }
        }
    }
}
