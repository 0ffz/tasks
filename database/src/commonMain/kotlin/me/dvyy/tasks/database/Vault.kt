package me.dvyy.tasks.database

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.asList
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.project
import me.dvyy.tasks.database.model.Note
import me.dvyy.tasks.database.model.toNote
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder
import org.dizitart.no2.filters.Filter
import kotlin.reflect.KProperty
import kotlin.time.Duration

class Vault(
    private val vault: VaultDataSource,
    private val fileSystem: VaultFileSystemDataSource,
    private val indexer: VaultIndexer,
    private val ioDispatcher: CoroutineDispatcher,
    private val queueSaveDelay: Duration,
) {
    private val saveThread = CoroutineScope(ioDispatcher.limitedParallelism(1, "VaultSaveThread"))
    private val queuedSaves: MutableSet<VaultPath> = mutableSetOf()

    // READ

    fun getNote(path: VaultPath): Note? = vault.getDocument(path)

    fun fileTree(): Flow<List<VaultPath>> {
        return vault.findAsFlow(options = FindOptions.orderBy("path", SortOrder.Ascending))
            .project("path")
            .asList { VaultPath(it["path"] as String) }
    }

    fun observeDocument(path: VaultPath): Flow<Note?> {
        return vault.findAsFlow("path" eq path.pathString).map { it.singleOrNull()?.toNote() }
    }

    fun taskFolderFor(list: VaultPath): VaultPath = VaultPath(".tasks").resolve(list.pathWithoutExt)
    fun query(filter: Filter = Filter.ALL, options: FindOptions? = null): DocumentCursor = vault.find(filter, options)
    fun queryAsFlow(filter: Filter = Filter.ALL, options: FindOptions? = null): Flow<DocumentCursor> =
        vault.findAsFlow(filter, options)

    fun getBacklinks(key: KProperty<*>, contains: VaultPath): Backlinks = Backlinks(contains, key, this)

    // UPDATE

    fun createDocument(
        path: VaultPath,
        create: Note.() -> Unit = {},
    ): VaultPath {
        val note = Note.new(path).apply(create)
        fileSystem.createOrSaveNote(path, note.stringify())
        indexer.index(path)
        return path
    }

    fun update(
        path: VaultPath,
        clearOldFrontMatter: Boolean = false,
        update: Note.() -> Unit,
    ) {
        vault.upsert(path, clearOldFrontMatter, update)
        queueSave(path)
    }

    fun moveDocument(from: VaultPath, to: VaultPath) {
        //TODO mechanism to prevent indexing while this is happening (or we might get duplicate path keys)
        fileSystem.moveNote(from, to)
        vault.upsert(from) { path = to }
    }

    fun queueSave(path: VaultPath) {
        saveThread.launch {
            queuedSaves.add(path)
            if (queuedSaves.size == 1) {
                delay(queueSaveDelay)
                queuedSaves.toList().forEach { savePath ->
                    val document = vault.getDocument(savePath) ?: return@launch
                    runCatching {
                        val hash = fileSystem.saveNote(savePath, document)
                        vault.upsert(savePath) { md5hash = hash }
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

    // DELETE

    suspend fun deleteDocument(path: VaultPath) = withContext(ioDispatcher) {
        vault.removeDocument(path)
        fileSystem.deleteNote(path)
    }
}
