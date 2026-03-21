package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
class ChildOfModel(
    val parent: Uuid,
    val rank: String,
)