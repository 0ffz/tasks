package me.dvyy.tasks.sync

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.model.Task
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.SyncStatus
import me.dvyy.tasks.state.TaskState

class SyncClient(val url: String, val app: AppState) {
    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun sendTasks(tasksPerDate: Map<LocalDate, List<Task>>) {
        client.post("${url}/dates") {
            contentType(ContentType.Application.Json)
            setBody(tasksPerDate)
        }
    }

    suspend fun getLatestTasks(dates: List<LocalDate>): List<List<Task>> =
        client.get("${url}/dates") {
            contentType(ContentType.Application.Json)
            setBody(dates)
        }.body()

    suspend fun getLatestTasks(date: LocalDate): List<Task> =
        client.get("${url}/date/${date.toEpochDays()}").body()

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = coroutineScope {
        val dateStates = app.loadedDates.values.toList()
        val dates = dateStates.map { it.date }
        val syncedTasks: List<List<Task>> = getLatestTasks(dates).mapIndexed { i, serverTasks ->
            val date = dateStates[i]
            val serverTasksByUUID = serverTasks.associateByTo(mutableMapOf()) { it.uuid }
            val mergedTasks = mutableListOf<TaskState>()

            date.tasks.value.forEachIndexed { index, localTask ->
                val serverTask = serverTasksByUUID[localTask.uuid]
                val syncStatus = localTask.syncStatus.value
                if (serverTask == null) {

                }
                when {
                    // Server doesn't have a task, but we do locally that's either been created or updated
                    serverTask == null && syncStatus != SyncStatus.PULLED -> {
                        if (mergedTasks.lastIndex < index) {
                            mergedTasks.add(localTask)
                        } else {
                            mergedTasks.add(index, localTask)
                        }
                        serverTasksByUUID.remove(localTask.uuid)
                    }
                    // Task was deleted on server and untouched locally
                    else -> {
                        app.tasks.remove(localTask.uuid)
                    }
                }
            }

            // Remaining tasks are ones the server has that we don't
            serverTasksByUUID.values.forEachIndexed { index, task ->
                mergedTasks.add(date.createTask(app, task))
            }

            date.tasks.value = mergedTasks
            mergedTasks.map { it.toTask() }
        }
        sendTasks(dates.zip(syncedTasks).toMap())
    }
}
