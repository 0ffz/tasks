package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.database.model.property

object TaskModel {
    var NoteFrontMatter.done by property<Boolean>("done", default = { false })
    var NoteFrontMatter.tags by property<List<String>>("tags", default = { listOf() })
}
