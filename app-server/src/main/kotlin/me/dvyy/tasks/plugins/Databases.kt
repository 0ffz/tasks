package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskChangeList
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

            get("/changes") {
                val lastSyncDate = call.parameters["lastSyncDate"]?.let { it1 -> Instant.parse(it1) }
                val upToDate = Instant.parse(call.parameters["upToDate"]!!)
                val lists = call.parameters["lists"]
                    ?.split(",")
                    ?.map { ListKey.fromIdentifier(it) } ?: error("Lists must be specified")
                val changelist = taskService.getChangelistFor(lists, lastSyncDate, upToDate)
                call.respond(HttpStatusCode.OK, changelist)
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
