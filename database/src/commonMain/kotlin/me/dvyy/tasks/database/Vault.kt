package me.dvyy.tasks.database

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.helpers.KeyHelpers.frontMatter
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.asList
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.project
import me.dvyy.tasks.database.model.Note
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.database.model.toNote
import me.dvyy.tasks.model.database.RankFunctions
import org.dizitart.kno2.filters.*
import org.dizitart.no2.collection.DocumentCursor
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder
import org.dizitart.no2.filters.Filter
import kotlin.reflect.KProperty
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
        create: Note.() -> Unit = {},
    ): VaultPath {
        val note = Note.new(path).apply(create)
        fileSystem.createOrSaveNote(path, note.stringify())
        indexer.index(path)
        return path
    }

    fun fileTree(): Flow<List<VaultPath>> {
        return vault.findAsFlow(options = FindOptions.orderBy("path", SortOrder.Ascending))
            .project("path")
            .asList { VaultPath(it["path"] as String) }
    }

    fun observeDocument(path: VaultPath): Flow<Note?> {
        return vault.findAsFlow("path" eq path.pathString).map { it.singleOrNull()?.toNote() }
    }

    fun getNote(path: VaultPath): Note? = vault.getDocument(path)

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
                //TODO concurrent modification occurring
                queuedSaves.forEach { savePath ->
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

    fun taskFolderFor(list: VaultPath): VaultPath = VaultPath(".tasks").resolve(list.pathWithoutExt)
    fun query(filter: Filter = Filter.ALL, options: FindOptions? = null): DocumentCursor = vault.find(filter, options)
    fun queryAsFlow(filter: Filter = Filter.ALL, options: FindOptions? = null): Flow<DocumentCursor> =
        vault.findAsFlow(filter, options)

    fun <T> getBacklinks(key: KProperty<T>, contains: VaultPath): Backlinks = Backlinks(contains, key, this)

    suspend fun deleteDocument(path: VaultPath) = withContext(ioDispatcher) {
        vault.removeDocument(path)
        fileSystem.deleteNote(path)
    }
}


class Backlinks(
    val path: VaultPath,
    val property: KProperty<*>,
    private val vault: Vault,
) {
    private val filter = frontMatter(property.name) elemMatch ("$" eq path.pathWithoutExt)
    private val sortOrderKey = frontMatter(NoteFrontMatter::sortOrder.name)
    private val ascending = FindOptions.orderBy(sortOrderKey, SortOrder.Ascending)
    private val descending = FindOptions.orderBy(sortOrderKey, SortOrder.Descending)

    fun asFlow(): Flow<List<Note>> = vault.queryAsFlow(filter, ascending).map { it.map { it.toNote() } }

    fun rankOf(item: VaultPath): String? = vault.getNote(item)?.frontMatter?.sortOrder

    fun itemAfter(rank: String): Note? {
//        val itemSortOrder =  ?: return null
        return vault.query(filter.and(sortOrderKey.gt(rank)), ascending.limit(1))
            .firstOrNull()
            ?.toNote()
    }

    fun itemBefore(rank: String): Note? {
//        val itemSortOrder = vault.getNote(item)?.frontMatter?.sortOrder ?: return null
        return vault.query(filter.and(sortOrderKey.lt(rank)), descending.limit(1))
            .firstOrNull()
            ?.toNote()
    }

    private fun getRankOrMiddle(findOptions: FindOptions) = vault
        .query(filter, findOptions.limit(1))
        .firstOrNull()
        ?.toNote()
        ?.frontMatter
        ?.sortOrder
        ?: RankFunctions.middleChar.toString()

    fun rankBeforeFirst(): String = RankFunctions.getRankBefore(getRankOrMiddle(ascending))

    fun rankAfterLast(): String = RankFunctions.getRankAfter(getRankOrMiddle(descending))
}
