package com.gaden.checkin.data.remote.dto

import com.gaden.checkin.domain.model.AttendanceRecord
import kotlinx.serialization.Serializable

enum class AttendanceResponseStatus {
    OnTime, Late, EarlyLeave, MissingCheckout, OnLeave
}

@Serializable
data class AttendanceResponse(
    val id: String?,
    val employeeId: String?,
    val employeeName: String?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val checkInMethod: String?,
    val status: String?,
    val workedHours: Double?
) {
}

@Serializable
data class AttendanceTodayResponse(
    val hasCheckIn: Boolean = false,
    val hasCheckOut: Boolean = false,
    val record: AttendanceResponse?,
)