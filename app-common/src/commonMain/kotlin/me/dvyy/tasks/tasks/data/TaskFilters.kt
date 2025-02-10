package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.KeyHelpers.frontMatter
import org.dizitart.kno2.filters.elemMatch
import org.dizitart.kno2.filters.eq

object TaskFilters {
    fun tasksForList(list: VaultPath) = frontMatter("projects") elemMatch ("$" eq list.pathWithoutExt)
}
