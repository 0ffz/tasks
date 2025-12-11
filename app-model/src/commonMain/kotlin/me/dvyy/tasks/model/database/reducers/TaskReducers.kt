package me.dvyy.tasks.model.database.reducers

import me.dvyy.syncengine.reducers.MutableReducers
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.actions.MoveTaskAction

fun MutableReducers.taskReducers(db: AppQueries) {
    reduce<MoveTaskAction> {
        db.rank.moveTaskToList(it.task.uuid, it.toList.uuid)
    }
}