package me.dvyy.tasks.model.database

import me.dvyy.syncengine.db.tables.SubtaskRelation
import me.dvyy.syncengine.schema.Mutators
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.mutators.Mutator
import me.dvyy.tasks.model.schema.JsonDAO
import me.dvyy.tasks.model.schema.JsonMutators
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.model.schema.RelationTableDAO

class AppDatabase(
    val mutators: Mutators<Mutator>
) {
    val tasks = JsonDAO(Task.serializer(), NotesTable)
    val rank: RelationTableDAO = RelationTableDAO(SubtaskRelation)

    val mutateTasks = mutate(this@AppDatabase.tasks)

    private fun <T> mutate(dao: JsonDAO<T>) = JsonMutators(dao, mutators)
}
