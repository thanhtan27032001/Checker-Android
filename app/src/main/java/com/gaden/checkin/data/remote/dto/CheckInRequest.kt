package com.gaden.checkin.data.remote.dto

import com.gaden.checkin.domain.model.CheckInMethod
import kotlinx.serialization.Serializable

@Serializable
data class CheckInRequest(
    val employeeId: String,
    val method: String,
    val metadata: String?
)
