package com.gaden.checkin.domain.model

import kotlin.time.Instant

enum class LeaveType {
    ANNUAL, SICK, UNPAID
}

enum class LeaveStatus {
    PENDING, APPROVED, REJECTED
}

data class LeaveRequest(
    val id: String,
    val type: LeaveType,
    val startDateEpochDay: Long,
    val endDateEpochDay: Long,
    val reason: String,
    val status: LeaveStatus,
    val submittedAt: Instant,
)
