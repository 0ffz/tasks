package me.dvyy.tasks.tasks.data

import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.model.Changelist
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.ProjectNetworkModel
import me.dvyy.tasks.model.sync.TaskNetworkModel

class SyncConfig(
    val url: String,
    val auth: DigestAuthCredentials,
)

class SyncAPI(
    private val http: AppHTTP,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun pullTaskChanges(list: ListKey, lastSync: Instant?): Changelist<TaskNetworkModel> {
        TODO()
        return http.client.get("changes") {
            if (lastSync != null) parameter("lastSyncDate", lastSync.toString())
//            parameter("upToDate", upTo.toString())
//            parameter("lists", lists)
        }.body<Changelist<TaskNetworkModel>>()
    }

    suspend fun pushTaskChanges(changelist: Changelist<TaskNetworkModel>) {
        http.client.post("changes") {
            setBody(changelist)
        }
    }

    suspend fun pullProjectChanges(lastSync: Instant?): Changelist<ProjectNetworkModel> {
        TODO()
    }

    suspend fun pushProjectChanges(changelist: Changelist<ProjectNetworkModel>) {
        TODO()
    }
}

class TasksNetworkDataSource(
    private val http: AppHTTP,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    val sync: SyncAPI = SyncAPI(http, ioDispatcher)

//    suspend fun fetchTasksForLists(keys: List<ListKey>): List<TaskListModel> = withContext(ioDispatcher) {
//        http.client.get("lists") {
//            parameter("lists", keys.joinToString(separator = ",") { it.uniqueIdentifier })
//        }.body<List<TaskListModel>>()
//    }
//
//    suspend fun fetchTasksForList(key: ListKey): List<TaskModel> = withContext(ioDispatcher) {
//        http.client.get("list/${key.uniqueIdentifier}").body()
//    }
//    suspend fun fetchProjects(): List<String> = TODO()
//
//    suspend fun sendTaskLists(tasksPerDate: Map<ListKey, TaskListModel>) = withContext(ioDispatcher) {
//        http.client.post("lists") {
//            contentType(ContentType.Application.Json)
//            setBody(tasksPerDate)
//        }
//    }
}
