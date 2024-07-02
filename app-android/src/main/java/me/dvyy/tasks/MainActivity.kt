package me.dvyy.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ca.gosyer.appdirs.impl.attachAppDirs
import me.dvyy.tasks.app.ui.AppAndroid


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        application.attachAppDirs()
        super.onCreate(savedInstanceState)
        setContent {
            AppAndroid()
        }
    }
}
