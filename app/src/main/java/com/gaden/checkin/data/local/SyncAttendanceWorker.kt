package com.gaden.checkin.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gaden.checkin.domain.model.CheckInStrategy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncAttendanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingDao: PendingAttendanceDao,
    private val checkInStrategy: CheckInStrategy,
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val pendingItems = pendingDao.getAllPending()
        if (pendingItems.isEmpty()) return Result.success()

        var hasFailure = false

        for (item in pendingItems) {
            val syncResult = if (item.actionType == PendingActionType.CHECK_IN) {
                checkInStrategy.performCheckin()
            } else {
                checkInStrategy.performCheckout()
            }

            syncResult.fold(
                onSuccess = { pendingDao.delete(item) },
                onFailure = {
                    hasFailure = true
                    pendingDao.update(item.copy(retryCount = item.retryCount + 1))
                }
            )
        }

        return if (hasFailure) Result.retry() else Result.success()
    }
}