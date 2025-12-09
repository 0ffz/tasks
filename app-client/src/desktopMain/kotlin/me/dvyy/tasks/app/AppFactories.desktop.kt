package me.dvyy.tasks.app

import ca.gosyer.appdirs.AppDirs
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database
import me.dvyy.tasks.app.data.Environment
import java.util.prefs.Preferences
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.div

actual object AppFactories {
    actual fun createDatabase(): Database {
        val dirs = AppDirs(Environment.customAppDir ?: "tasks", "dvyy")
        val dataPath = Path(dirs.getUserDataDir())
        dataPath.createDirectories()
        //TODO swap back to tasks.db once migrations figured out
        return Database((dataPath / "database.db").absolutePathString())
    }

    actual fun createAppSettings(): Settings {
        return PreferencesSettings(Preferences.userRoot().node("me.dvyy").node(Environment.customAppDir ?: "tasks"))
    }
}