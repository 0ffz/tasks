package me.dvyy.tasks.utils

import kotlinx.coroutines.Dispatchers

object AppDispatchers {
    val db = Dispatchers.Default.limitedParallelism(1)
}
