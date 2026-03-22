package me.dvyy.tasks.model.database.reducers

import co.touchlab.kermit.Logger
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
//        val rank =
//            (if (it.atEnd) db.childOf.getLastRankInList(list)?.let { RankFunctions.getRankAfter(it) }
//            else db.childOf.getFirstRankInList(list)?.let { RankFunctions.getRankBefore(it) })
//                ?: RankFunctions.middleChar.toString()
        db.tasks.create(it.uuid, it.task)
        Logger.w { "TODO: Implement atEnd" }
        db.childOf.moveTaskToList(it.uuid, list) //TODO atEnd
    }
}