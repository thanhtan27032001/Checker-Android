package com.gaden.checkin.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Material 3 chỉ có sẵn primary/secondary/error... không có khái niệm
 * "trạng thái nghiệp vụ" như onTime/late/absent/onLeave.
 * Đây là cách mở rộng theme chuẩn production: tạo 1 data class màu riêng,
 * cung cấp qua CompositionLocal, inject song song với MaterialTheme.
 */
data class AttendanceColors(
    val onTime: Color,
    val onTimeContainer: Color,
    val late: Color,
    val lateContainer: Color,
    val absent: Color,
    val absentContainer: Color,
    val onLeave: Color,
    val onLeaveContainer: Color,
)

val LightAttendanceColors = AttendanceColors(
    onTime = StatusOnTimeLight,
    onTimeContainer = StatusOnTimeContainerLight,
    late = StatusLateLight,
    lateContainer = StatusLateContainerLight,
    absent = StatusAbsentLight,
    absentContainer = StatusAbsentContainerLight,
    onLeave = StatusOnLeaveLight,
    onLeaveContainer = StatusOnLeaveContainerLight,
)

val DarkAttendanceColors = AttendanceColors(
    onTime = StatusOnTimeDark,
    onTimeContainer = StatusOnTimeContainerDark,
    late = StatusLateDark,
    lateContainer = StatusLateContainerDark,
    absent = StatusAbsentDark,
    absentContainer = StatusAbsentContainerDark,
    onLeave = StatusOnLeaveDark,
    onLeaveContainer = StatusOnLeaveContainerDark,
)

val LocalAttendanceColors = staticCompositionLocalOf {
    LightAttendanceColors // giá trị mặc định, sẽ bị override trong ChamCongTheme
}

// Cách dùng trong UI: AttendanceTheme.colors.late
object AttendanceTheme {
    val colors: AttendanceColors
        @Composable
        get() = LocalAttendanceColors.current
}
