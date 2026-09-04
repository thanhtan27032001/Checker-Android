package com.gaden.checkin.data.remote

import com.gaden.checkin.data.remote.dto.ApiResponse
import com.gaden.checkin.data.remote.dto.AttendanceResponse
import com.gaden.checkin.data.remote.dto.AttendanceTodayResponse
import com.gaden.checkin.data.remote.dto.CheckInRequest
import com.gaden.checkin.data.remote.dto.LoginRequest
import com.gaden.checkin.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/attendance/check-in")
    suspend fun checkIn(@Body request: CheckInRequest): ApiResponse<AttendanceResponse>

    @POST("api/attendance/check-out")
    suspend fun checkOut(@Query("employeeId") employeeId: String?): ApiResponse<AttendanceResponse>

    @GET("api/attendance/today")
    suspend fun getTodayStatus(): ApiResponse<AttendanceTodayResponse?>
}