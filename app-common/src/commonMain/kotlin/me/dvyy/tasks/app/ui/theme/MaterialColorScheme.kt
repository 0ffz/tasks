package me.dvyy.tasks.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun MaterialColorScheme(pref: DarkModePref): ColorScheme

expect fun DefaultTheme(): TaskAppTheme

@Composable
expect fun SystemDarkMode(): Boolean
