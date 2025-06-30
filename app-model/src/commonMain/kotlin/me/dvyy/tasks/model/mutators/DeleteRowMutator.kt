package me.dvyy.tasks.model.mutators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.schema.UuidSerializer
import me.dvyy.tasks.model.database.AppDAO
import kotlin.uuid.Uuid

@Serializable
@SerialName("delete")
class DeleteRowMutator(
    val table: String,
    val id: Uuid,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDAO) {
        db.tasks.delete(id)
    }
}
