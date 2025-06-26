package me.dvyy.tasks.model.mutators

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.tasks.model.database.AppDatabase
import kotlin.uuid.Uuid

@Serializable
class JsonCreateMutator(
    val table: String,
    val id: Uuid,
    val data: JsonElement,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate(db: AppDatabase) {
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