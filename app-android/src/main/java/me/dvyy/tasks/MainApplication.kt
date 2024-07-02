package me.dvyy.tasks

import android.app.Application
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.createAppKoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin(createAppKoinApplication {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                module {
                    single { createDatabase(DriverFactory(applicationContext)) }
                }
            )
        })
    }
}
