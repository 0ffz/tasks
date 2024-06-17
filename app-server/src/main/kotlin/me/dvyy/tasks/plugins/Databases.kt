package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.ListKey
import org.jetbrains.exposed.sql.Database

fun ApplicationCall.getDate(): LocalDate {
    val epochDays = parameters["epochDays"]?.toInt() ?: throw IllegalArgumentException("Invalid date")
    return LocalDate.fromEpochDays(epochDays)
}

fun Application.configureDatabases() {
    val database = Database.connect(
        url = environment.config.property("database.url").getString(),
    )
    val taskService = TaskService(database)
    routing {
        authenticate {
            get("/test") {
                call.respond(HttpStatusCode.OK, "Hello, ${call.principal<UserSession>()}!")
            }

            route("/changes") {
                fun ApplicationCall.getListKey(): ListKey {
                    return ListKey.fromIdentifier(parameters.getOrFail("id"))
                }
                /*{
                    val epochDays = parameters["epochDays"]?.toInt() ?: throw IllegalArgumentException("Invalid date")
                    return LocalDate.fromEpochDays(epochDays)
                }*/
                get("/list/{id?}") {
                    val key = call.getListKey()
                    val lastSyncDate = call.parameters.getOrFail<Instant>("lastSyncDate")
                    val upToDate = call.parameters.getOrFail<Instant>("upToDate")
//                    val lists = call.parameters["lists"]
//                        ?.split(",")
//                        ?.map { ListKey.fromIdentifier(it) } ?: error("Lists must be specified")
                    val changelist = taskService.getMessages(lists, lastSyncDate, upToDate)
                    call.respond(HttpStatusCode.OK, changelist)
                }

            }

            post("/changes") {
                val changelist = call.receive<TaskChangeList>()
                taskService.update(changelist)
            }
            // Single date routes
//            get("/date/{epochDays}") {
//                val date = call.getDate()
//                call.respond(HttpStatusCode.OK, taskService.tasksForList(date))
//            }
//
//            post("/date/{epochDays}") {
//                val date = call.getDate()
//                val tasks = call.receive<List<TaskModel>>()
//                taskService.update(date, tasks)
//                call.respond(HttpStatusCode.Created)
//            }
//
//            // Bulk date routes
//            get("/lists") {
//                val dates = call.parameters["dates"]
//                    ?.split(",")
//                    ?.map { ListKey.fromIdentifier(it) } ?: error("Lists must be specified")
//                val tasks = dates.map {
//                    async { taskService.tasksForList(it) }
//                }.awaitAll()
//                call.respond(HttpStatusCode.OK, tasks)
//            }
//
//            post("/lists") {
//                val tasksPerDate = call.receive<Map<LocalDate, List<TaskModel>>>()
//                tasksPerDate.forEach { (date, tasks) ->
//                    taskService.update(date, tasks)
//                }
//                call.respond(HttpStatusCode.Created)
//            }
        }
    }
}
