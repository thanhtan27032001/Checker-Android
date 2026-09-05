package com.gaden.checkin.data.auth

import com.gaden.checkin.data.remote.ApiService
import com.gaden.checkin.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: Provider<ApiService>,
    private val sessionEventBus: SessionEventBus,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }
        val token = runBlocking { tokenManager.refreshTokenFlow.first() }
        if (token != null) {
            val tokenResponse = runBlocking {
                apiService.get().refreshToken(RefreshTokenRequest(token))
            }
            if (tokenResponse.success && tokenResponse.data != null) {
                 runBlocking {
                    tokenManager.saveSession(
                        tokenResponse.data.accessToken,
                        tokenResponse.data.refreshToken,
                        tokenResponse.data.employeeId
                    )
                }
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.data.accessToken}")
                    .build()
            }
        }
        runBlocking {
            tokenManager.clearSession()
            sessionEventBus.notifySessionExpired()
        }
        return null
    }
}

private fun responseCount(response: Response): Int {
    var result = 1
    var currentResponse = response.priorResponse
    while (currentResponse != null) {
        result++
        currentResponse = currentResponse.priorResponse
    }
    return result
}