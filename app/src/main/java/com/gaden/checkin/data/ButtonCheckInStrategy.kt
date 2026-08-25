package com.gaden.checkin.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gaden.checkin.data.local.PendingActionType
import com.gaden.checkin.data.local.PendingAttendanceDao
import com.gaden.checkin.data.local.PendingAttendanceEntity
import com.gaden.checkin.data.local.SyncAttendanceWorker
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceRepository
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.domain.model.CheckInStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SYNC_WORK_NAME = "sync_attendance_work"

class ButtonCheckInStrategy @Inject constructor(
    private val repository: AttendanceRepository,
    private val pendingDao: PendingAttendanceDao,
    @ApplicationContext private val context: Context,
): CheckInStrategy {
    override suspend fun performCheckin(): Result<AttendanceRecord> {
        return executeWithOfflineFallback(
            PendingActionType.CHECK_IN
        ) {
            repository.checkIn(CheckInMethod.BUTTON)
        }
    }

    override suspend fun performCheckout(): Result<AttendanceRecord> {
        return executeWithOfflineFallback(
            PendingActionType.CHECK_OUT
        ) {
            repository.checkOut(CheckInMethod.BUTTON)
        }
    }

    private suspend fun executeWithOfflineFallback(
        actionType: PendingActionType,
        networkCall: suspend () -> Result<AttendanceRecord>,
    ): Result<AttendanceRecord> {
        val result = networkCall()

        if (result.isFailure) {
            pendingDao.insert(
                PendingAttendanceEntity(
                    actionType = actionType,
                    method = CheckInMethod.BUTTON,
                    capturedAtMillis = System.currentTimeMillis(),
                )
            )
            scheduleSyncWork()
        }

        return result;
    }

    private fun scheduleSyncWork() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncAttendanceWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}