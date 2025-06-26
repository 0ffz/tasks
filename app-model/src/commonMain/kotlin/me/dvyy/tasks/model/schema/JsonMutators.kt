package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.schema.Mutators
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

    suspend fun patch(uuid: Uuid, element: T) = mutators.invoke(
        JsonPatchMutator(
            dao.table.name,
            uuid,
            dao.json.encodeToJsonElement(dao.serializer, element)
        )
    )
}
