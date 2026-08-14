package com.gaden.checkin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [PendingAttendanceEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(AttendanceConverters::class)
abstract class CheckInDatabase: RoomDatabase() {
    abstract fun pendingAttendanceDao(): PendingAttendanceDao
}
