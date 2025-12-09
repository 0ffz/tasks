package me.dvyy.tasks.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun MaterialColorScheme(pref: DarkModePref): ColorScheme =
    if (pref == DarkModePref.LIGHT) lightColorScheme() else darkColorScheme()

actual fun DefaultTheme(): TaskAppTheme = TaskAppTheme.JetbrainsLike

@Composable
actual fun SystemDarkMode(): Boolean = true
