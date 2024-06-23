package me.dvyy.tasks.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.CredentialsDataSource
import me.dvyy.tasks.auth.ui.AuthViewModel
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.tasks.data.TaskListRepository
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.data.TasksLocalDataSource
import me.dvyy.tasks.tasks.data.TasksNetworkDataSource
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.currentKoinScope
import org.koin.dsl.module

fun appModule() = module {
    single { AppState() }
    single { DialogViewModel() }
}

fun syncModule() = module {
    single { CredentialsDataSource() }
    single { AppHTTP() }
    single { TasksNetworkDataSource(get()) }
    single { AuthRepository(get<AppHTTP>(), get<CredentialsDataSource>()) }
    single { Settings() }
}

fun repositoriesModule() = module {
    single { TasksLocalDataSource(get<Database>()) }
    single {
        TaskRepository(
            localStore = get(),
            network = get<TasksNetworkDataSource>(),
            ioDispatcher = Dispatchers.Default,
            settings = get<Settings>(),
        )
    }
    single { TaskListRepository(get()) }
}

fun viewModelsModule() = module {
    single { TimeViewModel() }
    single { TasksViewModel(get(), get()) }
    single { AuthViewModel(get<AuthRepository>()) }
}

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel { scope.get<T>() }
}
