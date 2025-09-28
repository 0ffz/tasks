package me.dvyy.tasks.model.database.mutators

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.schema.AbstractMutator
import me.dvyy.tasks.model.database.AppDAO

@Serializable
sealed interface Mutator : AbstractMutator<AppDAO>
