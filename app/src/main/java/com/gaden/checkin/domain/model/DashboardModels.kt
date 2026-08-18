package com.gaden.checkin.domain.model

enum class EmployeeRole { ADMIN, MANAGER, STAFF }

data class Employee(
    val id: String,
    val fullName: String,
    val department: String,
    val role: EmployeeRole,
    val todayStatus: AttendanceStatus,
    val todayCheckinTime: kotlin.time.Instant?,
)

data class DashboardSummary(
    val totalEmployees: Int,
    val checkedInToday: Int,
    val lateToday: Int,
    val onLeaveToday: Int,
)

interface DashboardRepository {
    suspend fun getSummary(): DashboardSummary
    suspend fun getEmployeesToday(): List<Employee>
}
