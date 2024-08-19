package me.dvyy.tasks

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.datetime.Clock
import me.dvyy.tasks.app.data.createClientDatabase
import me.dvyy.tasks.database.createDataSource
import me.dvyy.tasks.database.createServerDatabase
import me.dvyy.tasks.db.client.Database
import me.dvyy.tasks.db.server.ServerDatabase
import me.dvyy.tasks.plugins.ServerDataSource
import me.dvyy.tasks.plugins.UserSession
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DbTest {
    val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
    private var _clientDb: Database? = null
    private var _serverDb: ServerDatabase? = null

    val clientDb: Database get() = _clientDb!!
    val serverDb: ServerDatabase get() = _serverDb!!

    // When your test needs a driver
    @BeforeAll
    fun beforeAll() {
        postgres.start();
        _clientDb = createClientDatabase(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties(), Database.Schema))
        _serverDb = createServerDatabase(createDataSource(postgres.getJdbcUrl(), postgres.username, postgres.password))
    }

    @AfterAll
    fun afterAll() {
        postgres.stop()
    }

    val userSession by lazy { UserSession("test", ServerDataSource(serverDb).getOrCreateUserId("test")) }

    fun now() = Clock.System.now()
}
