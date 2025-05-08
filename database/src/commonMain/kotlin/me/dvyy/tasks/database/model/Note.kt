package me.dvyy.tasks.database.model

import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers.encodeToString
import me.dvyy.tasks.database.helpers.KeyHelpers
import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document
import kotlin.reflect.KProperty

class Note internal constructor(
    override val document: Document,
) : DocumentWrapper {
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

    fun stringify() = "---\n${frontMatter.document.encodeToString()}\n---\n${fileContent ?: ""}"

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
}

fun Document.toNote(): Note = Note.fromDocument(this)

inline operator fun <reified T> Document.getValue(thisRef: Any?, property: KProperty<*>): T =
    get(property.name) as T

inline operator fun <reified T> Document.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    put(property.name, value)
}
