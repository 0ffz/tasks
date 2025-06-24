package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val name: String,
    val done: Boolean = false,
)