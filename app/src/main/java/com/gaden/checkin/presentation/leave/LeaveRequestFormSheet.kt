package com.gaden.checkin.presentation.leave

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.gaden.checkin.domain.model.LeaveType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gaden.checkin.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestFormSheet(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (LeaveType, Long, Long, String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        LeaveRequestFormContent(
            isSubmitting = isSubmitting,
            onSubmit = onSubmit,
        )
    }
}

@SuppressLint("RememberInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaveRequestFormContent(
    isSubmitting: Boolean,
    onSubmit: (LeaveType, Long, Long, String) -> Unit,
) {
    var selectedType by remember { mutableStateOf(LeaveType.ANNUAL) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var reason by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .padding(bottom = Spacing.xl),
    ) {
        Text("Create leave request", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(Spacing.md))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            LeaveType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    shape = SegmentedButtonDefaults.itemShape(index, LeaveType.entries.size),
                ) {
                    Text(leaveTypeShortLabel(type))
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            OutlinedTextField(
                value = startDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("From date") },
                modifier = Modifier.weight(1f).clickable { showStartDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            OutlinedTextField(
                value = endDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("To date") },
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = MutableInteractionSource()
                    ) { showEndDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        Spacer(Modifier.height(Spacing.md))

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it; validationError = null },
            label = { Text("Reason") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
        )

        if (validationError != null) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = validationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        Button(
            onClick = {
                val start = startDate
                val end = endDate
                when {
                    start == null || end == null -> validationError = "Please select start and end date"
                    end.isBefore(start) -> validationError = "End date must be later than start date"
                    reason.isBlank() -> validationError = "Please note reason"
                    else -> onSubmit(selectedType, start.toEpochDay(), end.toEpochDay(), reason)
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Send request")
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialogWrapper(
            onDateSelected = { startDate = it; showStartDatePicker = false },
            onDismiss = { showStartDatePicker = false },
        )
    }
    if (showEndDatePicker) {
        DatePickerDialogWrapper(
            onDateSelected = { endDate = it; showEndDatePicker = false },
            onDismiss = { showEndDatePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogWrapper(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onDateSelected(date)
                    }
                },
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun leaveTypeShortLabel(type: LeaveType): String = when (type) {
    LeaveType.ANNUAL -> "Annual"
    LeaveType.SICK -> "Sick"
    LeaveType.UNPAID -> "Unpaid"
}
