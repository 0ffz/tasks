package me.dvyy.tasks

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ca.gosyer.appdirs.impl.attachAppDirs
import me.dvyy.tasks.app.createAppKoinApplication
import me.dvyy.tasks.app.ui.AppAndroid
import org.kodein.di.DI
import org.kodein.di.DIAware

class MainActivity : ComponentActivity(), DIAware {
    override val di: DI = createAppKoinApplication()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ) // light causes internally enforce the navigation bar to be fully transparent
        )
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }
        application.attachAppDirs()

        setContent {
            AppAndroid()
        }
    }
}
