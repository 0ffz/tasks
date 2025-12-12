package me.dvyy.tasks.app

import android.content.Context
import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database
import org.koin.core.scope.Scope

actual object AppFactories {
    actual fun createDatabase(scope: Scope): Database {
        val databasePath = scope.get<Context>().getDatabasePath("database.db").absolutePath
        return Database(databasePath)
    }

    actual fun createAppSettings(): Settings = Settings()
}