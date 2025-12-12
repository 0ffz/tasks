package me.dvyy.tasks.app

import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database
import org.koin.core.scope.Scope

expect object AppFactories {
    fun createDatabase(scope: Scope): Database

    fun createAppSettings(): Settings
}
