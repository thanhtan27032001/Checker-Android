package com.gaden.checkin.presentation.checkin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.presentation.theme.CheckinTheme
import kotlin.time.Clock


@Preview(showBackground = true, name = "Chưa check-in")
@Composable
private fun CheckInContentNotCheckedInPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                status = AttendanceStatus.NOT_CHECKED_IN,
                lastRecord = null,
            ),
            onCheckInClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "Đã check-in")
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
        )
    }
}

@Preview(showBackground = true, name = "Đang submit (loading)")
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
        )
    }
}

@Preview(showBackground = true, name = "Loading ban đầu")
@Composable
private fun CheckInContentLoadingPreview() {
    CheckinTheme {
        CheckInContent(
            uiState = CheckInUiState.Loading,
            onCheckInClicked = {},
        )
    }
}