package me.dvyy.tasks.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import me.dvyy.tasks.app.data.Environment
import java.util.prefs.Preferences

actual fun AppSettings(): Settings =
    PreferencesSettings(Preferences.userRoot().node("me.dvyy").node(Environment.customAppDir ?: "tasks"))
