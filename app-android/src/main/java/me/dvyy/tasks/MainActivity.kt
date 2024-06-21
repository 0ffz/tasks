package me.dvyy.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import ca.gosyer.appdirs.impl.attachAppDirs
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.App


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        application.attachAppDirs()
        super.onCreate(savedInstanceState)
        setContent {
            App(remember { createDatabase(DriverFactory(applicationContext)) })
        }
    }
}
