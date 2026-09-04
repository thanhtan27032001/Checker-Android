package com.gaden.checkin.data.mapper

import com.gaden.checkin.data.remote.dto.AttendanceResponse
import com.gaden.checkin.data.remote.dto.AttendanceResponseStatus
import com.gaden.checkin.data.remote.dto.AttendanceTodayResponse
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.AttendanceToday
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.domain.model.CheckInMethod.BUTTON
import com.gaden.checkin.domain.model.CheckInMethod.FACE
import com.gaden.checkin.domain.model.CheckInMethod.GPS
import com.gaden.checkin.domain.model.CheckInMethod.QR
import kotlin.time.Instant

fun AttendanceResponse.toDomain(): AttendanceRecord {
    val id = this.id ?: ""
    val checkInTime = this.checkInTime?.let { Instant.parse(it) }
    val checkOutTime = this.checkOutTime?.let { Instant.parse(it) }
    val method = mapCheckInMethod(this.checkInMethod)
    val status =
        if (this.checkInTime != null && this.checkOutTime != null) AttendanceStatus.CHECKED_OUT
        else {
            if (this.checkInTime != null) AttendanceStatus.CHECKED_IN
            else AttendanceStatus.NOT_CHECKED_IN
        }
    val isLate = this.status == AttendanceResponseStatus.Late.name

    return AttendanceRecord(
        id = id,
        checkinTime = checkInTime,
        checkoutTime = checkOutTime,
        method = method,
        status = status,
        isLate = isLate
    )
}

fun AttendanceTodayResponse.toDomain(): AttendanceToday {
    return AttendanceToday(
        hasCheckIn = this.hasCheckIn,
        hasCheckOut = this.hasCheckOut,
        record = this.record?.toDomain()
    )
}

private fun mapCheckInMethod(value: String?): CheckInMethod {
    if (value != null) {
        for (method in CheckInMethod.values()) {
            if (method.name.equals(value, ignoreCase = true)) return method
        }
    }
    return CheckInMethod.BUTTON
}

fun CheckInMethod.toApiString(): String {
    return when (this) {
        BUTTON -> "Button"
        GPS -> "Gps"
        QR -> "Qr"
        FACE -> "Face"
    }
}