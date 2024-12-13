package me.dvyy.tasks.model.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

object Adapters {
    val BytesToUuid = object : ColumnAdapter<Uuid, ByteArray> {
        override fun encode(value: Uuid) = value.toByteArray()
        override fun decode(databaseValue: ByteArray) = Uuid.fromByteArray(databaseValue)
    }

    val StringToHighlight = object : ColumnAdapter<Highlight, String> {
        override fun encode(value: Highlight): String = Highlight.Serializer.serialize(value)
        override fun decode(databaseValue: String): Highlight = Highlight.Serializer.deserialize(databaseValue)
    }
    val KotlinUuid = object : ColumnAdapter<Uuid, UUID> {
        override fun decode(databaseValue: UUID): Uuid = databaseValue.toKotlinUuid()
        override fun encode(value: Uuid): UUID = value.toJavaUuid()
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

    val UuidToTaskId = object : ColumnAdapter<TaskId, UUID> {
        override fun decode(databaseValue: UUID) = TaskId(databaseValue.toKotlinUuid())
        override fun encode(value: TaskId) = value.uuid.toJavaUuid()
    }

    val UuidToListId = object : ColumnAdapter<ListId, UUID> {
        override fun decode(databaseValue: UUID) = ListId(databaseValue.toKotlinUuid())
        override fun encode(value: ListId) = value.uuid.toJavaUuid()
    }

    val LongToInstant = object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue)
        override fun encode(value: Instant) = value.toEpochMilliseconds()
    }

}
