package me.dvyy.tasks.model.database

import me.dvyy.syncengine.schema.Mutators
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.mutators.Mutator
import me.dvyy.tasks.model.schema.JsonDAO
import me.dvyy.tasks.model.schema.JsonMutators
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.model.schema.SubtasksDAO
import me.dvyy.tasks.model.schema.TasksView

class AppDatabase(
    val query: AppDAO,
    val mutate: AppMutators,
) {
    suspend fun mutate(mutator: Mutator) {
        mutate.mutators.invoke(mutator)
    }
}

class AppDAO {
    val tasks = JsonDAO(Task.serializer(), NotesTable)
    val rank = SubtasksDAO(tasks)
}

class AppMutators(
    db: AppDAO,
    val mutators: Mutators<Mutator>
) {
    val tasks = mutate(db.tasks)

    private fun <T> mutate(dao: JsonDAO<T>) = JsonMutators(dao, mutators)
}
