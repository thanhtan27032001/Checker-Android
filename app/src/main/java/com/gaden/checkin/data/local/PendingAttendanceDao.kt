package com.gaden.checkin.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingAttendanceDao {
    @Insert
    suspend fun insert(entity: PendingAttendanceEntity): Long

    @Query("SELECT * FROM pending_attendance ORDER BY capturedAtMillis ASC")
    suspend fun getAllPending(): List<PendingAttendanceEntity>

    // Flow used for showing number of pending attendance
    @Query("SELECT COUNT(*) FROM pending_attendance")
    fun observePendingCount(): Flow<Int>

    @Update
    suspend fun update(entity: PendingAttendanceEntity)

    @Delete
    suspend fun delete(entity: PendingAttendanceEntity)
}