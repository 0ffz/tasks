package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.TaskModel
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
            // Single date routes
            get("/date/{epochDays}") {
                val date = call.getDate()
                call.respond(HttpStatusCode.OK, taskService.tasksForDate(date))
            }

            post("/date/{epochDays}") {
                val date = call.getDate()
                val tasks = call.receive<List<TaskModel>>()
                taskService.update(date, tasks)
                call.respond(HttpStatusCode.Created)
            }

            // Bulk date routes
            get("/dates") {
                val dates = call.parameters["dates"]
                    ?.split(",")
                    ?.map { LocalDate.parse(it) } ?: error("Dates must be specified")
                val tasks = dates.map {
                    async { taskService.tasksForDate(it) }
                }.awaitAll()
                call.respond(HttpStatusCode.OK, tasks)
            }

            post("/dates") {
                val tasksPerDate = call.receive<Map<LocalDate, List<TaskModel>>>()
                tasksPerDate.forEach { (date, tasks) ->
                    taskService.update(date, tasks)
                }
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}
