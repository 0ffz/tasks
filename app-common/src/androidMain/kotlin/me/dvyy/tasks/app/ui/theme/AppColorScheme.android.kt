package me.dvyy.tasks.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun MaterialColorScheme(pref: DarkModePref): ColorScheme {
    val isInDarkMode = when (pref) {
        DarkModePref.DARK -> true
        DarkModePref.LIGHT -> false
        DarkModePref.AUTO -> isSystemInDarkTheme()
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Use dynamic colors if available
        val scheme = when {
            isInDarkMode -> dynamicDarkColorScheme(LocalContext.current)
            else -> dynamicLightColorScheme(LocalContext.current)
        }
        return scheme
    } else {
        // Fall back to the default colors otherwise
        when {
            isInDarkMode -> darkColorScheme()
            else -> lightColorScheme()
        }
    }
}

actual fun DefaultTheme(): TaskAppTheme = TaskAppTheme.Material

@Composable
actual fun SystemDarkMode(): Boolean = isSystemInDarkTheme()
