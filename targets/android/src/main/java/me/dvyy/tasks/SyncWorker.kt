package me.dvyy.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.dvyy.syncengine.client.sync.SyncClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams),
    KoinComponent {
    private val syncClient: SyncClient by inject()

    override suspend fun doWork(): Result {
        return try {
            syncClient.sync()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
