package me.dvyy.tasks.di

import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.auth.data.AuthAPI
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.CredentialsDataSource
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.sync.data.MessagesDataSource
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.data.*
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    singleOf(::AppState)
    single { Dispatchers.Default }
    singleOf(::AppSettings)
}

fun authModule() = module {
    singleOf(::CredentialsDataSource)
    singleOf(::AppHTTP)
    singleOf(::AuthAPI)
    singleOf(::AuthRepository)
}

fun repositoriesModule() = module {
    singleOf(::TasksLocalDataSource)
    singleOf(::TaskRepository)
    singleOf(::TaskListRepository)
    singleOf(::BulkAddRepository)
}


fun syncModule() = module {
    singleOf(::MessagesDataSource)
    singleOf(::SyncAPI)
    singleOf(::SyncRepository)
    viewModelOf(::SyncViewModel)
}

fun viewModelsModule() = module {
    viewModelOf(::TimeViewModel)
    viewModelOf(::TasksViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::DialogViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::LayoutViewModel)
}
