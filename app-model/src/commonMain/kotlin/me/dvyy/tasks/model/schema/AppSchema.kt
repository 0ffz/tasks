package me.dvyy.tasks.model.schema

import me.dvyy.sqlite.tables.View
import me.dvyy.syncengine.schema.jsonTable
import me.dvyy.syncengine.schema.schema
import me.dvyy.syncengine.schema.view

val NotesTable = jsonTable("notes")

val TasksView = view("tasks", NotesTable) {
    text("text")
    integer("done")
    text("parent")
    text("rank")
}

val MutatorsDebugView = View(
    "mutators_json",
    """
        SELECT json(data), length(data) FROM mutators
    """.trimIndent(),
    setOf()
)

val AppSchema = schema(
    shared = setOf(NotesTable),
    views = setOf(TasksView, MutatorsDebugView),
)
