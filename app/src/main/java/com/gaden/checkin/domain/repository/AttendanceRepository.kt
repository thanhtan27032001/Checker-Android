package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.CheckInStrategy
import com.gaden.checkin.domain.model.CheckInMethod
import javax.inject.Inject

interface AttendanceRepository {
    suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord>
    suspend fun checkout(method: CheckInMethod): Result<AttendanceRecord>
    suspend fun getTodayStatus(): AttendanceRecord?
}

class ButtonCheckInStrategy @Inject constructor (
    private val repository: AttendanceRepository
) : CheckInStrategy {
    override suspend fun performCheckin(): Result<AttendanceRecord> {
         return repository.checkIn(CheckInMethod.BUTTON);
    }

    override suspend fun performCheckout(): Result<AttendanceRecord> {
        return repository.checkout(CheckInMethod.BUTTON)
    }

}