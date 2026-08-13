package com.gaden.checkin.domain.model

import kotlin.time.Instant

enum class CheckinMethod {
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
    val method: CheckinMethod,
    val status: AttendanceStatus
) {}

interface CheckInStrategy {
    suspend fun performCheckin(): Result<AttendanceRecord>
    suspend fun performCheckout(): Result<AttendanceRecord>
}
