package com.gaden.checkin.data.repository

import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

/**
 * Implementation giả — trả về data cứng sau 1 khoảng delay giả lập network,
 * để bạn thấy UI (loading state, success state) hoạt động thật sự
 * trước khi có backend Spring Boot. Xóa/thay bằng AttendanceRepositoryImpl
 * (dùng Retrofit) khi nối API thật ở Phase 1.
 */
@Singleton
class FakeAttendanceRepository @Inject constructor() : AttendanceRepository {

    private var lastRecord: AttendanceRecord? = null

    override suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord> {
        delay(800) // giả lập độ trễ network, để thấy loading spinner hoạt động
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