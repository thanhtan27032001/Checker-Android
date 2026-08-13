package com.gaden.checkin.presentation.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInStrategy
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CheckInUIState {
    data object Loading : CheckInUIState
    data class Ready(
        val status: AttendanceStatus,
        val lastRecord: AttendanceRecord?,
        val isSubmitting: Boolean = false,
    ) : CheckInUIState
    data class Error(val message: String) : CheckInUIState
}

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val checkInStrategy: CheckInStrategy
): ViewModel() {
    private val _uiState = MutableStateFlow<CheckInUIState>(CheckInUIState.Loading)
    val uiState: StateFlow<CheckInUIState> = _uiState.asStateFlow()

    init {
        loadTodayStatus()
    }

    private fun loadTodayStatus() {
        viewModelScope.launch {
            // TODO: Load today's status from repo impl
            _uiState.value = CheckInUIState.Ready(
                status = AttendanceStatus.NOT_CHECKED_IN,
                lastRecord = null,
            )
        }
    }

    fun onCheckInClicked() {
        val currentState = _uiState.value
        if (currentState !is CheckInUIState.Ready || currentState.isSubmitting) return

        viewModelScope.launch {
            val result = if (currentState.status == AttendanceStatus.NOT_CHECKED_IN) {
                checkInStrategy.performCheckin()
            } else {
                checkInStrategy.performCheckout()
            }

            _uiState.value = result.fold(
                onSuccess = { record ->
                    val newStatus: AttendanceStatus = if (record.checkoutTime != null) {
                        AttendanceStatus.CHECKED_OUT
                    } else {
                        AttendanceStatus.CHECKED_IN
                    }
                    CheckInUIState.Ready(
                        status = newStatus,
                        lastRecord = record,
                        isSubmitting = false,
                    )
                },
                onFailure = {
                    currentState.copy(
                        isSubmitting = false
                    )
                }
            )
        }
    }
}