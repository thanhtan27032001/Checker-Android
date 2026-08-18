package com.gaden.checkin.presentation.leave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gaden.checkin.domain.model.LeaveRequest
import com.gaden.checkin.domain.model.LeaveStatus
import com.gaden.checkin.domain.model.LeaveType
import com.gaden.checkin.presentation.theme.AttendanceTheme
import com.gaden.checkin.presentation.theme.Radius
import com.gaden.checkin.presentation.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LeaveScreen(
    onBackClicked: () -> Unit,
    viewModel: LeaveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.submitEvent.collect { result ->
            when (result) {
                is SubmitResult.Success -> {
                    showForm = false
                    snackBarHostState.showSnackbar("Submitted leave request")
                }
                is SubmitResult.Error -> {
                    snackBarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    LeaveContent(
        uiState = uiState,
        showForm = showForm,
        onBackClick = onBackClicked,
        onAddClick = { showForm = true },
        onDismissForm = { showForm = false },
        onSubmit = viewModel::submitLeaveRequest,
        snackBarHostState = snackBarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveContent(
    uiState: LeaveUiState,
    showForm: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onDismissForm: () -> Unit,
    onSubmit: (LeaveType, Long, Long, String) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leave request form") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "New request")
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.requests.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues).padding(Spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Empty least request. Click + to add new",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.requests, key = { it.id }) { request ->
                    LeaveRequestCard(request)
                }
            }
        }
    }

    if (showForm) {
        LeaveRequestFormSheet(
            isSubmitting = uiState.isSubmitting,
            onDismiss = onDismissForm,
            onSubmit = onSubmit,
        )
    }
}

@Composable
fun LeaveRequestCard(request: LeaveRequest) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val startDate = LocalDate.ofEpochDay(request.startDateEpochDay)
    val endDate = LocalDate.ofEpochDay(request.endDateEpochDay)

    Card(shape = RoundedCornerShape(Radius.md)) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = leaveTypeLabel(request.type), style = MaterialTheme.typography.titleMedium)
                StatusBadge(request.status)
            }

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = if (startDate == endDate) {
                    startDate.format(dateFormatter)
                } else {
                    "${startDate.format(dateFormatter)} - ${endDate.format(dateFormatter)}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(text = request.reason, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StatusBadge(status: LeaveStatus) {
    val (containerColor, contentColor, label) = when (status) {
        LeaveStatus.PENDING -> Triple(AttendanceTheme.colors.lateContainer, AttendanceTheme.colors.late, "Waiting for review")
        LeaveStatus.APPROVED -> Triple(AttendanceTheme.colors.onTimeContainer, AttendanceTheme.colors.onTime, "Approved")
        LeaveStatus.REJECTED -> Triple(AttendanceTheme.colors.absentContainer, AttendanceTheme.colors.absent, "Rejected")
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(Radius.sm),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private fun leaveTypeLabel(type: LeaveType): String = when (type) {
    LeaveType.ANNUAL -> "Annual leave"
    LeaveType.SICK -> "Sick leave"
    LeaveType.UNPAID -> "Unpaid salary leave"
}