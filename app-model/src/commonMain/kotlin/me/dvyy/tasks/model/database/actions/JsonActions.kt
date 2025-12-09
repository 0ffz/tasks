package me.dvyy.tasks.model.database.actions

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.schema.minus
import me.dvyy.tasks.model.database.dao.JsonDataDAO
import kotlin.uuid.Uuid

class JsonActions<T>(
    val db: Database,
    val dao: JsonDataDAO<T>,
    val actions: Actions,
) {
    suspend fun create(element: T) = actions.invoke(
        JsonCreateAction(
//            dao.table.name,
            Uuid.random(),
            dao.json.encodeToJsonElement(dao.serializer, element)
        )
    )

    suspend fun delete(uuid: Uuid) = actions.invoke(
        DeleteRowAction(
//            dao.table.name,
            uuid
        )
    )

    suspend fun patch(uuid: Uuid, element: T) {
        val existing = db.read { dao.getJsonElement(uuid) }
        val new = dao.json.encodeToJsonElement(dao.serializer, element)
        val patch = new - existing
        actions.invoke(
            JsonPatchAction(
//                table = dao.table.name,
                id = uuid,
                patch = patch
            )
        )
    }
}