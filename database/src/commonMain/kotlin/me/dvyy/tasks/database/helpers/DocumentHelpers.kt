package me.dvyy.tasks.database.helpers

import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document

fun documentOfNulls(vararg keys: String): Document = documentOf(*keys.map { it to null }.toTypedArray())
