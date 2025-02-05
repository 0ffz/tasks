package me.dvyy.tasks.database.helpers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.common.RecordStream

object NitriteFlowHelpers {
    fun Flow<DocumentCursor>.project(document: Document): Flow<RecordStream<Document>> =
        map { it.project(document) }

    fun Flow<DocumentCursor>.project(vararg params: String): Flow<RecordStream<Document>> {
        val document = documentOf()
        params.forEach { document.put(it, null) }
        return project(document)
    }

    inline fun <T> Flow<RecordStream<Document>>.asSequence(crossinline transform: (Document) -> T): Flow<Sequence<T>> =
        map { it.asSequence().map { transform(it) } }

    inline fun <T> Flow<RecordStream<Document>>.asList(crossinline transform: (Document) -> T): Flow<List<T>> =
        map { it.map { transform(it) } }
}
