package me.dvyy.tasks

import android.app.Application
import me.dvyy.tasks.app.createAppKoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class MainApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin(createAppKoinApplication(configure = {
            androidLogger()
            androidContext(this@MainApplication)
        }))
    }
}
