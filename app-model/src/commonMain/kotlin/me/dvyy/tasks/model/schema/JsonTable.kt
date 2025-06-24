package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.db.tables.Table

class JsonTable(name: String) : Table(
    """
    CREATE TABLE IF NOT EXISTS $name (
        id BLOB PRIMARY KEY,
        data BLOB
    )
    """.trimIndent(),
) {
    val columns: Set<String> = setOf("id", "data")
}