package me.dvyy.tasks.model.mutators

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.dvyy.syncengine.db.WriteTransaction
import kotlin.uuid.Uuid

@Serializable
class JsonPatchMutator(
    val table: String,
    val id: Uuid,
    val patch: JsonElement,
) : Mutator {
    context(tx: WriteTransaction)
    override fun mutate() {
        TODO("Call DAO")
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