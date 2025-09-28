package me.dvyy.tasks

import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DbTest {
//    val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
//    private var _clientDb: Database? = null
//    private var _serverDb: ServerDatabase? = null
//
//    val clientDb: Database get() = _clientDb!!
//    val serverDb: ServerDatabase get() = _serverDb!!
//
//    // When your test needs a driver
//    @BeforeAll
//    fun beforeAll() {
//        postgres.start();
//        _clientDb = createClientDatabase(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties(), Database.Schema.synchronous()))
//        _serverDb = createServerDatabase(createDataSource(postgres.getJdbcUrl(), postgres.username, postgres.password))
//    }
//
//    @AfterAll
//    fun afterAll() {
//        postgres.stop()
//    }
//
//    val userSession by lazy { UserSession("test", ServerDataSource(serverDb).getOrCreateUserId("test")) }
//
//    fun now() = Clock.System.now()
}
