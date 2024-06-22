package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.CoroutineDispatcher

class TaskListRepository(
    private val localStore: TasksLocalDataSource,
    private val network: TasksNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {

}
