package me.dvyy.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.dvyy.syncengine.client.sync.SyncClient
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams), DIAware {
    override val di: DI by closestDI(appContext)
    private val syncClient: SyncClient by instance()

    override suspend fun doWork(): Result {
        return try {
            syncClient.sync()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
