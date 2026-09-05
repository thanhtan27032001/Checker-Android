package com.gaden.checkin.data.auth

import com.gaden.checkin.data.remote.ApiService
import com.gaden.checkin.data.remote.dto.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.success && response.data != null) {
                tokenManager.saveSession(
                    response.data.accessToken,
                    response.data.refreshToken,
                    response.data.employeeId
                )
                return Result.success(Unit)
            }
            else {
                return Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            System.err.println(e)
            return Result.failure(e)
        }
    }
}