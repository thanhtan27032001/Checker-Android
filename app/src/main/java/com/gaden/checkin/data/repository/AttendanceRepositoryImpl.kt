package com.gaden.checkin.data.repository

import com.gaden.checkin.data.auth.TokenManager
import com.gaden.checkin.data.mapper.toApiString
import com.gaden.checkin.data.mapper.toDomain
import com.gaden.checkin.data.remote.ApiService
import com.gaden.checkin.data.remote.dto.CheckInRequest
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceRepository
import com.gaden.checkin.domain.model.CheckInMethod
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AttendanceRepository {
    override suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord> {
        val employeeId = tokenManager.employeeIdFlow.first()
        if (employeeId != null) {
            try {
                val response = apiService.checkIn(CheckInRequest(employeeId, method.toApiString(), null))
                return Result.success(response.data!!.toDomain())
            }
            catch (e: Exception) {
                return Result.failure(e)
            }
        }
        else {
            return Result.failure(Exception("Employee ID not found"))
        }
    }

    override suspend fun checkOut(method: CheckInMethod): Result<AttendanceRecord> {
        TODO("Not yet implemented")
    }

    override suspend fun getTodayStatus(): AttendanceRecord? {
        TODO("Not yet implemented")
    }

    override suspend fun getMonthRecords(
        year: Int,
        month: Int
    ): Map<Int, AttendanceRecord> {
        TODO("Not yet implemented")
    }
}