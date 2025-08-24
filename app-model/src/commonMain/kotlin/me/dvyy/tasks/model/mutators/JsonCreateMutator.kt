package me.dvyy.tasks.model.mutators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.tasks.model.database.AppDAO
import kotlin.uuid.Uuid

@Serializable
@SerialName("create")
class JsonCreateMutator(
    val table: String,
    val id: Uuid,
    val data: JsonElement,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDAO) {
        db.tasks.create(id, data)
    }
}
