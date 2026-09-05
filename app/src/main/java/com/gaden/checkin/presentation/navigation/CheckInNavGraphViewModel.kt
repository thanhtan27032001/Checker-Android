package com.gaden.checkin.presentation.navigation

import androidx.lifecycle.ViewModel
import com.gaden.checkin.data.auth.SessionEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckInNavGraphViewModel @Inject constructor(
    private val sessionEventBus: SessionEventBus
) : ViewModel() {
    val sessionExpiredEvent = sessionEventBus.sessionExpiredEvent
}