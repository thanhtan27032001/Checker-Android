package com.gaden.checkin.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaden.checkin.domain.model.DashboardRepository
import com.gaden.checkin.domain.model.DashboardSummary
import com.gaden.checkin.domain.model.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Ready(
        val summary: DashboardSummary,
        val employees: List<Employee>,
    ) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            val (summary, employees) = coroutineScope {
                val summaryDeferred = async { repository.getSummary() }
                val employeesDeferred = async { repository.getEmployeesToday() }
                summaryDeferred.await() to employeesDeferred.await()
            }

            _uiState.value = DashboardUiState.Ready(summary = summary, employees = employees)
        }
    }
}