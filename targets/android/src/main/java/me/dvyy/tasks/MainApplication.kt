package me.dvyy.tasks

import android.app.Application
import me.dvyy.tasks.app.createAppKoinApplication
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton

class MainApplication : Application(), DIAware {
    override val di: DI = createAppKoinApplication(DI.Module("android") {
        bindSingleton { this@MainApplication.applicationContext }
    })
}
