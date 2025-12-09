package me.dvyy.tasks.model.database

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

val AppSchema = schema(
    shared = setOf(NotesTable),
    views = setOf(TasksView),
)
