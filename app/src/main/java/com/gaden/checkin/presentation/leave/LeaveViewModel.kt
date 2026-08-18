package com.gaden.checkin.presentation.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaden.checkin.domain.model.LeaveRequest
import com.gaden.checkin.domain.model.LeaveType
import com.gaden.checkin.domain.repository.LeaveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaveUiState(
    val requests: List<LeaveRequest> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
)

sealed interface SubmitResult {
    data object Success : SubmitResult
    data class Error(val message: String) : SubmitResult
}

@HiltViewModel
class LeaveViewModel @Inject constructor(
    private val leaveRepository: LeaveRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()

    private val _submitEvent = Channel<SubmitResult>()
    val submitEvent = _submitEvent.receiveAsFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val requests = leaveRepository.getLeaveRequests()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                requests = requests
            )
        }
    }

    fun submitLeaveRequest(
        type: LeaveType,
        startDateEpochDay: Long,
        endDateEpochDay: Long,
        reason: String,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true
            )

            val result = leaveRepository.submitLeaveRequest(
                type = type,
                startDateEpochDay = startDateEpochDay,
                endDateEpochDay = endDateEpochDay,
                reason = reason,
            )

            result.fold(
                onSuccess = {
                    _submitEvent.send(SubmitResult.Success)
                    loadRequests()
                },
                onFailure = { error ->
                    _submitEvent.send(
                        SubmitResult.Error(error.message ?: "Something went wrong")
                    )
                }
            )

            _uiState.value = _uiState.value.copy(isSubmitting = false)
        }
    }
}