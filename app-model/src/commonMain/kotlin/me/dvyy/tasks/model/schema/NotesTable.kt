package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.db.tables.Table

object NotesTable : Table(
    """
    CREATE TABLE IF NOT EXISTS notes (
        id BLOB PRIMARY KEY,
        data BLOB
    )
    """.trimIndent()
)

