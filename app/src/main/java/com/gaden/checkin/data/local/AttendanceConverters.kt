package com.gaden.checkin.data.local

import androidx.room.TypeConverter
import com.gaden.checkin.domain.model.CheckInMethod

class AttendanceConverters {
    @TypeConverter
    fun fromCheckInMethod(method: CheckInMethod): String = method.name

    @TypeConverter
    fun toCheckInMethod(value: String): CheckInMethod = CheckInMethod.valueOf(value)

    @TypeConverter
    fun fromPendingActionType(type: PendingActionType): String = type.name

    @TypeConverter
    fun toPendingActionType(value: String): PendingActionType = PendingActionType.valueOf(value)
}