package me.dvyy.tasks.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

//fun <T, R> StateFlow<T>.mapSync(transform: (T) -> R): StateFlow<R> = object : StateFlow<R> {
//
//    override val replayCache: List<R> get() = listOf(value)
//
//    override suspend fun collect(collector: FlowCollector<R>): Nothing {
//        var lastEmittedValue: Any? = nullSurrogate
//        this@mapSync.collect { newValue ->
//            val transformedValue = transform(newValue)
//            if (transformedValue != lastEmittedValue) {
//                lastEmittedValue = transformedValue
//                collector.emit(transformedValue)
//            }
//        }
//    }
//
//    private var lastUpstreamValue = this@mapSync.value
//
//    override var value: R = transform(lastUpstreamValue)
//        private set
//        get() {
//            val currentUpstreamValue: T = this@mapSync.value
//            if (currentUpstreamValue == lastUpstreamValue) return field
//            val newValue = transform(currentUpstreamValue)
//            field = newValue
//            lastUpstreamValue = currentUpstreamValue
//            return newValue
//        }
//}
//

infix fun <T> Flow<T>.defaults(value: T): StateFlow<T> = object : StateFlow<T> {
    override val value: T = value
    override val replayCache: List<T> = listOf(value)

    override suspend fun collect(collector: FlowCollector<T>): Nothing =
        coroutineScope { this@defaults.stateIn(this).collect(collector) }
}

fun <A, B, R> CoroutineScope.combinedStateFlow(
    flowA: StateFlow<A>,
    flowB: StateFlow<B>,
    map: (A, B) -> R,
): StateFlow<R> {
    return combine(flowA, flowB) { a, b ->
        map(a, b)
    }.stateIn(this, SharingStarted.WhileSubscribed(1000), map(flowA.value, flowB.value))
}
