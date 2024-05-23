package me.dvyy.tasks.tasks.data

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskChangeList
import me.dvyy.tasks.model.Timestamped

class TasksSynchronizer {
    suspend fun sync(
        getLocalTasks: suspend () -> Map<ListKey, TaskListModel>,
        pullChangelist: suspend () -> TaskChangeList,
        applyChangelist: suspend (TaskChangeList) -> Unit,
        getLocalDeletions: suspend () -> Set<Uuid>,
        pushChangelist: suspend (TaskChangeList) -> Unit,
    ) {
//        try {
        val now = Clock.System.now()
        val changelist = pullChangelist()
        val localUuidToModel = getLocalTasks()
            .values
            .flatMap { it.tasks }
            .associateBy { it.uuid }

        fun Instant.modifiedAfter(task: Uuid): Boolean {
            val localModified = localUuidToModel[task]?.lastModified ?: return true
            return localModified <= this
        }
        // Latest write wins, ignore any tasks in changelist modified BEFORE local tasks,
        // this applies to deletions too.
        val resolvedChangelist = changelist.copy(
            updatedTasks = changelist.updatedTasks.mapValues { (_, value) ->
                value.filter { it.lastModified.modifiedAfter(it.uuid) }
            },
            deletedTasks = changelist.deletedTasks.filter { it.timestamp.modifiedAfter(it.data) }
        )

        // Update local tasks based on changelist
        applyChangelist(resolvedChangelist)

        // Calculate a local changelist, ignoring resolved tasks just sent by server
        val modifiedByServer = resolvedChangelist.updatedTasks
            .values
            .flatten()
            .map { it.uuid }
            .toSet()

        val deletedByServer = resolvedChangelist.deletedTasks.map { it.data }.toSet()

        val mergedLocalTasks = getLocalTasks()
        val localChangelist = TaskChangeList(
            //TODO change return type of getLocalDeletions()
            deletedTasks = getLocalDeletions()
                .filter { it !in modifiedByServer }
                .map { Timestamped(it, localUuidToModel[it]!!.lastModified) },
            updatedTasks = mergedLocalTasks
                .mapNotNull { (key, model) ->
                    val filteredTasks = model.tasks.filter { it.uuid !in deletedByServer }
                    if (filteredTasks.isEmpty()) return@mapNotNull null
                    key to model.copy(tasks = filteredTasks).tasks
                }.toMap()
        )
        pushChangelist(localChangelist)
    }
}
