package me.dvyy.tasks.sync.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.sync.data.SyncClient.AuthResult.*

class SyncConfig(
    val url: String,
    val auth: DigestAuthCredentials,
)

class SyncClient(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
//    val inProgress = MutableStateFlow(false)
//    val isError = MutableStateFlow(false)
//    val diffRemoved = mutableSetOf<Uuid>()

    private val baseClient = HttpClient {
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule = AppFormats.networkModule
            })
        }
    }

    private var client = baseClient

    fun HttpClient.configure(config: SyncConfig): HttpClient {
        return config {
            defaultRequest {
                url(config.url)
            }
            install(Auth) {
                digest {
                    credentials { config.auth }
                }
            }
        }
    }

    fun configure(config: SyncConfig) {
        client = baseClient.configure(config)
    }

    enum class AuthResult {
        SUCCESS, INVALID_CREDENTIALS, CONNECTION_ERROR
    }

    suspend fun checkAuth(config: SyncConfig): AuthResult = withContext(ioDispatcher) {
        val result = runCatching { client.configure(config).get("auth/check").status == HttpStatusCode.OK }
            .getOrElse { return@withContext CONNECTION_ERROR }
        if (result) SUCCESS else INVALID_CREDENTIALS
    }

    suspend fun sendTasks(tasksPerDate: Map<LocalDate, List<TaskModel>>) = withContext(ioDispatcher) {
        client.post("dates") {
            contentType(ContentType.Application.Json)
            setBody(tasksPerDate)
        }
    }

    suspend fun getLatestTasks(dates: List<LocalDate>): List<List<TaskModel>> = withContext(ioDispatcher) {
        client.get("dates") {
            parameter("dates", dates.joinToString(separator = ",") { it.toString() })
        }.body<List<List<TaskModel>>>()
    }

    suspend fun getLatestTasks(date: LocalDate): List<TaskModel> = withContext(ioDispatcher) {
        client.get("date/${date.toEpochDays()}").body()
    }

    /** Ensures any currently loaded dates are synced with the server. */
    suspend fun sync() = withContext(ioDispatcher) {
//        if (inProgress.value) return@withContext
//        inProgress.emit(true)
//        isError.emit(false)
//        try {
//            val dateStates = app.loadedDates.values.toList()
//            val dates = dateStates.map { it.date }
//            val serverTasksByDate: List<List<TaskModel>> = getLatestTasks(dates)
//
//            // Filter out local deletions/modifications from received server tasks
//
//            val incoming = serverTasksByDate.map {
//                it.filter { task ->
//                    // Remove tasks on server we've deleted locally
//                    if (task.uuid in diffRemoved) return@filter false
//
//                    // Ignore tasks we've updated locally since last sync
//                    val sync = app.tasks[task.uuid]?.syncStatus?.value
//                    if (sync != null && sync != SyncStatus.SYNCED) return@filter false
//
//                    true
//                }
//            }
//            val incomingUpdates = incoming.map { it.filter { task -> app.tasks[task.uuid] != null } }
//            val incomingAdditions = incoming.map { it.filter { task -> app.tasks[task.uuid] == null } }
//
//            // Calculate any local tasks that should be deleted
//            val uuidsOnServer = serverTasksByDate.flatten().mapTo(mutableSetOf()) { it.uuid }
//            val queuedDeletions = app.tasks.values
//                .asSequence()
//                // Only delete tasks that weren't modified locally
//                .filter { it.syncStatus.value == SyncStatus.SYNCED }
//                .map { it.uuid }
//                // Delete anything no longer on the server
//                .minus(uuidsOnServer)
//                .toSet()
//
//            // Remove deleted tasks from map
//            queuedDeletions.forEach { uuid ->
//                app.tasks.remove(uuid)
//            }
//
//            // Update all incoming updates
//            incomingUpdates.forEachIndexed { i, taskList ->
//                val newDate = dates[i]
//                taskList.forEach { task ->
//                    val existingTask = app.tasks[task.uuid] ?: return@forEach
//                    existingTask.updateToMatch(task)
//                    existingTask.changeDate(app, newDate)
//                }
//            }
//
//            // Update local date states to reflect changes
//            val updatedTasksByDate = dateStates.mapIndexed { i, date ->
//                val currTasks = date.tasks.value
//
//                // mapNotNull to filter any clashing uuids (keep local)
//                fun Collection<TaskModel>.createTasks() =
//                    mapNotNull { date.createTask(app, it, updateDateTaskList = false) }
//
//                val updatedTasks: List<TaskState> = currTasks
//                    // Remove deleted
//                    .filter { it.uuid !in queuedDeletions }
//                    // Create states for update tasks and append to the end of the list
//                    .plus(incomingAdditions[i].createTasks())
//                // TODO sorting
//                updatedTasks.forEach { it.syncStatus.value = SyncStatus.SYNCED }
//                date.tasks.value = updatedTasks
//                updatedTasks.map { it.toModel() }
//            }
//            diffRemoved.clear()
//            app.saveTasks()
//
//            // Send synced tasks back to server
//            val changedDates = dates
//                .zip(updatedTasksByDate)
//                .filterIndexed { index, (_, tasks) -> serverTasksByDate[index] != tasks }
//                .toMap()
//            if (changedDates.isNotEmpty())
//                sendTasks(changedDates)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            launch {
//                app.snackbarHostState
//                    .showSnackbar("Error syncing: ${e.message ?: "Unknown error"}", withDismissAction = true)
//            }
//            isError.emit(true)
//        } finally {
//            inProgress.emit(false)
//        }
    }
}
