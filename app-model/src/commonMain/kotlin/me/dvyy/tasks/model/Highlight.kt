package me.dvyy.tasks.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Highlight.Serializer::class)
data class Highlight(
    val type: Type,
    val isLight: Boolean,
) {
    object Serializer : KSerializer<Highlight> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Highlight", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Highlight) {
            encoder.encodeString(serialize(value))
        }

        fun serialize(value: Highlight): String = "${value.type.name}${if (!value.isLight) " Dark" else ""}"

        override fun deserialize(decoder: Decoder) = deserialize(decoder.decodeString())

        fun deserialize(string: String): Highlight {
            val split = string.split(" ")
            val type = runCatching { Type.valueOf(split[0]) }.getOrElse { Type.Unmarked }
            val isLight = split.getOrNull(1) != "Dark"
            return Highlight(type, isLight)
        }
    }

    // based on ANSI's 8 colors
    enum class Type {
        Unmarked,
        Red,
        Yellow,
        Green,
        Blue,
        Magenta,
        Cyan,
        Light
    }

    companion object {
        val Unmarked = Highlight(Type.Unmarked, true)
    }
}
