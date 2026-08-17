package com.gaden.checkin.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

sealed class HistoryUiState {
    data object Loading: HistoryUiState()
    data class Ready(
        val yearMonth: YearMonth,
        val records: Map<Int, AttendanceRecord>,
    ): HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor (
    private val repository: AttendanceRepository,
): ViewModel() {
    var currentYearMonth = YearMonth.now()

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadMonth(currentYearMonth)
    }

    fun loadMonth(yearMonth: YearMonth) {
        currentYearMonth = yearMonth
        _uiState.value = HistoryUiState.Loading

        viewModelScope.launch {
            val records = repository.getMonthRecords(
                year = currentYearMonth.year,
                month = currentYearMonth.monthValue,
            )
            if (currentYearMonth == yearMonth) {
                _uiState.value = HistoryUiState.Ready(
                    yearMonth,
                    records
                )
            }
        }
    }

    fun goToPreviousMonth() = loadMonth(currentYearMonth.minusMonths(1))
    fun goToNextMonth() = loadMonth(currentYearMonth.plusMonths(1))
}