package me.dvyy.tasks.database

import androidx.compose.runtime.Immutable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.path.Path

@Serializable(with = VaultPath.Serializer::class)
@Immutable
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

    fun resolve(other: String): VaultPath {
        return VaultPath("$pathString/${other.removePrefix("/")}")
    }

    fun resolve(other: VaultPath): VaultPath {
        return resolve(other.pathString)
    }

    val parent get(): VaultPath {
        return VaultPath(pathString.substringBeforeLast("/", missingDelimiterValue = ""))
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VaultPath) return false

        if (pathString != other.pathString) return false

        return true
    }

    override fun hashCode(): Int {
        return pathString.hashCode()
    }

    override fun toString(): String {
        return "VaultPath($pathString)"
    }

    companion object{
        val root = VaultPath("/")
    }
}
