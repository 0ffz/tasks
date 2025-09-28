package me.dvyy.tasks.plugins

import me.dvyy.sqlite.Identity

data class UserSession(
    val username: String,
    val identity: Identity,
)