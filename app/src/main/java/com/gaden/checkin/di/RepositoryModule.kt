package com.gaden.checkin.di

import com.gaden.checkin.data.repository.AttendanceRepository
import com.gaden.checkin.data.repository.ButtonCheckInStrategy
import com.gaden.checkin.data.repository.FakeAttendanceRepository
import com.gaden.checkin.domain.model.CheckInStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAttendanceRepository(impl: FakeAttendanceRepository): AttendanceRepository

    @Binds
    abstract fun bindCheckInStrategy(impl: ButtonCheckInStrategy): CheckInStrategy
}