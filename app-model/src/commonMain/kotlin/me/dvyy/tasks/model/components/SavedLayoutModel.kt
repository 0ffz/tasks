package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SavedLayoutModel(
    val title: String,
    val layout: JsonElement,
) {
    val type: String = "layout"
}