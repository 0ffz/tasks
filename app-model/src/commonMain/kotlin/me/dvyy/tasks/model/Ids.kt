package me.dvyy.tasks.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface EntityId {
    val uuid: Uuid
}

@Serializable
@JvmInline
value class TaskId(override val uuid: @Contextual Uuid) : EntityId {
    companion object {
        fun new(): TaskId = TaskId(Uuid.random())
    }
}

@Serializable
@JvmInline
value class ListId(override val uuid: @Contextual Uuid) : EntityId {

    val isDate: Boolean get() = uuid.toLongs { top, _ -> top == TOP_BITS }
    val date: LocalDate?
        get() = if (isDate) LocalDate.fromEpochDays(uuid.toLongs { _, bottom -> bottom.toInt() })
        else null

    companion object {
        fun newProject(): ListId {
            val uuid = Uuid.random()
            // Avoid clashes with TOP_BITS, that one combination is reserved for date lists
            return uuid.toLongs { top, bottom ->
                val topFixed = if (top == TOP_BITS) TOP_BITS + 1 else top
                ListId(Uuid.fromLongs(topFixed, bottom))
            }
        }

        fun forDate(date: LocalDate): ListId =
            ListId(Uuid.fromLongs(TOP_BITS, date.toEpochDays().toLong() or UUIDv4_VAR))
    }
}

// Mark as correct UUIDv4 variant
private val UUIDv4_VAR = (0b10uL shl 62).toLong()

// Randomly selected top bits to avoid really obvious clashes with all zeroes for instance
private const val TOP_BITS = 0x5cf7d47c6112423dL

fun Uuid.asTask() = TaskId(this)
fun Uuid.asList() = ListId(this)

