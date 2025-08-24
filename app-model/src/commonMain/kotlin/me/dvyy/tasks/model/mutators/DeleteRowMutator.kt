package me.dvyy.tasks.model.mutators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.sqlite.WriteTransaction
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
