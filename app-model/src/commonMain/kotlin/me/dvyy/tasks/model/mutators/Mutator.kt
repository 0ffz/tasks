package me.dvyy.tasks.model.mutators

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.schema.AbstractMutator
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.database.AppDatabase

@Serializable
sealed interface Mutator : AbstractMutator<AppDAO>
