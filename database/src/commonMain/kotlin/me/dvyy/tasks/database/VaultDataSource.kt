package me.dvyy.tasks.database

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.helpers.KeyHelpers
import me.dvyy.tasks.database.helpers.documentOfNulls
import me.dvyy.tasks.database.model.HashInfo
import me.dvyy.tasks.database.model.HashInfo.Companion.toHashInfo
import me.dvyy.tasks.database.model.Note
import me.dvyy.tasks.database.model.toNote
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getCollection
import org.dizitart.no2.Nitrite
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.collection.UpdateOptions
import org.dizitart.no2.collection.events.CollectionEventListener
import org.dizitart.no2.collection.events.EventType
import org.dizitart.no2.filters.Filter
import org.dizitart.no2.index.IndexOptions
import org.dizitart.no2.index.IndexType

class VaultDataSource(
    val db: Nitrite,
) {
    val filesCollection = db.getCollection("files") {
        createIndex("path")
        createIndex(IndexOptions.indexOptions(IndexType.NON_UNIQUE), Note::md5hash.name) //TODO also track filesize
        createIndex(IndexOptions.indexOptions(IndexType.FULL_TEXT), Note::fileContent.name)
    }

    fun find(filter: Filter = Filter.ALL, options: FindOptions? = null): DocumentCursor =
        if (options != null)
            filesCollection.find(filter, options)
        else
            filesCollection.find(filter)

    fun findAsFlow(filter: Filter = Filter.ALL, options: FindOptions? = null): Flow<DocumentCursor> = flow {
        val updates = Channel<Unit>(CONFLATED)
        updates.trySend(Unit)
        val listener = CollectionEventListener { event ->
            if (event.eventType == EventType.Insert || event.eventType == EventType.Update || event.eventType == EventType.Remove) {
                updates.trySend(Unit)
            }
        }
        filesCollection.subscribe(listener)
        try {
            for (update in updates) {
                if (options != null) {
                    emit(filesCollection.find(filter, options))
                } else {
                    emit(filesCollection.find(filter))
                }
            }
        } finally {
            filesCollection.unsubscribe(listener)
        }
    }

    fun removeDocument(path: VaultPath) {
        filesCollection.remove("path" eq path.pathString)
    }

    fun upsert(path: VaultPath, note: Note, clearOldFrontMatter: Boolean = true) {
        // Clear existing frontMatter, since update doesn't delete old values
        if (clearOldFrontMatter && note.document.containsKey(KeyHelpers.FRONTMATTER_KEY))
            filesCollection.update("path" eq path.pathString, documentOf(KeyHelpers.FRONTMATTER_KEY to null))

        filesCollection.update("path" eq path.pathString, note.document.apply {
            put(KeyHelpers.PATH_KEY, path.pathString)
        }, UpdateOptions.updateOptions(true))
    }

    inline fun upsert(path: VaultPath, clearOldFrontMatter: Boolean = true, edit: Note.() -> Unit) {
        upsert(path, documentOf().toNote().apply(edit), clearOldFrontMatter)
    }

    fun removeAll(filter: Filter) {
        filesCollection.remove(filter)
    }

    fun getDocument(path: VaultPath): Note? {
        return Note.fromDocument(filesCollection.find("path" eq path.pathString).singleOrNull())
    }

    fun getHashInfo(path: VaultPath): HashInfo? = filesCollection
        .find("path" eq path.pathString)
        .project(documentOfNulls("path", "md5hash")).singleOrNull()
        ?.toHashInfo()

    fun getDocumentAsFlow(path: VaultPath): Flow<Note?> {
        return findAsFlow("path" eq path.pathString).map { Note.fromDocument(it.singleOrNull()) }
    }
}
