package me.dvyy.tasks.serialization

import com.benasher44.uuid.Uuid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
class Task(
    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid,
    val name: String,
    val completed: Boolean = true,
)

@Serializable
class UuidSurrogate(val msb: Long, val lsb: Long) {
    fun toUuid() = Uuid(msb, lsb)
}

object UuidSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = UuidSurrogate.serializer().descriptor
    private val surrogate = UuidSurrogate.serializer()

    override fun deserialize(decoder: Decoder): Uuid {
        return decoder.decodeSerializableValue(surrogate).toUuid()
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeSerializableValue(surrogate, UuidSurrogate(value.mostSignificantBits, value.leastSignificantBits))
    }
}
