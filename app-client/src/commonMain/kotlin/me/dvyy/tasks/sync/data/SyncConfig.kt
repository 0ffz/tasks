package me.dvyy.tasks.sync.data

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.isSecure

class SyncConfig(
    val url: String,
    val loadToken: suspend () -> String?,
    val refreshToken: suspend () -> String?,
) {
    val websocketUrl = run {
        val url = Url(url)
        URLBuilder(url).apply {
            protocol = if (url.protocol.isSecure()) URLProtocol.WSS else URLProtocol.WS
        }.buildString()
    }
}