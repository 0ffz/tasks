package me.dvyy.tasks.app.data

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.Database
import me.dvyy.tasks.db.Message

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
        )
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

fun dothings(db: Database) {
    db.messagesQueries.insert(uuid4(), Clock.System.now())
    db.messagesQueries.insert(uuid4(), Clock.System.now())
    db.messagesQueries.insert(uuid4(), Clock.System.now())
    db.messagesQueries.clear()
    println(db.messagesQueries.selectAll().executeAsList())
}
