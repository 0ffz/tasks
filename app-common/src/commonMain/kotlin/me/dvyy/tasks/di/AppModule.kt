package me.dvyy.tasks.di

import kotlinx.coroutines.Dispatchers
import me.dvyy.syncengine.client.mutators.MutatorQueue
import me.dvyy.syncengine.schema.Mutators
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
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.AppMutators
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.mutators.Mutator
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.data.SyncAPI
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.binds
import org.koin.dsl.module

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
    single { MutatorQueue(get(), Mutator.serializer()) }.binds(arrayOf(Mutators::class, MutatorQueue::class))
    singleOf(::AppDAO)
    singleOf(::AppMutators)
    singleOf(::AppDatabase)
    viewModelOf(::SyncViewModel)
}

fun viewModelsModule() = module {
    viewModelOf(::TimeViewModel)
    viewModel { TasksViewModel(db = get<AppDatabase>()) }
    viewModelOf(::AuthViewModel)
    viewModelOf(::DialogViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::LayoutViewModel)
}
