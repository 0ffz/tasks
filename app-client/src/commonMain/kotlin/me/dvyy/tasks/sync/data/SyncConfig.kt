package me.dvyy.tasks.sync.data

import io.ktor.http.*

class SyncConfig(
    val url: String,
    val loadToken: suspend () -> String?,
    val refreshToken: suspend () -> String?,
) {
    val websocketUrl = run {
        val url = Url(url)
        when (url.protocolOrNull) {
            URLProtocol.HTTPS -> buildUrl {
                protocol = URLProtocol.WSS
                host = url.host
                port = url.port
            }

            else -> url
        }
    }.toString()
}