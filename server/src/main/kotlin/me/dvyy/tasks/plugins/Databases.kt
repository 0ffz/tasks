package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Task
import org.jetbrains.exposed.sql.Database

fun ApplicationCall.getDate(): LocalDate {
    val epochDays = parameters["epochDays"]?.toInt() ?: throw IllegalArgumentException("Invalid date")
    return LocalDate.fromEpochDays(epochDays)
}

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    val taskService = TaskService(database)
    routing {
        post("/date/{epochDays}") {
            val date = call.getDate()
            val tasks = call.receive<List<Task>>()
            taskService.update(date, tasks)
            call.respond(HttpStatusCode.Created)
        }
        get("/date/{epochDays}") {
            val date = call.getDate()
            call.respond(HttpStatusCode.OK, taskService.tasksForDate(date))
        }

        post("/dates") {
            val tasksPerDate = call.receive<Map<LocalDate, List<Task>>>()
            tasksPerDate.forEach { (date, tasks) ->
                taskService.update(date, tasks)
            }
            call.respond(HttpStatusCode.Created)
        }
        get("/dates") {
            val dates = call.parameters["dates"]
                ?.split(",")
                ?.map { LocalDate.parse(it) } ?: error("Dates must be specified")
            val tasks = dates.map {
                async { taskService.tasksForDate(it) }
            }.awaitAll()
            call.respond(HttpStatusCode.OK, tasks)
        }

//        // Create user
//        post("/users") {
//            val user = call.receive<ExposedUser>()
//            val id = userService.create(user)
//            call.respond(HttpStatusCode.Created, id)
//        }
//
//            // Read user
//        get("/users/{id}") {
//            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
//            val user = userService.read(id)
//            if (user != null) {
//                call.respond(HttpStatusCode.OK, user)
//            } else {
//                call.respond(HttpStatusCode.NotFound)
//            }
//        }
//
//            // Update user
//        put("/users/{id}") {
//            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
//            val user = call.receive<ExposedUser>()
//            userService.update(id, user)
//            call.respond(HttpStatusCode.OK)
//        }
//
//            // Delete user
//        delete("/users/{id}") {
//            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
//            userService.delete(id)
//            call.respond(HttpStatusCode.OK)
//        }
    }
}
