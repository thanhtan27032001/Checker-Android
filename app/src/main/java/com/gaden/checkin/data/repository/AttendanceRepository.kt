package com.gaden.checkin.data.repository

import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.CheckInStrategy
import com.gaden.checkin.domain.model.CheckinMethod
import javax.inject.Inject

interface AttendanceRepository {
    suspend fun checkIn(method: CheckinMethod): Result<AttendanceRecord>
    suspend fun checkout(method: CheckinMethod): Result<AttendanceRecord>
    suspend fun getTodayStatus(): AttendanceRecord?
}

class ButtonCheckInStrategy @Inject constructor (
    private val repository: AttendanceRepository
) : CheckInStrategy {
    override suspend fun performCheckin(): Result<AttendanceRecord> {
         return repository.checkIn(CheckinMethod.BUTTON);
    }

    override suspend fun performCheckout(): Result<AttendanceRecord> {
        return repository.checkout(CheckinMethod.BUTTON)
    }

}