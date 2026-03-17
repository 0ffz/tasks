package me.dvyy.tasks.plugins

import co.touchlab.kermit.Logger
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.deserialize
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.converter
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.protobuf.ProtoBuf
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.routes.login
import org.slf4j.MDC

//val ApplicationCall.trace
//    get() = attributes.get(StructuredLoggerAttr)
//private val StructuredLoggerAttr = AttributeKey<TracingEvent.Span.Local>("StructuredLogger")

fun Application.configureRouting(
    userRepository: UserRepository,
    syncServer: SyncServer,
    jwtConfig: JWTConfig,
    ldapConfig: LDAPConfig,
) {
    fun ApplicationCall.setupContext() {
        MDC.put("user", principal<UserSession>()?.username ?: "anonymous")
        val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        MDC.put("callId", buildString { repeat(5) { append(alphabet.random()) } })
    }
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
    }

    // Call logging and attach context
    intercept(ApplicationCallPipeline.Call) {
        call.setupContext()
        withContext(MDCContext()) {
            proceed()
        }
        Logger.v { "(${call.request.httpMethod.value} ${call.request.uri}): ${call.response.status()}" }
    }

    routing {
        login(userRepository, ldapConfig, jwtConfig)
        authenticate {
            get("/auth/check") {
                call.respond(HttpStatusCode.OK)
            }
            webSocket("/sync") {
                call.setupContext()
                val session = call.principal<UserSession>()
                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized)
                withContext(MDCContext()) {
                    Logger.i { "Starting sync session..." }
                    runCatching {
                        val initialRequest = receiveDeserialized<SyncRequest>()
                        syncServer.streamingSync(session.identity, initialRequest, incoming.consumeAsFlow().map {
                            converter!!.deserialize<SyncRequest>(it)
                        }).collect {
                            sendSerialized<SyncResult>(it)
                        }
                    }.onFailure {
                        Logger.e(it) { "Sync failed" }
                    }
                    Logger.i { "Sync session complete" }
                }
            }
        }
    }
}
