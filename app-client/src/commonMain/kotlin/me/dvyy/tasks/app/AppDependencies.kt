package me.dvyy.tasks.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.data.UpdateViewModel
import me.dvyy.tasks.app.logging.ColoredFormatter
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.PreferencesViewModel
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
import org.kodein.di.DI
import org.kodein.di.LazyDI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.delegate
import org.kodein.di.instance

fun createAppKoinApplication(vararg overrides: DI.Module): LazyDI = DI.lazy {
    import(appModule())
    importAll(*overrides)
    onReady {
        runBlocking {
            instance<SyncClient>().initialize()
            instance<AuthViewModel>()
        }
    }
}

fun appModule() = DI.Module("app") {
    importAll(
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
            withStyle(style = SpanStyle(color = severityToColor(severity))) {
                append(severityToString(severity))
                if (tag.isNotEmpty()) append(" [$tag]")
                append(" ")
                append(message)
            }
            throwable?.let {
                append("\n")
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append(it.stackTraceToString())
                }
            }
        }
        logFlow.tryEmit(LogEntry(severity, annotatedMessage, tag, throwable))
    }
}

fun coreModule() = DI.Module("core") {
    bindSingleton<TrackingLogWriter> { TrackingLogWriter() }
    bindSingleton<Logger> {
        Logger.apply {
            setLogWriters(platformLogWriter(ColoredFormatter), instance<TrackingLogWriter>())
        }
    }
    bindSingletonOf(::AppState)
    bindSingleton { Dispatchers.Default }
    bindSingleton { AppFactories.createDatabase(di) }
    bindSingleton { AppFactories.createAppSettings() }
    bindSingletonOf(::LocalPreferencesRepository)
    bindSingleton { SnackbarHostState() }
}

fun authModule() = DI.Module("auth") {
    bindSingletonOf(::CredentialsDataSource)
    bindSingletonOf(::AppHTTP)
    bindSingletonOf(::AuthAPI)
    bindSingletonOf(::AuthRepository)
}

fun syncModule() = DI.Module("sync") {
    importAll(commonSyncModule())
    bindSingletonOf(::KtorSyncService)// bind SyncService::class
    bindSingletonOf(::ActionQueue) //binds (arrayOf(Actions::class, ActionQueue::class))
    bindSingletonOf(::AppActions)
    bindSingletonOf(::SyncClient)
    delegate<Actions>().to<SyncClient>()
    bindSingletonOf(::AppDatabase)
    bindSingletonOf(::SyncViewModel)
}

fun viewModelsModule() = DI.Module("viewModels") {
    bindSingletonOf(::TimeViewModel)
    bindSingleton { TasksViewModel(db = instance<AppDatabase>()) }
    bindSingletonOf(::AuthViewModel)
    bindSingletonOf(::PreferencesViewModel)
    bindSingletonOf(::LayoutViewModel)
    bindSingletonOf(::UpdateViewModel)
}
