package com.gaden.checkin.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.DashboardSummary
import com.gaden.checkin.domain.model.Employee
import com.gaden.checkin.presentation.theme.AttendanceTheme
import com.gaden.checkin.presentation.theme.Radius
import com.gaden.checkin.presentation.theme.Spacing

@Composable
fun DashboardScreen(
    onBackClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is DashboardUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Ready -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    item { SummaryGrid(uiState.summary) }
                    item {
                        Text(
                            text = "Today status",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = Spacing.sm),
                        )
                    }
                    items(uiState.employees, key = { it.id }) { employee ->
                        EmployeeRow(employee)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryGrid(summary: DashboardSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SummaryCard(
                label = "Total Employees",
                value = summary.totalEmployees.toString(),
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                label = "Checked-in",
                value = summary.checkedInToday.toString(),
                valueColor = AttendanceTheme.colors.onTime,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SummaryCard(
                label = "Late",
                value = summary.lateToday.toString(),
                valueColor = AttendanceTheme.colors.late,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                label = "Leave",
                value = summary.onLeaveToday.toString(),
                valueColor = AttendanceTheme.colors.onLeave,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = if (valueColor == androidx.compose.ui.graphics.Color.Unspecified) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    valueColor
                },
            )
        }
    }
}

@Composable
private fun EmployeeRow(employee: Employee) {
    Card(shape = RoundedCornerShape(Radius.md)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = employee.fullName.firstOrNull()?.toString() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Spacer(Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = employee.fullName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = employee.department,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            StatusDot(employee.todayStatus)
        }
    }
}

@Composable
private fun StatusDot(status: AttendanceStatus) {
    val color = when (status) {
        AttendanceStatus.NOT_CHECKED_IN -> AttendanceTheme.colors.onLeave
        AttendanceStatus.CHECKED_IN -> AttendanceTheme.colors.onTime
        AttendanceStatus.CHECKED_OUT -> AttendanceTheme.colors.late
    }
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}