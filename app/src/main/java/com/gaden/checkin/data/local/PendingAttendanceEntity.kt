package com.gaden.checkin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gaden.checkin.domain.model.CheckInMethod

enum class PendingActionType {
    CHECK_IN,
    CHECK_OUT
}

@Entity(tableName = "pending_attendance")
data class PendingAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: PendingActionType,
    val method: CheckInMethod,
    val capturedAtMillis: Long,
    val retryCount: Int = 0,
)