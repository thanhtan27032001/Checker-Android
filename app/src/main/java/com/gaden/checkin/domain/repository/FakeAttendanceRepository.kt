package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import kotlinx.coroutines.delay
import java.time.YearMonth
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

    override suspend fun getMonthRecords(
        year: Int,
        month: Int
    ): Map<Int, AttendanceRecord> {
        delay(500)
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val result = mutableMapOf<Int, AttendanceRecord>()

        for (day in 1..daysInMonth) {
            val date = java.time.LocalDate.of(year, month, day)
            val dayOfWeek = date.dayOfWeek
            val isWeekend = dayOfWeek == java.time.DayOfWeek.SATURDAY ||
                    dayOfWeek == java.time.DayOfWeek.SUNDAY
            if (isWeekend) continue

            // fake 1 late day every 5 days
            val status = if (day % 5 == 0) AttendanceStatus.CHECKED_OUT else AttendanceStatus.CHECKED_OUT
            val isLate = day % 5 == 0

            result[day] = AttendanceRecord(
                id = "fake-history-$day",
                checkinTime = Clock.System.now(),
                checkoutTime = Clock.System.now(),
                method = CheckInMethod.BUTTON,
                status = status,
                isLate = isLate,
            )
        }
        return result
    }
}