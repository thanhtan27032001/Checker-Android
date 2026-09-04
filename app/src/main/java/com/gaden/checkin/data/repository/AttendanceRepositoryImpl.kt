package com.gaden.checkin.data.repository

import android.util.Log
import com.gaden.checkin.data.auth.TokenManager
import com.gaden.checkin.data.mapper.toApiString
import com.gaden.checkin.data.mapper.toDomain
import com.gaden.checkin.data.remote.ApiService
import com.gaden.checkin.data.remote.dto.CheckInRequest
import com.gaden.checkin.data.remote.parseErrorMessage
import com.gaden.checkin.data.remote.safeApiCall
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceRepository
import com.gaden.checkin.domain.model.AttendanceToday
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.domain.repository.FakeAttendanceRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AttendanceRepository {
    override suspend fun checkIn(method: CheckInMethod): Result<AttendanceRecord> {
        val employeeId = tokenManager.employeeIdFlow.first()
        return if (employeeId != null) {
            safeApiCall("checkIn") {
                val response = apiService.checkIn(CheckInRequest(employeeId, method.toApiString(), null))
                response.data!!.toDomain()
            }
        } else {
            Result.failure(Exception("Employee ID not found"))
        }
    }

    override suspend fun checkOut(method: CheckInMethod): Result<AttendanceRecord> {
        val employeeId = tokenManager.employeeIdFlow.first()
        return if (employeeId != null) {
            safeApiCall("checkOut") {
                val response = apiService.checkOut(employeeId)
                response.data!!.toDomain()
            }
        } else {
            Result.failure(Exception("Employee ID not found"))
        }
    }

    override suspend fun getTodayStatus(): Result<AttendanceToday?> {
        return safeApiCall("getTodayStatus") {
            val response = apiService.getTodayStatus()
            response.data?.toDomain()
        }
    }

    override suspend fun getMonthRecords(
        year: Int,
        month: Int
    ): Result<Map<Int, AttendanceRecord>> {
        return FakeAttendanceRepository().getMonthRecords(year, month)
    }
}