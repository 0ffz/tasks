package me.dvyy.tasks.di

import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.DialogState
import me.dvyy.tasks.state.TimeState
import org.koin.dsl.module

fun appModule() = module {
    single { AppState() }
    single { TimeState() }
    single { DialogState() }
}
