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
    singleOf(::SyncViewModel)
}

fun viewModelsModule() = module {
    singleOf(::TimeViewModel)
    singleOf(::TasksViewModel)
    singleOf(::AuthViewModel)
    singleOf(::DialogViewModel)
    singleOf(::PreferencesViewModel)
}

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel { scope.get<T>() }
}
