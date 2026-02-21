package me.dvyy.tasks.app

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.client.mutators.ActionQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.auth.data.AuthAPI
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.CredentialsDataSource
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.model.database.AppActions
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.commonSyncModule
import me.dvyy.tasks.sync.data.KtorSyncService
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.takeout.takeoutModule
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.time.TimeViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.koinApplication
import org.koin.dsl.module

fun createAppKoinApplication(configure: KoinApplication.() -> Unit = {}, overrides: Module = module {}) =
    koinApplication {
        configure()
    modules(appModule(), overrides)
}.also {
    runBlocking {
        //TODO loading screen
        it.koin.get<SyncClient>().initialize()
    }
}

fun appModule() = module(createdAtStart = true) {
    includes(
        coreModule(),
        authModule(),
        syncModule(),
        takeoutModule(),
        viewModelsModule(),
    )
}

fun coreModule() = module {
    single<Logger> { Logger }
    singleOf(::AppState)
    single { Dispatchers.Default }
    single { AppFactories.createDatabase(this) }
    single { AppFactories.createAppSettings() }
    singleOf(::LocalPreferencesRepository)
}

fun authModule() = module {
    singleOf(::CredentialsDataSource)
    singleOf(::AppHTTP)
    singleOf(::AuthAPI)
    singleOf(::AuthRepository)
}

fun syncModule() = module(createdAtStart = true) {
    includes(commonSyncModule(), authModule())
    singleOf(::KtorSyncService) bind SyncService::class
    singleOf(::ActionQueue) binds (arrayOf(Actions::class, ActionQueue::class))
    singleOf(::AppActions)
    singleOf(::SyncClient)
    singleOf(::AppDatabase)
    viewModelOf(::SyncViewModel)
}

fun viewModelsModule() = module(createdAtStart = true) {
    viewModelOf(::TimeViewModel)
    viewModel { TasksViewModel(db = get<AppDatabase>()) }
    viewModelOf(::AuthViewModel)
    viewModelOf(::DialogViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::LayoutViewModel)
}
