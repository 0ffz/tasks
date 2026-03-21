package me.dvyy.tasks.model.database.reducers

import co.touchlab.kermit.Logger
import me.dvyy.syncengine.reducers.MutableReducers
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveTaskAction

fun MutableReducers.taskReducers(db: AppQueries) {
    reduce<MoveTaskAction> {
        if (it.toList != null && it.toTask == null) {
            db.childOf.moveTaskToList(it.task.uuid, it.toList.uuid)
            val nextRank = db.childOf.getRankAfterLast(it.toList.uuid)
            db.childOf.setRank(it.task.uuid, nextRank)
        }
        if (it.toTask != null) db.childOf.moveToTask(it.task.uuid, it.toTask.uuid)
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