package me.dvyy.tasks.model.database.reducers

import me.dvyy.syncengine.reducers.MutableReducers
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveChildAction

fun MutableReducers.taskReducers(db: AppQueries) {
    reduce<MoveChildAction> {
        if (it.toParent != null && it.atChild == null) {
            db.childOf.moveTaskToList(it.item, it.toParent)
            val nextRank = db.childOf.getRankAfterLast(it.toParent)
            db.childOf.setRank(it.item, nextRank)
        }
        if (it.atChild != null) db.childOf.moveToTask(it.item, it.atChild)
    }
    reduce<CreateTaskAction> {
        val list = it.parent
        db.tasks.create(it.uuid, it.task)
        db.childOf.moveTaskToList(it.uuid, list, it.atEnd)
    }
}