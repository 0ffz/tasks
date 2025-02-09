package me.dvyy.tasks.database.helpers

import me.dvyy.tasks.database.VaultPath
import org.dizitart.no2.collection.Document

object DocumentHelpers {
    fun Document.content() = get(KeyHelpers.CONTENT_KEY, String::class.java)
    fun Document.frontMatter() = get(KeyHelpers.FRONTMATTER_KEY) as? Document
    fun Document.vaultPath(): VaultPath = VaultPath(get(KeyHelpers.PATH_KEY, String::class.java))

    inline fun <reified T> Document.frontMatter(key: String) =
        get(KeyHelpers.frontMatter(key)) as? T
}
