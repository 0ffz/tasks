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
@SerialName("create")
class JsonCreateMutator(
    val table: String,
    val id: @Serializable(with = UuidSerializer::class) Uuid,
    val data: @Serializable(with = JsonElementAsStringSerializer::class) JsonElement,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDAO) {
        db.tasks.create(id, data)
    }

//    companion object {
//        inline fun <reified T> fromData(
//            data: T,
//            serializer: KSerializer<T> = kotlinx.serialization.serializer<T>()
//        ) {
//            JsonPatchMutator()
//        }
//    }
}
