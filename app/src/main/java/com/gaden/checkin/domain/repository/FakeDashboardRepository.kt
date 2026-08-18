package com.gaden.checkin.domain.repository

import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.DashboardRepository
import com.gaden.checkin.domain.model.DashboardSummary
import com.gaden.checkin.domain.model.Employee
import com.gaden.checkin.domain.model.EmployeeRole
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class FakeDashboardRepository @Inject constructor() : DashboardRepository {

    private val fakeEmployees = listOf(
        Employee("emp-1", "Nguyễn Văn A", "Kỹ thuật", EmployeeRole.STAFF, AttendanceStatus.CHECKED_IN, Clock.System.now()),
        Employee("emp-2", "Trần Thị B", "Kỹ thuật", EmployeeRole.STAFF, AttendanceStatus.CHECKED_IN, Clock.System.now()),
        Employee("emp-3", "Lê Văn C", "Kinh doanh", EmployeeRole.MANAGER, AttendanceStatus.CHECKED_IN, Clock.System.now()),
        Employee("emp-4", "Phạm Thị D", "Kinh doanh", EmployeeRole.STAFF, AttendanceStatus.NOT_CHECKED_IN, null),
        Employee("emp-5", "Hoàng Văn E", "Nhân sự", EmployeeRole.STAFF, AttendanceStatus.NOT_CHECKED_IN, null),
        Employee("emp-6", "Vũ Thị F", "Kỹ thuật", EmployeeRole.STAFF, AttendanceStatus.CHECKED_OUT, Clock.System.now()),
    )

    override suspend fun getSummary(): DashboardSummary {
        delay(400)
        return DashboardSummary(
            totalEmployees = fakeEmployees.size,
            checkedInToday = fakeEmployees.count {
                it.todayStatus == AttendanceStatus.CHECKED_IN || it.todayStatus == AttendanceStatus.CHECKED_OUT
            },
            lateToday = 1,
            onLeaveToday = 0,
        )
    }

    override suspend fun getEmployeesToday(): List<Employee> {
        delay(500)
        return fakeEmployees
    }
}