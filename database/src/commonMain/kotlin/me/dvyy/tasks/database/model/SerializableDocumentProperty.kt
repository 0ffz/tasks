package me.dvyy.tasks.database.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SerializableDocumentProperty<T>(
    val name: String,
    val serializer: KSerializer<T>,
    val default: () -> T,
) : ReadWriteProperty<DocumentWrapper, T> {
    override fun getValue(
        thisRef: DocumentWrapper,
        property: KProperty<*>,
    ): T {
        val encoded = thisRef.document.get(name) as String? ?: return default()
        return DocumentYamlHelpers.styledYaml.decodeFromString(serializer, encoded)
    }

    override fun setValue(thisRef: DocumentWrapper, property: KProperty<*>, value: T) {
        val encoded = DocumentYamlHelpers.styledYaml.encodeToString(serializer, value)
        thisRef.document.put(name, encoded)
    }
}

inline fun <reified T> serializableProperty(
    key: String,
    serializer: KSerializer<T> = serializer<T>(),
    noinline default: () -> T = { error("$key was not set on document.") },
) = SerializableDocumentProperty<T>(
    key,
    serializer,
    default
)
