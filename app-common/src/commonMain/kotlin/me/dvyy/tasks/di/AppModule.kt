package me.dvyy.tasks.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.DialogState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.auth.data.UserRepository
import me.dvyy.tasks.sync.data.SyncClient
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.data.TasksLocalDataSource
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.currentKoinScope
import org.koin.dsl.module

fun appModule() = module {
    single { AppState() }
    single { DialogState() }
}

fun syncModule() = module {
    single { UserRepository() }
    single { SyncClient("http://localhost:4000", Dispatchers.Default, get<UserRepository>()) }
}

fun viewModelsModule() = module {
    single { TimeViewModel() }
    single {
        TasksViewModel(
            tasks = TaskRepository(
                localStore = TasksLocalDataSource(),
                ioDispatcher = Dispatchers.Default
            )
        )
    }
}

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel { scope.get<T>() }
}
