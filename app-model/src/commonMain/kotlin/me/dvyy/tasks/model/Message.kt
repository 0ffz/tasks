package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed interface Message<T> {
    val uuid: Uuid
    val timestamp: Instant

    data class Update<T>(
        val data: T,
        override val uuid: @Contextual Uuid,
        override val timestamp: Instant,
    ) : Message<T>

    data class Delete<T>(
        override val uuid: @Contextual Uuid,
        override val timestamp: Instant,
    ) : Message<T>

    fun merge(other: Message<T>): Message<T> {
        return if (other.timestamp > this.timestamp) other else this
    }
}
