package me.dvyy.tasks.sync.data

class SyncConfig(
    val url: String,
    val loadToken: suspend () -> String?,
    val refreshToken: suspend () -> String?,
)