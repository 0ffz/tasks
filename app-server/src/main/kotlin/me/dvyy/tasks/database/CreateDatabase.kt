package me.dvyy.tasks.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.dvyy.tasks.db.Message
import me.dvyy.tasks.db.ServerDatabase
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList.Adapter
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
    ServerDatabase.Schema.create(driver)
    return ServerDatabase(
        driver,
        messageAdapter = Message.Adapter(
            modifiedAdapter = Adapters.LongToInstant,
            typeAdapter = EnumColumnAdapter(),
            entityTypeAdapter = EnumColumnAdapter(),
        ),
        taskAdapter = Task.Adapter(
            uuidAdapter = Adapters.UuidToTaskId,
            highlightAdapter = Adapters.StringToHighlight,
            listAdapter = Adapters.UuidToListId,
        ),
        taskListAdapter = Adapter(
            uuidAdapter = Adapters.UuidToListId,
        ),
    )
}
