package com.gaden.checkin.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.presentation.theme.AttendanceTheme
import com.gaden.checkin.presentation.theme.Spacing
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onPreviousMonthClicked = { viewModel.loadMonth(viewModel.currentYearMonth.minusMonths(1)) },
        onNextMonthClicked = { viewModel.loadMonth(viewModel.currentYearMonth.plusMonths(1)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    onBackClicked: () -> Unit,
    onPreviousMonthClicked: () -> Unit,
    onNextMonthClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử chấm công") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState) {
                is HistoryUiState.Loading -> {
                    LoadingScreen()
                }
                is HistoryUiState.Ready -> {
                    MonthCalendar(
                        yearMonth = uiState.yearMonth,
                        records = uiState.records,
                        onPreviousMonthClick = onPreviousMonthClicked,
                        onNextMonthClick = onNextMonthClicked,
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MonthCalendar(
    yearMonth: YearMonth,
    records: Map<Int, AttendanceRecord>,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonthClick) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Tháng trước")
            }
            Text(
                text = "Tháng ${yearMonth.monthValue}, ${yearMonth.year}",
                style = MaterialTheme.typography.titleMedium,
            )
            IconButton(onClick = onNextMonthClick) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Tháng sau")
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // Header thứ trong tuần: T2 -> CN (Locale VN, tuần bắt đầu Thứ 2)
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekDays = listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
            )
            weekDays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale("vi")),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // Số ô trống đầu tháng = vị trí thứ-trong-tuần của ngày 1 (0 nếu ngày 1 là Thứ 2)
        val firstDayOffset = yearMonth.atDay(1).dayOfWeek.value - 1
        val daysInMonth = yearMonth.lengthOfMonth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // Ô trống canh lề đầu tháng — key riêng để Compose không nhầm với ngày thật
            items(firstDayOffset, key = { "blank-$it" }) {
                Spacer(Modifier.aspectRatio(1f))
            }

            items(daysInMonth, key = { it }) { index ->
                val day = index + 1
                DayCell(day = day, record = records[day])
            }
        }
    }
}

@Composable
fun DayCell(day: Int, record: AttendanceRecord?) {
    val (containerColor, contentColor) = when {
        record == null -> MaterialTheme.colorScheme.surfaceVariant to
                MaterialTheme.colorScheme.onSurfaceVariant
        record.isLate -> AttendanceTheme.colors.lateContainer to AttendanceTheme.colors.late
        else -> AttendanceTheme.colors.onTimeContainer to AttendanceTheme.colors.onTime
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}