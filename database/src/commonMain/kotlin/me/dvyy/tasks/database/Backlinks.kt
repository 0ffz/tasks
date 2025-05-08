package me.dvyy.tasks.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.helpers.KeyHelpers
import me.dvyy.tasks.database.model.Note
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.database.model.toNote
import me.dvyy.tasks.model.database.RankFunctions
import org.dizitart.kno2.filters.and
import org.dizitart.kno2.filters.elemMatch
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.gt
import org.dizitart.kno2.filters.lt
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder
import kotlin.reflect.KProperty

/**
 * API for managing notes related to a note at [path].
 */
class Backlinks(
    val path: VaultPath,
    val property: KProperty<*>,
    private val vault: Vault,
) {
    private val filter = KeyHelpers.frontMatter(property.name) elemMatch ("$" eq path.pathWithoutExt)
    private val sortOrderKey = KeyHelpers.frontMatter(NoteFrontMatter::sortOrder.name)
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
