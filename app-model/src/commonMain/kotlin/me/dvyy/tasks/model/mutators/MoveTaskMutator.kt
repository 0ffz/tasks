package me.dvyy.tasks.model.mutators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.schema.AbstractMutator
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.database.AppDAO

@Serializable
@SerialName("move-task")
class MoveTaskMutator(
    val task: TaskId,
    val toList: ListId,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDAO) {
        db.rank.moveTaskToList(task.uuid, toList.uuid)
    }

    override fun reduce(previous: AbstractMutator<AppDAO>): AbstractMutator<AppDAO>? {
        if (previous !is MoveTaskMutator) return null
        if (previous.task == task) return this
        return null
    }
}
