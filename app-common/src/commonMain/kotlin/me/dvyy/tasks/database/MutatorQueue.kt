package me.dvyy.tasks.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.dvyy.syncengine.db.Database
import me.dvyy.tasks.model.mutators.Mutator
import java.util.concurrent.ConcurrentLinkedQueue

interface Mutators {
    suspend operator fun invoke(mutator: Mutator)
}

class MutatorQueue: Mutators {
//    val inMemoryQueue = ConcurrentLinkedQueue<Mutator>()
    val mutatorScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))
    var applying = false

//    private suspend fun scheduleApply() {
//        if (applying) return
//        applying = true
//        Database.write {
//            applyQueued()
//        }
//        applying = false
//    }
//    mutatorScope.launch {
//        inMemoryQueue.add(mutator) //TODO this could lead to a mutator not being applied since we are currently applying
//        scheduleApply()
//    }

    override suspend fun invoke(mutator: Mutator) = Database.write {
        mutator.mutate() //TODO merge with last if possible
        MutatorsTable.append(mutator)
    }

//    fun applyQueued() {
//        repeat(inMemoryQueue.size) {
//            val mutator = inMemoryQueue.poll()
//            mutator.mutate()
//            MutatorsTable.insert { it[data] = mutator }
//        }
//    }

//    suspend fun reconcileStored() = Database.write {
//        MutatorsTable.forEachMutator { it.mutate() }
//        applyQueued()
//    }
//
//    fun clearMutators(count: Int) {
//        val sqlCount = MutatorsTable.deleteWhere {
//            id inSubQuery select(id).limit(count)
//        }
//        val remaining = sqlCount - count
////        repeat(remaining) { inMemoryQueue.poll() }
//    }
//
//    fun getMutatorsToSend(): List<Mutator> {
//        applyQueued()
//        return MutatorsTable.selectAll().map { it[MutatorsTable.data] }
//    }
}