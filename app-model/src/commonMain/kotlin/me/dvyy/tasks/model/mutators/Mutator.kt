package me.dvyy.tasks.model.mutators

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.db.WriteTransaction

@Serializable
sealed interface Mutator {
    context(tx: WriteTransaction)
    fun mutate()

    fun reduce(previous: Mutator): Mutator? = null
}
