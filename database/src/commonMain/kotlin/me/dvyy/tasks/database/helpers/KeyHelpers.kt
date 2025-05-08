package me.dvyy.tasks.database.helpers

import me.dvyy.tasks.database.model.Note

object KeyHelpers {
    val FRONTMATTER_KEY = "frontMatter"
    val PATH_KEY = Note::path.name


    fun frontMatter(key: String) = "$FRONTMATTER_KEY.$key"
}
