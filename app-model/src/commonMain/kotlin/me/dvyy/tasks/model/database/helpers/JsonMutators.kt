package me.dvyy.tasks.model.database.helpers

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.schema.Mutators
import me.dvyy.syncengine.schema.minus
import me.dvyy.tasks.model.database.dao.JsonDataDAO
import me.dvyy.tasks.model.database.mutators.DeleteRowMutator
import me.dvyy.tasks.model.database.mutators.JsonCreateMutator
import me.dvyy.tasks.model.database.mutators.JsonPatchMutator
import me.dvyy.tasks.model.database.mutators.Mutator
import kotlin.uuid.Uuid

class JsonMutators<T>(
    val db: Database,
    val dao: JsonDataDAO<T>,
    val mutators: Mutators<Mutator>,
) {
    suspend fun create(element: T) = mutators.invoke(
        JsonCreateMutator(
//            dao.table.name,
            Uuid.random(),
            dao.json.encodeToJsonElement(dao.serializer, element)
        )
    )

    suspend fun delete(uuid: Uuid) = mutators.invoke(
        DeleteRowMutator(
//            dao.table.name,
            uuid
        )
    )

    suspend fun patch(uuid: Uuid, element: T) {
        val existing = db.read { dao.getJsonElement(uuid) }
        val new = dao.json.encodeToJsonElement(dao.serializer, element)
        val patch = new - existing
        mutators.invoke(
            JsonPatchMutator(
//                table = dao.table.name,
                id = uuid,
                patch = patch
            )
        )
    }
}