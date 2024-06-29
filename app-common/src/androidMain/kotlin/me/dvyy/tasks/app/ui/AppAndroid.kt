package me.dvyy.tasks.app.ui

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createDatabase
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAndroid(applicationContext: Context) = App(
    extraModules = listOf(module {
        single { createDatabase(DriverFactory(applicationContext)) }
    }),
)
