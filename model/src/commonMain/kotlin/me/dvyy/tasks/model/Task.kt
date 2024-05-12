package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Task(
    val uuid: @Contextual Uuid,
    val name: String = "",
    val completed: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
)


fun LongArray.toUuid() = Uuid(this[0], this[1])

object UuidSerializer : KSerializer<Uuid> {
    val surrogate = LongArraySerializer()
    override val descriptor: SerialDescriptor = surrogate.descriptor

    override fun deserialize(decoder: Decoder): Uuid {
        val array = decoder.decodeSerializableValue(surrogate)
        require(array.size == 2) { "UUID expected a long array of size 2, but found ${array.size}" }
        return array.toUuid()
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeSerializableValue(surrogate, longArrayOf(value.mostSignificantBits, value.leastSignificantBits))
    }
}

object UuidAsStringSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): Uuid {
        return uuidFrom(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }
}
