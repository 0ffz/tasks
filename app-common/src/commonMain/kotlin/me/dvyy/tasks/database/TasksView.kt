package me.dvyy.tasks.database

import me.dvyy.syncengine.db.tables.View
import me.dvyy.tasks.model.schema.NotesTable

object TasksView : View(
    "tasks",
    """
    SELECT
        cast(data ->> '$.name' as TEXT) AS name,
        cast(data ->> '$.age' as INTEGER) as age
    from $NotesTable
    """.trimIndent(),
    setOf(NotesTable)
) {
}