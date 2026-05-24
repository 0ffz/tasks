package me.dvyy.tasks.app

import android.content.Context
import com.russhwolf.settings.Settings
import me.dvyy.sqlite.Database
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

actual object AppFactories {
    actual fun createDatabase(scope: DI): Database {
        val databasePath = scope.direct.instance<Context>().getDatabasePath("database.db").absolutePath
        return Database(databasePath)
    }

    actual fun createAppSettings(): Settings = Settings()
}