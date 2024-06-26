package me.dvyy.tasks.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.auth.data.AuthAPI
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.CredentialsDataSource
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.sync.data.MessagesDataSource
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.sync.ui.SyncViewModel
import me.dvyy.tasks.tasks.data.SyncAPI
import me.dvyy.tasks.tasks.data.TaskListRepository
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.data.TasksLocalDataSource
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.currentKoinScope
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule() = module {
    singleOf(::AppState)
    single { Dispatchers.Default }
    singleOf(::Settings)
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
}


fun syncModule() = module {
    singleOf(::MessagesDataSource)
    singleOf(::SyncAPI)
    singleOf(::SyncRepository)
    viewModel { SyncViewModel(get()) }
}

fun viewModelsModule() = module {
    viewModel { TimeViewModel() }
    viewModel { TasksViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { DialogViewModel() }
    viewModel { PreferencesViewModel(get()) }
}

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel { scope.get<T>() }
}


expect inline fun <reified VM : ViewModel> Module.viewModel(crossinline factory: Definition<VM>)
