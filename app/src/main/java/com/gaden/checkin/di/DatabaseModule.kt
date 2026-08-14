package com.gaden.checkin.di

import android.content.Context
import androidx.room.Room
import com.gaden.checkin.data.local.CheckInDatabase
import com.gaden.checkin.data.local.PendingAttendanceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideCheckInDatabase(@ApplicationContext context: Context): CheckInDatabase {
        return Room
            .databaseBuilder(context, CheckInDatabase::class.java, "checkin.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePendingAttendanceDao(database: CheckInDatabase): PendingAttendanceDao {
        return database.pendingAttendanceDao()
    }
}