package me.dvyy.tasks.database.helpers

import me.dvyy.tasks.database.VaultPath
import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document

object DocumentHelpers {
//    fun Document.content() = get(KeyHelpers.CONTENT_KEY) as? String ?: ""
//    fun Document.frontMatter() = (get(KeyHelpers.FRONTMATTER_KEY) as? Document) ?: documentOf()
//    fun Document.vaultPath(): VaultPath = VaultPath(get(KeyHelpers.PATH_KEY, String::class.java))

    inline fun <reified T> Document.read(key: String): T? = get(key) as? T
    fun <T> Document.write(key: String, value: T): Document = put(key, value)
    inline fun <reified T> Document.frontMatter(key: String) =
        get(KeyHelpers.frontMatter(key)) as? T
}


fun documentOfNulls(vararg keys: String): Document = documentOf(*keys.map { it to null }.toTypedArray())
