package me.dvyy.tasks.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.asList
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.project
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.collection.Document

class Vault(
    private val vault: VaultDataSource,
    private val fileSystem: VaultFileSystemDataSource,
    private val indexer: VaultIndexer,
    private val ioDispatcher: CoroutineDispatcher,
) {
    fun createDocument(path: VaultPath) {
        fileSystem.createDocument(path)
        indexer.index(path)
    }

    fun fileTree(): Flow<List<VaultPath>> {
        return vault.findAsFlow()
            .project("path")
            .asList { VaultPath(it["path"] as String) }
    }

    fun observeDocument(path: VaultPath): Flow<Document?> {
        return vault.findAsFlow("path" eq path.pathString).map { it.singleOrNull() }
    }

    fun update(path: VaultPath, modify: (Document) -> Document) {
        val document = vault.getDocument(path) ?: return
        vault.upsertDocument(path, modify(document))
    }

    fun queueSave(path: VaultPath) {
        val document = vault.getDocument(path) ?: return
        fileSystem.saveDocument(path, document)
    }

    fun index() {
        indexer.indexRoot()
    }

    suspend fun deleteDocument(path: VaultPath) = withContext(ioDispatcher) {
        vault.removeDocument(path)
        fileSystem.deleteDocument(path)
    }
}
