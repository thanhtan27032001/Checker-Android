package com.gaden.checkin.data.auth

import com.gaden.checkin.data.remote.dto.LoginResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionEventBus @Inject constructor() {
    private val _sessionExpiredEvent = Channel<Unit>()
    public val sessionExpiredEvent = _sessionExpiredEvent.receiveAsFlow()

    suspend fun notifySessionExpired() {
        _sessionExpiredEvent.send(Unit)
    }
}