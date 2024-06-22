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
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message.Type
import me.dvyy.tasks.model.TaskId

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
            uuidAdapter = taskIdAdapter,
            highlightAdapter = EnumColumnAdapter<Highlight>(),
            listAdapter = listIdAdapter,
        ),
        taskListAdapter = TaskList.Adapter(
            uuidAdapter = listIdAdapter,
        ),
    )
}

val uuidAdapter = object : ColumnAdapter<Uuid, ByteArray> {
    override fun encode(value: Uuid) = value.bytes

    override fun decode(databaseValue: ByteArray) = uuidOf(databaseValue)
}

class WrappedAdapter<Inner : Any, Outer : Any, S>(
    val wrapped: ColumnAdapter<Inner, S>,
    val wrap: (Inner) -> Outer,
    val unwrap: (Outer) -> Inner,
) : ColumnAdapter<Outer, S> {
    override fun decode(databaseValue: S) = wrap(wrapped.decode(databaseValue))

    override fun encode(value: Outer) = wrapped.encode(unwrap(value))
}

val taskIdAdapter = WrappedAdapter(
    uuidAdapter,
    { TaskId(it) },
    { it.uuid }
)

val listIdAdapter = WrappedAdapter(
    uuidAdapter,
    { ListId(it) },
    { it.uuid }
)


val instantAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant) = value.toEpochMilliseconds()
}
