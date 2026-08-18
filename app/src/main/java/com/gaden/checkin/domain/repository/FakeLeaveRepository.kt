package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.LeaveRepository
import com.gaden.checkin.domain.model.LeaveRequest
import com.gaden.checkin.domain.model.LeaveStatus
import com.gaden.checkin.domain.model.LeaveType
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class FakeLeaveRepository @Inject constructor(): LeaveRepository {
    val requests = mutableListOf<LeaveRequest>(
        LeaveRequest(
            id = "seed-1",
            type = LeaveType.ANNUAL,
            startDateEpochDay = java.time.LocalDate.now().minusDays(10).toEpochDay(),
            endDateEpochDay = java.time.LocalDate.now().minusDays(9).toEpochDay(),
            reason = "Về quê giỗ ông bà",
            status = LeaveStatus.APPROVED,
            submittedAt = Clock.System.now(),
        ),
        LeaveRequest(
            id = "seed-2",
            type = LeaveType.SICK,
            startDateEpochDay = java.time.LocalDate.now().minusDays(3).toEpochDay(),
            endDateEpochDay = java.time.LocalDate.now().minusDays(3).toEpochDay(),
            reason = "Sốt, đi khám bệnh",
            status = LeaveStatus.PENDING,
            submittedAt = Clock.System.now(),
        ),
    )

    override suspend fun submitLeaveRequest(
        type: LeaveType,
        startDateEpochDay: Long,
        endDateEpochDay: Long,
        reason: String
    ): Result<LeaveRequest> {
        delay(600)
        if (reason.isBlank()) {
            return Result.failure(IllegalArgumentException("Please note reason"))
        }
        if (endDateEpochDay < startDateEpochDay) {
            return Result.failure(IllegalArgumentException("End date must be later than start date"))
        }

        val newRequest = LeaveRequest(
            id = "leave-${System.currentTimeMillis()}",
            type = type,
            startDateEpochDay = startDateEpochDay,
            endDateEpochDay = endDateEpochDay,
            reason = reason,
            status = LeaveStatus.PENDING,
            submittedAt = Clock.System.now(),
        )
        requests.add(0, newRequest)
        return Result.success(newRequest)
    }

    override suspend fun getLeaveRequests(): List<LeaveRequest> {
        delay(300)
        return requests.toList()
    }
}