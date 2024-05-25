package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.model.Changelist
import me.dvyy.tasks.model.Message

object Synchronizer {
    suspend fun <T> sync(
        getLocalChanges: suspend () -> Changelist<T>,
        fetchServerChanges: suspend () -> Changelist<T>,
        pushChangelist: suspend (Changelist<T>) -> Unit,
        applyChanges: suspend (List<Message<T>>) -> Unit,
    ) {
        val local = getLocalChanges()
        val network = fetchServerChanges()
        val updates = network.compactedMap().toMutableMap()
        local.compactedMap().forEach { (key, message) ->
            val update = updates[key] ?: return@forEach
            if (update.timestamp < message.timestamp) updates.remove(key)
        }
        applyChanges(updates.values.toList())
        pushChangelist(local) //TODO remove changes we know were overridden in pull
    }
}
