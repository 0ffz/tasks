package me.dvyy.tasks.model.database

import me.dvyy.syncengine.db.tables.SubtaskRelation
import me.dvyy.syncengine.schema.MutatorQueue
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.mutators.Mutator
import me.dvyy.tasks.model.schema.NotesDAO
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.model.schema.RelationTableDAO

class AppDatabase {
    val tasks = NotesDAO(Task.serializer(), NotesTable.merged)
    val rank: RelationTableDAO<Task> = RelationTableDAO(SubtaskRelation)

    val mutators = MutatorQueue(this, Mutator.serializer())
}