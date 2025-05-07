package me.dvyy.tasks.database.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers.encodeToString
import me.dvyy.tasks.database.helpers.KeyHelpers
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.collection.Document
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

class Note internal constructor(
    override val document: Document,
) : DocumentWrapper {
    //    private val _frontMatterWrapper =
    var frontMatter: NoteFrontMatter
        get() = NoteFrontMatter(
            document.get(KeyHelpers.FRONTMATTER_KEY) as Document?
                ?: documentOf().also { document.put(KeyHelpers.FRONTMATTER_KEY, it) }
        )
        set(value) {
            document.put(KeyHelpers.FRONTMATTER_KEY, value.document)
        }
    var fileContent: String? by document
    var path: VaultPath by serializableProperty("path")
    var md5hash: String? by document

    inline fun frontMatter(apply: NoteFrontMatter.() -> Unit) {
        frontMatter.apply(apply)
    }

    companion object {
        fun fromDocument(document: Document): Note {
            return Note(document)
        }

        @JvmName("fromDocumentNullable")
        fun fromDocument(document: Document?): Note? {
            return document?.let { fromDocument(it) }
        }

        fun new(
            path: VaultPath,
        ): Note = documentOf(
            Note::path.name to path
        ).toNote()

        fun new(
            path: VaultPath,
            builder: Note.() -> Unit,
        ): Note {
            return new(path).apply { builder() }
        }
    }

    fun stringify() = "---\n${frontMatter.document.encodeToString()}\n---\n${fileContent ?: ""}"
}

fun Document.toNote(): Note = Note.fromDocument(this)

class NoteFrontMatter internal constructor(
    override val document: Document,
) : DocumentWrapper {
    var managed: Boolean? by document
    var projects: List<String> by document
    var sortOrder: String? by document

    fun entries(): List<Pair<String, Any>> = document.map { it.first to it.second }

    operator fun set(key: String, value: Any?) {
        document.put(key, value)
    }

    companion object {
        fun new(create: NoteFrontMatter.() -> Unit): NoteFrontMatter {
            return NoteFrontMatter(documentOf()).apply(create)
        }
    }
}

inline operator fun <reified T> Document.getValue(thisRef: Any?, property: KProperty<*>): T =
    get(property.name) as T

inline operator fun <reified T> Document.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    put(property.name, value)
}

class DocumentProperty<T>(
    val name: String,
    val kClass: KClass<T & Any>,
    val default: () -> T,
) : ReadWriteProperty<DocumentWrapper, T> {
    override fun getValue(
        thisRef: DocumentWrapper,
        property: KProperty<*>,
    ): T {
        return thisRef.document.get(name) as T? ?: default()
    }

    override fun setValue(thisRef: DocumentWrapper, property: KProperty<*>, value: T) {
        thisRef.document.put(name, value)
    }
}

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

inline fun <reified T> property(key: String, noinline default: () -> T) = DocumentProperty<T>(
    key,
    typeOf<T>().classifier as KClass<T & Any>,
    default
)


@PublishedApi //TODO move to koin module
internal val serializationMapper = KotlinXSerializationMapper()

inline fun <reified T> serializableProperty(
    key: String,
    serializer: KSerializer<T> = serializer<T>(),
    noinline default: () -> T = { error("$key was not set on document.") },
) = SerializableDocumentProperty<T>(
    key,
    serializer,
    default
)
