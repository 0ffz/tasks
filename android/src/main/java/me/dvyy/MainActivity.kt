package me.dvyy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ca.gosyer.appdirs.impl.attachAppDirs
import me.dvyy.tasks.App


class MainActivity : ComponentActivity() {

    //    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        application.attachAppDirs()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
