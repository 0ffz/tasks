package me.dvyy.tasks.app

import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database
import org.kodein.di.DI

expect object AppFactories {
    fun createDatabase(scope: DI): Database

    fun createAppSettings(): Settings
}
