package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed interface ListKey {
    val uniqueIdentifier: String

    @Serializable
    data class Date(val date: LocalDate) : ListKey {
        override val uniqueIdentifier: String get() = "date/$date"
    }

    @Serializable
    data class Project(@Contextual val uuid: Uuid) : ListKey {
        override val uniqueIdentifier: String = "project/$uuid"

        companion object {
            fun fromIdentifier(key: String): Project {
                val (type, id) = key.split("/")
                if (type != "project") throw IllegalArgumentException("Key is not a project: $key")
                return Project(uuidFrom(id))
            }
        }
    }

    companion object {
        fun fromIdentifier(key: String): ListKey {
            val (type, id) = key.split("/")
            return when (type) {
                "date" -> Date(LocalDate.parse(id))
                "project" -> Project(uuidFrom(id))
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }
    }
}
