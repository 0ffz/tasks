package me.dvyy.tasks.model.database.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.dvyy.syncengine.actions.Action
import kotlin.uuid.Uuid

@Serializable
@SerialName("patch")
data class JsonPatchAction(
//    val table: String,
    val id: Uuid,
    val patch: JsonElement,
) : Action