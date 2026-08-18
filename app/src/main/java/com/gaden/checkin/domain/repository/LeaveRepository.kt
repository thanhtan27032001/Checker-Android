package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.LeaveRequest
import com.gaden.checkin.domain.model.LeaveType

interface LeaveRepository {
    suspend fun submitLeaveRequest(
        type: LeaveType,
        startDateEpochDay: Long,
        endDateEpochDay: Long,
        reason: String,
    ): Result<LeaveRequest>

    suspend fun getLeaveRequests(): List<LeaveRequest>
}