package com.gaden.checkin.presentation.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.presentation.theme.AttendanceTheme
import com.gaden.checkin.presentation.theme.Radius
import com.gaden.checkin.presentation.theme.Spacing
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CheckInScreen(viewModel: CheckInViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CheckInContent(
        uiState,
        viewModel::onCheckInClicked
    )
}

@Composable
fun CheckInContent(
    uiState: CheckInUIState,
    onCheckInClicked: () -> Unit
) {
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(1000)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.md))

            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(Spacing.lg))

            when (uiState) {
                is CheckInUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is CheckInUIState.Ready -> {
                    CheckInCard(
                        status = uiState.status,
                        isSubmitting = uiState.isSubmitting,
                        onCheckInClick = onCheckInClicked,
                    )
                }
                is CheckInUIState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckInCard(
    status: AttendanceStatus,
    isSubmitting: Boolean,
    onCheckInClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StatusIcon(status)

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = statusLabel(status),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(Spacing.lg))

            Button(
                onClick = onCheckInClick,
                enabled = !isSubmitting && status != AttendanceStatus.CHECKED_OUT,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(Radius.lg),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = when (status) {
                            AttendanceStatus.NOT_CHECKED_IN -> "Chấm công vào"
                            AttendanceStatus.CHECKED_IN -> "Chấm công ra"
                            AttendanceStatus.CHECKED_OUT -> "Đã hoàn tất hôm nay"
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIcon(status: AttendanceStatus) {
    val (containerColor, iconTint) = when (status) {
        AttendanceStatus.NOT_CHECKED_IN -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        AttendanceStatus.CHECKED_IN -> AttendanceTheme.colors.onTimeContainer to AttendanceTheme.colors.onTime
        AttendanceStatus.CHECKED_OUT -> AttendanceTheme.colors.onLeaveContainer to AttendanceTheme.colors.onLeave
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .background(containerColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Schedule,
            contentDescription = null,
            tint = iconTint,
        )
    }
}

private fun statusLabel(status: AttendanceStatus): String = when (status) {
    AttendanceStatus.NOT_CHECKED_IN -> "Chưa chấm công vào"
    AttendanceStatus.CHECKED_IN -> "Đang trong ca làm việc"
    AttendanceStatus.CHECKED_OUT -> "Đã chấm công ra"
}