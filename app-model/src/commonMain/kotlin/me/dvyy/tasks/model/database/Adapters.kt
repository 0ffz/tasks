package me.dvyy.tasks.model.database

import app.cash.sqldelight.ColumnAdapter
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuidOf
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId

object Adapters {
    val BytesToUuid = object : ColumnAdapter<Uuid, ByteArray> {
        override fun encode(value: Uuid) = value.bytes
        override fun decode(databaseValue: ByteArray) = uuidOf(databaseValue)
    }

    val StringToHighlight = object : ColumnAdapter<Highlight, String> {
        override fun encode(value: Highlight): String = Highlight.Serializer.serialize(value)
        override fun decode(databaseValue: String): Highlight = Highlight.Serializer.deserialize(databaseValue)
    }

    class WrappedAdapter<Inner : Any, Outer : Any, S>(
        val wrapped: ColumnAdapter<Inner, S>,
        val wrap: (Inner) -> Outer,
        val unwrap: (Outer) -> Inner,
    ) : ColumnAdapter<Outer, S> {
        override fun decode(databaseValue: S) = wrap(wrapped.decode(databaseValue))

        override fun encode(value: Outer) = wrapped.encode(unwrap(value))
    }

    val BytesToTaskId = WrappedAdapter(
        BytesToUuid,
        { TaskId(it) },
        { it.uuid }
    )

    val BytesToListId = WrappedAdapter(
        BytesToUuid,
        { ListId(it) },
        { it.uuid }
    )

    val UuidToTaskId = object : ColumnAdapter<TaskId, Uuid> {
        override fun decode(databaseValue: Uuid) = TaskId(databaseValue)
        override fun encode(value: TaskId) = value.uuid
    }

    val UuidToListId = object : ColumnAdapter<ListId, Uuid> {
        override fun decode(databaseValue: Uuid) = ListId(databaseValue)
        override fun encode(value: ListId) = value.uuid
    }

    val LongToInstant = object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue)
        override fun encode(value: Instant) = value.toEpochMilliseconds()
    }

}
