package me.dvyy.tasks.tasks.data

import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskChangeList
import me.dvyy.tasks.model.TaskModel

class SyncConfig(
    val url: String,
    val auth: DigestAuthCredentials,
)

class TasksNetworkDataSource(
    private val http: AppHTTP,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend fun fetchTasksForLists(keys: List<ListKey>): List<TaskListModel> = withContext(ioDispatcher) {
        http.client.get("lists") {
            parameter("lists", keys.joinToString(separator = ",") { it.uniqueIdentifier })
        }.body<List<TaskListModel>>()
    }

    suspend fun fetchTasksForList(key: ListKey): List<TaskModel> = withContext(ioDispatcher) {
        http.client.get("list/${key.uniqueIdentifier}").body()
    }

    suspend fun pullChangelist(lists: List<ListKey>, lastSync: Instant?, upTo: Instant): TaskChangeList {
        return http.client.get("changes") {
            if (lastSync != null) parameter("lastSyncDate", lastSync.toString())
            parameter("upToDate", upTo.toString())
            parameter("lists", lists)
        }.body<TaskChangeList>()
    }

    suspend fun pushChangelist(changelist: TaskChangeList) {
        http.client.post("changes") {
            setBody(changelist)
        }
    }

    suspend fun fetchProjects(): List<String> = TODO()

    suspend fun sendTaskLists(tasksPerDate: Map<ListKey, TaskListModel>) = withContext(ioDispatcher) {
        http.client.post("lists") {
            contentType(ContentType.Application.Json)
            setBody(tasksPerDate)
        }
    }
}
