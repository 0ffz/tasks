package me.dvyy.tasks.database.model

import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document

class NoteFrontMatter internal constructor(
    override val document: Document,
) : DocumentWrapper {
    var managed: Boolean? by document
    var projects: List<String> by document
    var sortOrder: String? by document

    fun entries(): List<Pair<String, Any>> = document.map { it.first to it.second }

    operator fun set(key: String, value: Any?) {
        document.put(key, value)
    }

    companion object {
        fun new(create: NoteFrontMatter.() -> Unit): NoteFrontMatter {
            return NoteFrontMatter(documentOf()).apply(create)
        }
    }
}
