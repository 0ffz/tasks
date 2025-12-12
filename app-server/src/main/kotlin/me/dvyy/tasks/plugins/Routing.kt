package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.protobuf.ProtoBuf
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.routes.login

fun Application.configureRouting(
    userRepository: UserRepository,
    syncServer: SyncServer,
    jwtConfig: JWTConfig,
    ldapConfig: LDAPConfig,
) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
    }
    routing {
        login(userRepository, ldapConfig, jwtConfig)
        authenticate {
            get("/auth/check") {
                call.respond(HttpStatusCode.OK)
            }
            webSocket("/sync") {
                val session =
                    call.principal<UserSession>() ?: return@webSocket call.respond(HttpStatusCode.Unauthorized)
                runCatching {
                    val initialRequest = receiveDeserialized<SyncRequest>()
                    syncServer.streamingSync(session.identity, initialRequest, incoming.consumeAsFlow().map {
                        converter!!.deserialize<SyncRequest>(it)
                    }).collect {
                        sendSerialized<SyncResult>(it)
                    }
                }.onFailure {
                    println(it.stackTraceToString())
                }
            }
        }
    }
}
