package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.schema.table

val NotesTable = table("notes") {
    text("text")
    integer("done")
    blob("parent")
}

val SubtaskTable = table("subtask") {
    blob("parent")
    blob("child")
}