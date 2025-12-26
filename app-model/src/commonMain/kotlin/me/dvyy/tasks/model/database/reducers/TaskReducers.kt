package me.dvyy.tasks.model.database.reducers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import me.dvyy.syncengine.reducers.MutableReducers
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveTaskAction
import me.dvyy.tasks.model.rank.RankFunctions
import kotlin.uuid.Uuid

fun MutableReducers.taskReducers(db: AppQueries) {
    reduce<MoveTaskAction> {
        if (it.toList != null) {
            db.rank.moveTaskToList(it.task.uuid, it.toList.uuid)
            val nextRank = db.rank.getRankAfterLast(it.toList.uuid)
            db.rank.setRank(it.task.uuid, nextRank)

        }
        if (it.toTask != null) db.rank.moveToTask(it.task.uuid, it.toTask.uuid)
    }
    reduce<CreateTaskAction> { (task, atEnd) ->
        val rank =
            (if (atEnd) db.rank.getLastRankInList(task.parent)?.let { RankFunctions.getRankAfter(it) }
            else db.rank.getFirstRankInList(task.parent)?.let { RankFunctions.getRankBefore(it) })
                ?: RankFunctions.middleChar.toString()
        db.tasks.create(Uuid.random(), Json.encodeToJsonElement(task.copy(rank = rank)))
    }
}