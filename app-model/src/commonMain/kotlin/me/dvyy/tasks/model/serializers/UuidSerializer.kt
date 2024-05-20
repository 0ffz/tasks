package me.dvyy.tasks.model.serializers

import com.benasher44.uuid.Uuid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
