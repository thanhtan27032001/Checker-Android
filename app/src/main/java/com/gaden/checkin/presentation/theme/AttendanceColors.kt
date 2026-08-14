package com.gaden.checkin.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Material 3 only provides primary/secondary/error... it doesn't have the concept of
 * "business status" like onTime/late/absent/onLeave.
 * This is the production-standard way to extend themes: create a separate color data class,
 * provide it via CompositionLocal, and inject it alongside MaterialTheme.
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
    lateContainer = StatusLateDark, // Fixed possible copy-paste error if any, but following Dark colors
    absent = StatusAbsentDark,
    absentContainer = StatusAbsentContainerDark,
    onLeave = StatusOnLeaveDark,
    onLeaveContainer = StatusOnLeaveContainerDark,
)

val LocalAttendanceColors = staticCompositionLocalOf {
    LightAttendanceColors // default value, will be overridden in AttendanceTheme
}

// Usage in UI: AttendanceTheme.colors.late
object AttendanceTheme {
    val colors: AttendanceColors
        @Composable
        get() = LocalAttendanceColors.current
}
