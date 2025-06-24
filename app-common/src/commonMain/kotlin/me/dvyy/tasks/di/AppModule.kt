package me.dvyy.tasks.di

import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.auth.data.AuthAPI
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.CredentialsDataSource
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.database.MutatorQueue
import me.dvyy.tasks.database.Mutators
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.data.SyncAPI
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

fun appModule() = module {
    singleOf(::AppState)
    single { Dispatchers.Default }
    singleOf(::AppSettings)
    singleOf(::LocalPreferencesRepository)
}

fun authModule() = module {
    singleOf(::CredentialsDataSource)
    singleOf(::AppHTTP)
    singleOf(::AuthAPI)
    singleOf(::AuthRepository)
}

fun repositoriesModule() = module {
//    singleOf(::TasksLocalDataSource)
//    singleOf(::TaskRepository)
//    singleOf(::TaskListRepository)
//    singleOf(::BulkAddRepository)
}


fun syncModule() = module {
    singleOf(::SyncAPI)
    singleOf(::SyncRepository)
    singleOf<Mutators>(::MutatorQueue)
    viewModelOf(::SyncViewModel)
}

fun viewModelsModule() = module {
    viewModelOf(::TimeViewModel)
    viewModel { TasksViewModel(mutators = get<Mutators>()) }
    viewModelOf(::AuthViewModel)
    viewModelOf(::DialogViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::LayoutViewModel)
}
