package me.dvyy.tasks.app

import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database

expect object AppFactories {
    fun createDatabase(): Database

    fun createAppSettings(): Settings
}
