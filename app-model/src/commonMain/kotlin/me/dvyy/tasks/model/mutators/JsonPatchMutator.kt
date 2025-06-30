package me.dvyy.tasks.model.mutators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.schema.JsonElementAsStringSerializer
import me.dvyy.syncengine.schema.UuidSerializer
import me.dvyy.tasks.model.database.AppDAO
import kotlin.uuid.Uuid

@Serializable
@SerialName("patch")
class JsonPatchMutator(
    val table: String,
    val id: Uuid,
    val patch: JsonElement,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDAO) {
        db.tasks.patch(id, patch.toString())
    }
}
