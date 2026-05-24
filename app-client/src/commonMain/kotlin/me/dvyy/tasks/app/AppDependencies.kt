package me.dvyy.tasks.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.client.mutators.ActionQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.data.UpdateViewModel
import me.dvyy.tasks.app.logging.ColoredFormatter
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
            //FIXME does calling here remove once we leave this scope?
            it.koin.get<AuthViewModel>()
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

data class LogEntry(val severity: Severity, val message: AnnotatedString, val tag: String, val throwable: Throwable?)

class TrackingLogWriter(
    maxLogs: Int = 500,
) : LogWriter() {
    var enabled: Boolean = true
    val logFlow = MutableSharedFlow<LogEntry>(replay = maxLogs, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun severityToColor(severity: Severity): Color = when (severity) {
        Severity.Verbose -> Color.Gray
        Severity.Debug -> Color.Cyan
        Severity.Info -> Color.Green
        Severity.Warn -> Color.Yellow
        Severity.Error -> Color.Red
        Severity.Assert -> Color.Magenta
    }

    fun severityToString(severity: Severity) = when (severity) {
        Severity.Verbose -> "TRACE"  // White
        Severity.Debug -> "DEBUG"    // Cyan
        Severity.Info -> "INFO "     // Green
        Severity.Warn -> "WARN "     // Yellow
        Severity.Error -> "ERROR"    // Red
        Severity.Assert -> "ASSRT"   // Magenta
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        if (!enabled) return
        val annotatedMessage = buildAnnotatedString {
            withStyle(style = androidx.compose.ui.text.SpanStyle(color = severityToColor(severity))) {
                append(severityToString(severity))
                if (tag.isNotEmpty()) append(" [$tag]")
                append(" ")
                append(message)
            }
            throwable?.let {
                append("\n")
                withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Red)) {
                    append(it.stackTraceToString())
                }
            }
        }
        logFlow.tryEmit(LogEntry(severity, annotatedMessage, tag, throwable))
    }
}

fun coreModule() = module {
    single<TrackingLogWriter> { TrackingLogWriter() }
    single<Logger> {
        Logger.apply {
            setLogWriters(platformLogWriter(ColoredFormatter), get<TrackingLogWriter>())
        }
    }
    singleOf(::AppState)
    single { Dispatchers.Default }
    single { AppFactories.createDatabase(this) }
    single { AppFactories.createAppSettings() }
    singleOf(::LocalPreferencesRepository)
    single { SnackbarHostState() }
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

fun viewModelsModule() = module {
    viewModelOf(::TimeViewModel)
    viewModel { TasksViewModel(db = get<AppDatabase>()) }
    viewModelOf(::AuthViewModel)
    viewModelOf(::DialogViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::LayoutViewModel)
    viewModelOf(::UpdateViewModel)
}
