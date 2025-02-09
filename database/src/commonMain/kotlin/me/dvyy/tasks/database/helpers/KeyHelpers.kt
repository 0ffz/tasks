package me.dvyy.tasks.database.helpers

object KeyHelpers {
    const val FRONTMATTER_KEY = "frontMatter"
    const val CONTENT_KEY = "fileContent"
    const val PATH_KEY = "path"


    fun frontMatter(key: String) = "$FRONTMATTER_KEY.$key"
}
