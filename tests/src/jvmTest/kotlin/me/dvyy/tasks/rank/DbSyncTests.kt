package me.dvyy.tasks.rank

import kotlinx.coroutines.test.runTest
import me.dvyy.tasks.ClientServerHelpers

class DbSyncTests {
    val app = ClientServerHelpers(
        "/example/client.db",
        "/example/server.db",
    )

    //TODO get actual client/server database examples for testing, for now this is mostly just for pulling a database
    // experiencing issues from prod and testing sync on it
    fun `should load db`() = runTest {
        val projects = app.clientDb.read {
            app.client.query.projects.getAll()
        }
        println(projects.size)
        app.syncClient.initialize()
        app.syncClient.sync()
    }
}