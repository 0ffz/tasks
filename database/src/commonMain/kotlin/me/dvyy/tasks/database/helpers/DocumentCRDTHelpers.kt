package me.dvyy.tasks.database.helpers

import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document
import kotlin.math.max

/**
 * Adds a copy of the current document's keys under a `timestamp` document key.
 */
fun Document.applyTimestamps(timestamp: Long = System.currentTimeMillis()): Document {
    val values = deepKeys().map { it.first to timestamp }.toTypedArray()
    put("timestamps", documentOf(*values))
    return this
}

/**
 * Gets keys, with nested documents returned as period separated strings.
 */
fun Document.deepKeys(): List<Pair<String, Any>> {
    val result = mutableListOf<Pair<String, Any>>()

    fun traverse(doc: Document, prefix: String = "") {
        for (pair in doc) {
            val key = pair.first
            val value = pair.second
            when (value) {
                is Document -> traverse(value, if (prefix.isEmpty()) key else "$prefix.$key")
                else -> result.add(Pair(if (prefix.isEmpty()) key else "$prefix.$key", value))
            }
        }
    }

    traverse(this)
    return result
}


/**
 * Merges two documents with CRDT logic, where the latest timestamp for each field wins,
 * including merging the `timestamps` document values.
 */
fun Document.conflictFreeMerge(other: Document): Document {
    // Get timestamps from both documents
    val thisTimestamps = this.get("timestamps", Document::class.java) ?: documentOf()
    val otherTimestamps = other.get("timestamps", Document::class.java) ?: documentOf()

    // Create a merged timestamps document
    val merged = documentOf()
    val mergedTimestamps = documentOf()

    // Process all fields from both documents
    val allFields = mutableSetOf<String>()
    this.deepKeys().forEach { if (!it.first.startsWith("timestamps.")) allFields.add(it.first) }
    other.deepKeys().forEach { if (!it.first.startsWith("timestamps.")) allFields.add(it.first) }

    // Skip the timestamps field itself in our conflict resolution
    for (field in allFields) {
        val thisTimestamp = thisTimestamps.get(field) as? Long ?: 0L
        val otherTimestamp = otherTimestamps.get(field) as? Long ?: 0L

        // The field with the latest timestamp wins
        val winner = when {
            otherTimestamp > thisTimestamp -> other
            otherTimestamp == thisTimestamp -> TODO("Handle conflicts between documents with the same timestamp")
            else -> this
        }
        merged.put(field, winner.get(field))
        mergedTimestamps.put(field, max(thisTimestamp, otherTimestamp))
    }

    // Update the timestamps document
    merged.put("timestamps", mergedTimestamps)

    return merged
}
