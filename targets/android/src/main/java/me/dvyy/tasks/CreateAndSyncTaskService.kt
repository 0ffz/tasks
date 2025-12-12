package me.dvyy.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams),
    KoinComponent {
    override suspend fun doWork(): Result {
        //TODO background worker
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

val uploadWorkRequest: WorkRequest =
    OneTimeWorkRequestBuilder<UploadWorker>().build()
