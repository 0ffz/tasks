package me.dvyy.tasks.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.model.HashInfo
import me.dvyy.tasks.database.model.HashInfo.Companion.toHashInfo
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getCollection
import org.dizitart.no2.Nitrite
import org.dizitart.no2.collection.Document
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.collection.NitriteCollection
import org.dizitart.no2.filters.Filter
import org.dizitart.no2.index.IndexOptions
import org.dizitart.no2.index.IndexType

class VaultDataSource(
    val db: Nitrite,
    val dbScope: CoroutineDispatcher,
) {
    val filesCollection = db.getCollection("files") {
        createIndex("path")
        createIndex(IndexOptions.indexOptions(IndexType.NON_UNIQUE), "md5hash") //TODO also track filesize
        createIndex(IndexOptions.indexOptions(IndexType.FULL_TEXT), "fileContent")
    }

    val updatesFlow = MutableStateFlow<NitriteCollection>(filesCollection)

    init {
        filesCollection.subscribe { updatesFlow.tryEmit(filesCollection) }
    }

    fun findAsFlow(filter: Filter = Filter.ALL): Flow<DocumentCursor> {
        return updatesFlow.map { it.find(filter) }
    }

    fun removeDocument(path: VaultPath) {
        filesCollection.remove("path" eq path.pathString)
    }

    fun upsertDocument(path: VaultPath, document: Document) {
        filesCollection.update(document.apply {
            put("path", path.pathString)
        }, true)
    }

    fun removeAll(filter: Filter) {
        filesCollection.remove(filter)
    }

    fun getDocument(path: VaultPath): Document? {
        return filesCollection.find("path" eq path.pathString).singleOrNull()
    }

    fun getHashInfo(path: VaultPath): HashInfo? = filesCollection
        .find("path" eq path.pathString)
        .project(
            documentOf(
                "path" to null,
                "md5hash" to null
            )
        ).singleOrNull()
        ?.toHashInfo()

    fun getDocumentAsFlow(path: VaultPath): Flow<Document?> {
        return findAsFlow("path" eq path.pathString).map { it.singleOrNull() }
    }
}
