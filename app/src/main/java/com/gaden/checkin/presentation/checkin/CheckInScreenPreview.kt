package com.gaden.checkin.presentation.checkin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.presentation.theme.CheckinTheme
import kotlin.time.Clock


@Preview(showBackground = true, name = "Not checked in")
@Composable
private fun CheckInContentNotCheckedInPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                status = AttendanceStatus.NOT_CHECKED_IN,
                lastRecord = null,
            ),
            onCheckInClicked = {},
            onHistoryClicked = {},
            onLeaveClicked = {},
            onDashboardClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Checked in")
@Composable
private fun CheckInContentCheckedInPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                status = AttendanceStatus.CHECKED_IN,
                lastRecord = AttendanceRecord(
                    id = "preview-1",
                    checkinTime = Clock.System.now(),
                    checkoutTime = null,
                    method = CheckInMethod.BUTTON,
                    status = AttendanceStatus.CHECKED_IN,
                ),
            ),
            onCheckInClicked = {},
            onHistoryClicked = {},
            onLeaveClicked = {},
            onDashboardClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Submitting (loading)")
@Composable
private fun CheckInContentSubmittingPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                status = AttendanceStatus.NOT_CHECKED_IN,
                lastRecord = null,
                isSubmitting = true,
            ),
            onCheckInClicked = {},
            onHistoryClicked = {},
            onLeaveClicked = {},
            onDashboardClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Initial loading")
@Composable
private fun CheckInContentLoadingPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Loading,
            onCheckInClicked = {},
            onHistoryClicked = {},
            onLeaveClicked = {},
            onDashboardClick = {},
        )
    }
}