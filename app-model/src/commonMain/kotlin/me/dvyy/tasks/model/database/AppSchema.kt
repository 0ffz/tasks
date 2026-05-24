package me.dvyy.tasks.model.database

import me.dvyy.syncengine.jsonactions.actions.DeleteEntityAction
import me.dvyy.syncengine.jsonactions.actions.DeleteRowAction
import me.dvyy.syncengine.jsonactions.actions.JsonCreateAction
import me.dvyy.syncengine.jsonactions.actions.JsonPatchAction
import me.dvyy.syncengine.jsonactions.actions.JsonSetAction
import me.dvyy.syncengine.jsonactions.reducers.jsonReducers
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.reducers.reducers
import me.dvyy.syncengine.reducers.syncProtocol
import me.dvyy.syncengine.schema.Schema
import me.dvyy.syncengine.schema.jsonTable
import me.dvyy.syncengine.schema.schema
import me.dvyy.syncengine.schema.view
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveChildAction
import me.dvyy.tasks.model.database.reducers.taskReducers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance

val NotesTable = jsonTable("notes") {
    index("parent", "data ->> 'parent'")
}

val ChildOfTable = jsonTable("child_of_json") {
    index("parent_rank", "data ->> 'parent', data ->> 'rank'", unique = true)
}

val TasksView = view("tasks", NotesTable) {
    text("text")
    integer("done")
    text("parent")
    text("highlight")
    text("rank")
}

val ChildOfView = view("child_of", ChildOfTable) {
    text("parent")
    text("rank")
}

val ProjectsView = view("projects", NotesTable, where = "data ->> '$.type' = 'project'") {
    text("title")
}

val AppSchema = schema(
    shared = setOf(NotesTable, ChildOfTable),
    views = setOf(TasksView, ProjectsView, ChildOfView),
    protocol = syncProtocol {
        // Json actions
        action<DeleteRowAction>(1)
        action<JsonPatchAction>(2)
        action<JsonCreateAction>(3)
        action<DeleteEntityAction>(4)
        action<JsonSetAction>(5)

        // Task interactions
        action<MoveChildAction>(100)
        action<CreateTaskAction>(101)
    }
)

fun commonSyncModule() = DI.Module("common sync") {
    bindSingletonOf(::AppQueries)
    bindSingleton<Schema> { AppSchema }
    bindSingleton<Reducers> {
        reducers {
            val queries = instance<AppQueries>()
            jsonReducers(listOf(queries.tasks, queries.childOf.queries))
            taskReducers(queries)
        }
    }
}