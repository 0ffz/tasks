package me.dvyy.tasks.model.database

import me.dvyy.syncengine.jsonactions.actions.DeleteRowAction
import me.dvyy.syncengine.jsonactions.actions.JsonCreateAction
import me.dvyy.syncengine.jsonactions.actions.JsonPatchAction
import me.dvyy.syncengine.jsonactions.reducers.jsonReducers
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.reducers.reducers
import me.dvyy.syncengine.reducers.syncProtocol
import me.dvyy.syncengine.schema.Schema
import me.dvyy.syncengine.schema.jsonTable
import me.dvyy.syncengine.schema.schema
import me.dvyy.syncengine.schema.view
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveTaskAction
import me.dvyy.tasks.model.database.reducers.taskReducers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val NotesTable = jsonTable("notes")

val TasksView = view("tasks", NotesTable) {
    text("text")
    integer("done")
    text("parent")
    text("highlight")
    text("rank")
}
val ProjectsView = view("projects", NotesTable, where = "data ->> '$.type' = 'project'") {
    text("title")
}

val AppSchema = schema(
    shared = setOf(NotesTable),
    views = setOf(TasksView, ProjectsView),
    protocol = syncProtocol {
        // Json actions
        action<DeleteRowAction>(1)
        action<JsonPatchAction>(2)
        action<JsonCreateAction>(3)

        // Task interactions
        action<MoveTaskAction>(100)
        action<CreateTaskAction>(101)
    }
)

fun commonSyncModule() = module {
    singleOf(::AppQueries)
    single<Schema> { AppSchema }
    single<Reducers> {
        reducers {
            val queries = get<AppQueries>()
            jsonReducers(listOf(queries.tasks))
            taskReducers(queries)
        }
    }
}