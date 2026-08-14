package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class FakeAttendanceRepository @Inject constructor() : AttendanceRepository {

    private var lastRecord: AttendanceRecord? = null

    override suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord> {
        delay(800)
        val record = AttendanceRecord(
            id = "fake-${System.currentTimeMillis()}",
            checkinTime = Clock.System.now(),
            checkoutTime = null,
            method = method,
            status = AttendanceStatus.CHECKED_IN,
        )
        lastRecord = record
        return Result.success(record)
    }

    override suspend fun checkout(method: CheckInMethod): Result<AttendanceRecord> {
        delay(800)
        val current = lastRecord ?: return Result.failure(IllegalStateException("Chưa check-in"))
        val record = current.copy(
            checkoutTime = Clock.System.now(),
            status = AttendanceStatus.CHECKED_OUT,
        )
        lastRecord = record
        return Result.success(record)
    }

    override suspend fun getTodayStatus(): AttendanceRecord? = lastRecord
}