package me.dvyy.tasks.app.data

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuidOf
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.Message
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.Message.Type

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(
        driver = driver,
        messageAdapter = Message.Adapter(
            uuidAdapter = uuidAdapter,
            modifiedAdapter = instantAdapter,
            typeAdapter = EnumColumnAdapter<Type>(),
        ),
        taskAdapter = Task.Adapter(
            uuidAdapter = uuidAdapter,
            highlightAdapter = EnumColumnAdapter<Highlight>(),
            listAdapter = uuidAdapter,
        ),
        taskListAdapter = TaskList.Adapter(
            uuidAdapter = uuidAdapter,
        ),
    )
}

val uuidAdapter = object : ColumnAdapter<Uuid, ByteArray> {
    override fun encode(value: Uuid) = value.bytes

    override fun decode(databaseValue: ByteArray) = uuidOf(databaseValue)
}

val instantAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant) = value.toEpochMilliseconds()
}
