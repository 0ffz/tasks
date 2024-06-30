package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
sealed interface EntityId {
    val uuid: Uuid
    val type: EntityType
}

@Serializable
@JvmInline
value class TaskId(override val uuid: @Contextual Uuid) : EntityId {
    override val type: EntityType get() = EntityType.TASK
    companion object {
        fun new(): TaskId = TaskId(uuid4())
    }
}

@Serializable
@JvmInline
value class ListId(override val uuid: @Contextual Uuid) : EntityId {
    override val type: EntityType get() = EntityType.LIST

    val isDate: Boolean get() = uuid.mostSignificantBits == TOP_BITS
    val date: LocalDate?
        get() = if (isDate)
            LocalDate.fromEpochDays(uuid.leastSignificantBits.toInt())
        else null

    companion object {
        fun newProject(): ListId {
            val uuid = uuid4()
            // Avoid clashes with TOP_BITS, that one combination is reserved for date lists
            val top = if (uuid.mostSignificantBits == TOP_BITS) TOP_BITS + 1 else uuid.mostSignificantBits
            val bottom = uuid.leastSignificantBits
            return ListId(Uuid(top, bottom))
        }

        fun forDate(date: LocalDate): ListId =
            ListId(Uuid(TOP_BITS, date.toEpochDays().toLong() or UUIDv4_VAR))
    }
}

// Mark as correct UUIDv4 variant
private val UUIDv4_VAR = (0b10uL shl 62).toLong()

// Randomly selected top bits to avoid really obvious clashes with all zeroes for instance
private const val TOP_BITS = 0x5cf7d47c6112423dL

fun Uuid.asTask() = TaskId(this)
fun Uuid.asList() = ListId(this)

