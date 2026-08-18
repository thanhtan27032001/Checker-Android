package com.gaden.checkin.di

import com.gaden.checkin.data.ButtonCheckInStrategy
import com.gaden.checkin.domain.model.AttendanceRepository
import com.gaden.checkin.domain.repository.FakeAttendanceRepository
import com.gaden.checkin.domain.model.CheckInStrategy
import com.gaden.checkin.domain.repository.FakeLeaveRepository
import com.gaden.checkin.domain.model.LeaveRepository
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

    @Binds
    abstract fun bindLeaveRepository(impl: FakeLeaveRepository): LeaveRepository
}