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
        if (it.toList != null) db.rank.moveTaskToList(it.task.uuid, it.toList.uuid)
        if (it.toTask != null) db.rank.moveToTask(it.task.uuid, it.toTask.uuid)
    }
    reduce<CreateTaskAction> { (task) ->
        val lastRank = db.rank.getLastRankInList(task.parent)?.let { RankFunctions.getRankAfter(it) }
            ?: RankFunctions.middleChar.toString()
        db.tasks.create(Uuid.random(), Json.encodeToJsonElement(task.copy(rank = lastRank)))
    }
}