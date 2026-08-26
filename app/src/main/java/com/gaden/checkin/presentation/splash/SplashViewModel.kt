package com.gaden.checkin.presentation.splash

import androidx.lifecycle.ViewModel
import com.gaden.checkin.data.auth.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val tokenManager: TokenManager
) : ViewModel() {
    suspend fun checkLoginStatus() : Boolean {
        val accessToken = tokenManager.accessTokenFlow.first()
        val employeeId = tokenManager.employeeIdFlow.first()

        return accessToken != null && employeeId != null
    }
}