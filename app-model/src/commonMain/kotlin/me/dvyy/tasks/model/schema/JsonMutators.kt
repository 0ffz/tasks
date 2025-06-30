package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.db.Database
import me.dvyy.syncengine.schema.Mutators
import me.dvyy.syncengine.schema.minus
import me.dvyy.tasks.model.mutators.DeleteRowMutator
import me.dvyy.tasks.model.mutators.JsonCreateMutator
import me.dvyy.tasks.model.mutators.JsonPatchMutator
import me.dvyy.tasks.model.mutators.Mutator
import kotlin.uuid.Uuid

class JsonMutators<T>(
    val dao: JsonDAO<T>,
    val mutators: Mutators<Mutator>,
) {
    suspend fun create(element: T) = mutators.invoke(
        JsonCreateMutator(
            dao.table.name,
            Uuid.Companion.random(),
            dao.json.encodeToJsonElement(dao.serializer, element)
        )
    )

    suspend fun delete(uuid: Uuid) = mutators.invoke(
        DeleteRowMutator(
            dao.table.name,
            uuid
        )
    )

    suspend fun patch(uuid: Uuid, element: T) {
        val existing = Database.read { dao.getJsonElement(uuid) }
        val new = dao.json.encodeToJsonElement(dao.serializer, element)
        val patch = new - existing
        mutators.invoke(
            JsonPatchMutator(
                table = dao.table.name,
                id = uuid,
                patch = patch
            )
        )
    }
}
