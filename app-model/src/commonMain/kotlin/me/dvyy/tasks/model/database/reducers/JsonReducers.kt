package me.dvyy.tasks.model.database.reducers

import me.dvyy.syncengine.reducers.MutableReducers
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.actions.DeleteRowAction
import me.dvyy.tasks.model.database.actions.JsonCreateAction
import me.dvyy.tasks.model.database.actions.JsonPatchAction
import me.dvyy.tasks.model.database.actions.MoveTaskAction

fun MutableReducers.jsonReducers(
    db: AppQueries,
) {
    reduce<DeleteRowAction> {
        db.tasks.delete(it.id)
    }
    reduce<JsonCreateAction> {
        db.tasks.create(it.id, it.data)
    }
    reduce<JsonPatchAction> {
        db.tasks.patch(it.id, it.patch.toString())
    }
    reduce<MoveTaskAction> {
        db.rank.moveTaskToList(it.task.uuid, it.toList.uuid)
    }
}