package me.dvyy.tasks.app.data

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import me.dvyy.tasks.db.*
import me.dvyy.tasks.model.database.Adapters

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(
        driver = driver,
        messageAdapter = Message.Adapter(
            uuidAdapter = Adapters.BytesToUuid,
            modifiedAdapter = Adapters.LongToInstant,
            typeAdapter = EnumColumnAdapter(),
            entityTypeAdapter = EnumColumnAdapter(),
        ),
        taskAdapter = Task.Adapter(
            uuidAdapter = Adapters.BytesToTaskId,
            highlightAdapter = Adapters.StringToHighlight,
            listAdapter = Adapters.BytesToListId,
        ),
        taskListAdapter = TaskList.Adapter(
            uuidAdapter = Adapters.BytesToListId,
        ),
        rankAdapter = Rank.Adapter(
            uuidAdapter = Adapters.BytesToUuid,
            parentAdapter = Adapters.BytesToUuid,
        ),
    )
}
