package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val title: String,
) {
    val type: String = "project"
}