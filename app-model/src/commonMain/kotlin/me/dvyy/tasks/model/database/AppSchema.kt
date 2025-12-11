package me.dvyy.tasks.model.database

import me.dvyy.syncengine.jsonactions.reducers.jsonReducers
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.reducers.reducers
import me.dvyy.syncengine.schema.Schema
import me.dvyy.syncengine.schema.jsonTable
import me.dvyy.syncengine.schema.schema
import me.dvyy.syncengine.schema.view
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

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

fun commonSyncModule() = module {
    singleOf(::AppQueries)
    single<Schema> { AppSchema }
    single<Reducers> {
        reducers {
            val queries = get<AppQueries>()
            jsonReducers(listOf(queries.tasks))
        }
    }
}