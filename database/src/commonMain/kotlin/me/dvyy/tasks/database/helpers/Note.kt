package me.dvyy.tasks.database.helpers

import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.content
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.database.helpers.DocumentHelpers.vaultPath
import org.dizitart.no2.collection.Document

class Note(
    val frontMatter: Document,
    val content: String,
    val path: VaultPath,
) {
    companion object {
        fun fromDocument(document: Document): Note {
            return Note(
                frontMatter = document.frontMatter(),
                content = document.content(),
                path = document.vaultPath(),
            )
        }
    }
}
