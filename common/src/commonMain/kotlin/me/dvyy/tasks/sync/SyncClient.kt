package me.dvyy.tasks.sync

import com.benasher44.uuid.Uuid
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.model.AppFormats
import me.dvyy.tasks.model.Task
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.SyncStatus
import me.dvyy.tasks.state.TaskState

class SyncClient(val url: String, val app: AppState) {
    val inProgress = MutableStateFlow(false)
    val isError = MutableStateFlow(false)
    val diffRemoved = mutableSetOf<Uuid>()

    val client = HttpClient {
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule = AppFormats.networkModule
            })
        }
    }

    suspend fun sendTasks(tasksPerDate: Map<LocalDate, List<Task>>) {
        client.post("${url}/dates") {
            contentType(ContentType.Application.Json)
            setBody(tasksPerDate)
        }
    }

    suspend fun getLatestTasks(dates: List<LocalDate>): List<List<Task>> {
        return client.get("${url}/dates") {
            parameter("dates", dates.joinToString(separator = ",") { it.toString() })
        }.body<List<List<Task>>>()
    }

    suspend fun getLatestTasks(date: LocalDate): List<Task> =
        client.get("${url}/date/${date.toEpochDays()}").body()

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = coroutineScope {
        if (inProgress.value) return@coroutineScope
        inProgress.emit(true)
        isError.emit(false)
        try {
            val dateStates = app.loadedDates.values.toList()
            val dates = dateStates.map { it.date }
            val syncedTasks: List<List<Task>> = getLatestTasks(dates).mapIndexed { i, serverTasks ->
                val date = dateStates[i]
                val serverTasksByUUID = serverTasks.associateByTo(mutableMapOf()) { it.uuid }
                val mergedTasks = mutableListOf<TaskState>()

                date.tasks.value.forEachIndexed { index, localTask ->
                    val serverTask = serverTasksByUUID[localTask.uuid]
                    val syncStatus = localTask.syncStatus.value
                    when {
                        // We made changes locally since last sync (prefer client)
                        syncStatus != SyncStatus.SYNCED || serverTask != null -> {
                            if (mergedTasks.lastIndex < index) {
                                mergedTasks.add(localTask)
                            } else {
                                mergedTasks.add(index, localTask)
                            }
                            serverTasksByUUID.remove(localTask.uuid)
                        }
                        // Task was deleted on server and untouched locally (remove)
                        else -> {
                            app.tasks.remove(localTask.uuid)
                        }
                    }
                }

                // Remaining tasks are ones the server has that we don't
                serverTasksByUUID.values.forEachIndexed { index, task ->
                    if (task.uuid in diffRemoved) return@forEachIndexed
                    mergedTasks.add(date.createTask(app, task))
                }

                mergedTasks.forEach { it.syncStatus.value = SyncStatus.SYNCED }
                date.tasks.value = mergedTasks
                mergedTasks.map { it.toTask() }
            }
            diffRemoved.clear()
            sendTasks(dates.zip(syncedTasks).toMap())
        } catch (e: IOException) {
            e.printStackTrace()
            launch {
                app.snackbarHostState
                    .showSnackbar("Error syncing: ${e.message ?: "Unknown error"}", withDismissAction = true)
            }
            isError.emit(true)
        } finally {
            inProgress.emit(false)
        }
    }
}
