package me.dvyy.tasks.model.mutators

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.db.WriteTransaction
import kotlin.uuid.Uuid

@Serializable
class DeleteRowMutator(
    val table: String,
    val id: Uuid,
): Mutator {
    context(tx: WriteTransaction)
    override fun mutate() {
        TODO("Call DAO")
    }
}