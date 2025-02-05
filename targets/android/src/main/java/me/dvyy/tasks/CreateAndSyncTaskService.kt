package me.dvyy.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import me.dvyy.tasks.sync.data.SyncRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams),
    KoinComponent {
    val syncRepo by inject<SyncRepository>()
    override suspend fun doWork(): Result {
        syncRepo.sync()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

val uploadWorkRequest: WorkRequest =
    OneTimeWorkRequestBuilder<UploadWorker>().build()
