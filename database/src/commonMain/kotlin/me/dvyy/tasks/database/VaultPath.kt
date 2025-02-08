package me.dvyy.tasks.database

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.path.Path

@Serializable(with = VaultPath.Serializer::class)
class VaultPath(desiredPath: String) {
    val pathString: String = desiredPath.removePrefix("/")
    val pathWithoutExt = pathString.removeSuffix(".md")
    val displayName = pathWithoutExt.substringAfterLast("/")

    init {
        val asPath = Path(pathString)
        require(!asPath.isAbsolute) { "Path must be relative" }
        // TODO make can't go up from Path(""), .. internally is okay
        require(!pathString.contains("..")) { "Path must not contain .." }
    }
    companion object{
        val root = VaultPath("/")
    }

    object Serializer: KSerializer<VaultPath> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VaultPath", PrimitiveKind.STRING)

        override fun serialize(
            encoder: Encoder,
            value: VaultPath,
        ) {
            encoder.encodeString(value.pathString)
        }

        override fun deserialize(decoder: Decoder): VaultPath {
            return VaultPath(decoder.decodeString())
        }
    }
}
