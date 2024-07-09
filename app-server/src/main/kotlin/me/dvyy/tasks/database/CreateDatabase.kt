package me.dvyy.tasks.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.dvyy.tasks.db.ServerDatabase
import me.dvyy.tasks.db.migrations.Message
import me.dvyy.tasks.db.migrations.Task
import me.dvyy.tasks.db.migrations.TaskList
import me.dvyy.tasks.model.database.Adapters
import javax.sql.DataSource

fun createDataSource(jdbcUrl: String): HikariDataSource {
    val hikariConfig = HikariConfig()
    // See https://jdbc.postgresql.org/documentation/use/
    hikariConfig.jdbcUrl = jdbcUrl
    hikariConfig.driverClassName = "org.postgresql.Driver"
//    hikariConfig.username = "dbusername"
//    hikariConfig.password = "dbpassword"
    return HikariDataSource(hikariConfig)
}

fun createDatabase(dataSource: DataSource): ServerDatabase {
    val driver = dataSource.asJdbcDriver()
    val version = driver.getVersion()
    val schemaVersion = ServerDatabase.Schema.version
    if (version == 0L) {
        ServerDatabase.Schema.create(driver).value
        driver.setVersion(schemaVersion)
    } else if (version < schemaVersion) {
        ServerDatabase.Schema.migrate(driver, version, schemaVersion).value
        driver.setVersion(schemaVersion)
    }

    return ServerDatabase(
        driver,
        messageAdapter = Message.Adapter(
            modifiedAdapter = Adapters.LongToInstant,
            insertedAdapter = Adapters.LongToInstant,
            typeAdapter = EnumColumnAdapter(),
            entityTypeAdapter = EnumColumnAdapter(),
        ),
        taskAdapter = Task.Adapter(
            uuidAdapter = Adapters.UuidToTaskId,
            highlightAdapter = Adapters.StringToHighlight,
            listAdapter = Adapters.UuidToListId,
        ),
        taskListAdapter = TaskList.Adapter(
            uuidAdapter = Adapters.UuidToListId,
        ),
    )
}

private fun JdbcDriver.getVersion(): Long {
    val mapper = { cursor: SqlCursor ->
        app.cash.sqldelight.db.QueryResult.Value(if (cursor.next().value) cursor.getLong(0) else null)
    }
    return executeQuery(
        null, """
        SELECT current_setting('my.version', true);
        """.trimIndent(), mapper, 0, null
    ).value ?: 0L
}

private fun JdbcDriver.setVersion(version: Long) {
    execute(
        null, """
        SET my.version TO $version;
        ALTER DATABASE tasks set my.version from current;
        """.trimIndent(), 0, null
    ).value
}
