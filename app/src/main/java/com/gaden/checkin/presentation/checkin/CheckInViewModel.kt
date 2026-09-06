package com.gaden.checkin.presentation.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaden.checkin.data.auth.TokenManager
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceRepository
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInStrategy
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface CheckInUiState {
    data object Loading : CheckInUiState
    data class Ready(
        val status: AttendanceStatus,
        val lastRecord: AttendanceRecord?,
        val isSubmitting: Boolean = false,
    ) : CheckInUiState
    data class Error(val message: String) : CheckInUiState
}

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val checkInStrategy: CheckInStrategy,
    private val attendanceRepository: AttendanceRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<CheckInUiState>(CheckInUiState.Loading)
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private val _errorEvent = Channel<String>(capacity = Channel.BUFFERED)
    val errorEvent = _errorEvent.receiveAsFlow()

    init {
        loadTodayStatus()
    }

    private fun loadTodayStatus() {
        _uiState.value = CheckInUiState.Loading
        viewModelScope.launch {
            attendanceRepository
                .getTodayStatus()
                .fold(
                    onSuccess = { todayStatus ->
                        val status = if (todayStatus?.hasCheckOut == true) {
                            AttendanceStatus.CHECKED_OUT
                        } else if (todayStatus?.hasCheckIn == true) {
                            AttendanceStatus.CHECKED_IN
                        } else {
                            AttendanceStatus.NOT_CHECKED_IN
                        }
                        _uiState.value = CheckInUiState.Ready(
                            status = status,
                            lastRecord = todayStatus?.record,
                        )
                    },
                    onFailure = {
                        _uiState.value = CheckInUiState.Ready(
                            status = AttendanceStatus.NOT_CHECKED_IN,
                            lastRecord = null,
                        )
                    }
                )

        }
    }

    fun onCheckInClicked() {
        val currentState = _uiState.value
        if (currentState !is CheckInUiState.Ready || currentState.isSubmitting) return

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
                    CheckInUiState.Ready(
                        status = newStatus,
                        lastRecord = record,
                        isSubmitting = false,
                    )
                },
                onFailure = { error ->
                    _errorEvent.trySend(mapErrorToMessage(error))
                    currentState.copy(
                        isSubmitting = false
                    )
                }
            )
        }
    }

    private fun mapErrorToMessage(error: Throwable): String {
        return when (error.message?.contains("Network", ignoreCase = true)) {
            true -> "Network error"
            else -> error.message.toString()
        }
    }
}