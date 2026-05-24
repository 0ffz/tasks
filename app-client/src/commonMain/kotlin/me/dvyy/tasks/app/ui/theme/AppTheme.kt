package me.dvyy.tasks.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.app.ui.PreferencesViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val prefs: PreferencesViewModel by rememberViewModel()
    val appTheme by prefs.appTheme.collectAsState()
    val darkMode by prefs.darkMode.collectAsState()
    val isDarkMode = when(darkMode) {
        DarkModePref.AUTO -> SystemDarkMode()
        DarkModePref.DARK -> true
        DarkModePref.LIGHT -> false
    }
    val colorScheme = when (val theme = appTheme) {
        TaskAppTheme.Material -> MaterialColorScheme(darkMode)
        is TaskAppTheme.Custom -> with(if(isDarkMode) theme.dark else theme.light) {
            (if(isDarkMode) darkColorScheme() else lightColorScheme()).copy(
                background = color(surface),
                surface = color(surface),
                surfaceVariant = color(surface),
                surfaceDim = color(surface),
                surfaceBright = color(surface),
                surfaceContainerLow = color(surface), // TODO add customization options for these
                surfaceContainerLowest = color(surface),
                surfaceContainerHigh = color(surface),
                surfaceContainerHighest = color(surface),
                surfaceContainer = color(surfaceContainer),
                surfaceTint = color(tint),
                primary = color(primary),
                primaryContainer = color(primaryContainer),
                secondary = color(secondary),
                secondaryContainer = color(secondaryContainer),
                tertiary = color(tertiary),
                tertiaryContainer = color(tertiaryContainer),
                outline = color(outline),
                outlineVariant = color(outlineVariant),
                onSurface = bestText(surface),
                onSurfaceVariant = bestText(surface),
                onBackground = bestText(surface),
                onPrimary = bestText(primary),
                onPrimaryContainer = bestText(primaryContainer),
                onSecondary = bestText(secondary),
                onSecondaryContainer = bestText(secondaryContainer),
                onTertiary = bestText(tertiary),
                onTertiaryContainer = bestText(tertiaryContainer),
            )
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography(),
    ) {
        content()
    }
}
