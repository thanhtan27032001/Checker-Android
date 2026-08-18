package com.gaden.checkin.domain.model

import kotlin.time.Instant

enum class CheckInMethod {
    BUTTON,
    GPS,
    QR,
    FACE
}

enum class AttendanceStatus {
    NOT_CHECKED_IN,
    CHECKED_IN,
    CHECKED_OUT
}

data class AttendanceRecord(
    val id: String,
    val checkinTime: Instant?,
    val checkoutTime: Instant?,
    val method: CheckInMethod,
    val status: AttendanceStatus,
    val isLate: Boolean = false,
)

interface CheckInStrategy {
    suspend fun performCheckin(): Result<AttendanceRecord>
    suspend fun performCheckout(): Result<AttendanceRecord>
}

interface AttendanceRepository {
    suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord>
    suspend fun checkout(method: CheckInMethod): Result<AttendanceRecord>
    suspend fun getTodayStatus(): AttendanceRecord?
    suspend fun getMonthRecords(year: Int, month: Int): Map<Int, AttendanceRecord>
}
