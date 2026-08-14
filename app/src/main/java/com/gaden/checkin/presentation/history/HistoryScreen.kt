package com.gaden.checkin.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.presentation.theme.AttendanceTheme
import com.gaden.checkin.presentation.theme.Spacing
import com.gaden.checkin.presentation.theme.Typography
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.toJavaInstant

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
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance History") },
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
                        onDayClicked = { day -> selectedDay = day}
                    )

                    if (selectedDay != null) {
                        DayDetailSheet(
                            yearMonth = uiState.yearMonth,
                            day = selectedDay!!,
                            record = uiState.records[selectedDay],
                            onDismiss = { selectedDay = null }
                        )
                    }
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
    onDayClicked: (day: Int) -> Unit,
) {
    Column(modifier = Modifier.padding(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonthClick) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
            }
            Text(
                text = "Month ${yearMonth.monthValue}, ${yearMonth.year}",
                style = MaterialTheme.typography.titleMedium,
            )
            IconButton(onClick = onNextMonthClick) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // Weekday header: Mon -> Sun (Locale EN, week starts on Monday)
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekDays = listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
            )
            weekDays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // Initial empty slots = day-of-week index of day 1 (0 if day 1 is Monday)
        val firstDayOffset = yearMonth.atDay(1).dayOfWeek.value - 1
        val daysInMonth = yearMonth.lengthOfMonth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // Empty slots for month alignment — unique key to distinguish from real days
            items(firstDayOffset, key = { "blank-$it" }) {
                Spacer(Modifier.aspectRatio(1f))
            }

            items(daysInMonth, key = { it }) { index ->
                val day = index + 1
                DayCell(day = day, record = records[day], onClicked = onDayClicked)
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    record: AttendanceRecord?,
    onClicked: (Int) -> Unit = {},
) {
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
            .background(containerColor)
            .clickable(onClick = { onClicked(day) }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DayDetailSheet(
    yearMonth: YearMonth,
    day: Int,
    record: AttendanceRecord?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        DayDetailSheetContent(
            yearMonth = yearMonth,
            day = day,
            record = record,
        )
    }
}

@Composable
private fun DayDetailSheetContent(
    yearMonth: YearMonth,
    day: Int,
    record: AttendanceRecord?,
) {
    val timeFormatter = remember {
        java.time.format.DateTimeFormatter.ofPattern("HH:mm")
            .withZone(java.time.ZoneId.systemDefault())
    }

    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 16.dp)

    if (record == null) {
        return Text("No information in this day", modifier = modifier)
    }

    val checkinTime = record.checkinTime?.let {
        timeFormatter.format(record.checkinTime.toJavaInstant())
    } ?: "-"
    val checkoutTime = record.checkoutTime?.let {
        timeFormatter.format(record.checkoutTime.toJavaInstant())
    } ?: "-"
    val status = if (record.isLate) "Late" else "On time"
    val statusColor = if (record.isLate) AttendanceTheme.colors.late else AttendanceTheme.colors.onTime

    Column(
        modifier = modifier
    ) {
        Text("Date ${day}/${yearMonth.monthValue}/${yearMonth.year}", style = Typography.titleLarge)
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        DetailRow(
            label = "Check-in",
            value = checkinTime,
        )
        DetailRow(
            label = "Check-out",
            value = checkoutTime
        )
        DetailRow(
            label = "Status",
            value = status,
            valueColor = statusColor,
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, color = valueColor)
    }
}

@Preview(showBackground = true, name = "Day detail sheet")
@Composable
fun DayDetailSheetPreview() {
    DayDetailSheetContent(
        yearMonth = YearMonth.now(),
        day = 1,
        record = AttendanceRecord(
            id = "preview-1",
            checkinTime = Clock.System.now(),
            checkoutTime = null,
            method = CheckInMethod.BUTTON,
            status = AttendanceStatus.CHECKED_IN,
        )
    )
}
