package me.dvyy.tasks.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dvyy.tasks.database.helpers.DocumentHelpers.content
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.database.helpers.DocumentHelpers.write
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers.encodeToString
import me.dvyy.tasks.database.helpers.KeyHelpers
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.asList
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.project
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.collection.Document
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder
import org.dizitart.no2.filters.Filter
import kotlin.time.Duration

class Vault(
    private val vault: VaultDataSource,
    private val vaultPaths: VaultPaths,
    private val fileSystem: VaultFileSystemDataSource,
    private val indexer: VaultIndexer,
    private val ioDispatcher: CoroutineDispatcher,
    private val queueSaveDelay: Duration,
) {
    private val saveThread = CoroutineScope(ioDispatcher.limitedParallelism(1, "VaultSaveThread"))
    private val queuedSaves: MutableSet<VaultPath> = mutableSetOf()

    fun createDocument(
        path: VaultPath,
        frontMatter: Document = documentOf(),
        content: String = "",
    ): VaultPath {
        fileSystem.createOrSaveDocument(path, "---\n${frontMatter.encodeToString()}\n---\n$content")
        indexer.index(path)
        return path
    }

    fun fileTree(): Flow<List<VaultPath>> {
        return vault.findAsFlow(options = FindOptions.orderBy("path", SortOrder.Ascending))
            .project("path")
            .asList { VaultPath(it["path"] as String) }
    }

    fun observeDocument(path: VaultPath): Flow<Document?> {
        return vault.findAsFlow("path" eq path.pathString).map { it.singleOrNull() }
    }

    fun getDocument(path: VaultPath): Document? = vault.getDocument(path)

    fun update(
        path: VaultPath,
        frontMatter: ((Document) -> Document)? = null,
        content: ((String) -> String)? = null,
    ) {
        val document = vault.getDocument(path) ?: return
        if (frontMatter != null)
            document.put(KeyHelpers.FRONTMATTER_KEY, frontMatter(document.frontMatter()))
        if (content != null) document.put(KeyHelpers.CONTENT_KEY, content.invoke(document.content()))
        vault.upsertDocument(path, document)
        queueSave(path)
    }

    fun moveDocument(from: VaultPath, to: VaultPath) {
        //TODO mechanism to prevent indexing while this is happening (or we might get duplicate path keys)
        fileSystem.moveDocument(from, to)
        vault.upsertDocument(from, documentOf(
            "path" to to.pathString,
        ))
    }

    fun queueSave(path: VaultPath) {
        saveThread.launch {
            queuedSaves.add(path)
            if (queuedSaves.size == 1) {
                delay(queueSaveDelay)
                queuedSaves.forEach { savePath ->
                    val document = vault.getDocument(savePath) ?: return@launch
                    runCatching {
                        val hash = fileSystem.saveDocument(savePath, document)
                        vault.upsertDocument(savePath, document.write(KeyHelpers.MD5_HASH_KEY, hash))
                    }.onFailure {
                        println("Failed to save document $savePath:")
                        it.printStackTrace()
                    }
                    queuedSaves.remove(savePath)
                }
            }
        }
    }

    fun index() {
        indexer.indexRoot()
    }

    fun taskFolderFor(list: VaultPath): VaultPath = VaultPath(".tasks").resolve(list.pathWithoutExt)
    fun query(filter: Filter = Filter.ALL, options: FindOptions? = null): DocumentCursor = vault.find(filter, options)
    fun queryAsFlow(filter: Filter = Filter.ALL, options: FindOptions? = null): Flow<DocumentCursor> = vault.findAsFlow(filter, options)

    suspend fun deleteDocument(path: VaultPath) = withContext(ioDispatcher) {
        vault.removeDocument(path)
        fileSystem.deleteDocument(path)
    }
}
