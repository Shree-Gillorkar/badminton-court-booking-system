package com.badminton.booking.dto

import com.badminton.booking.enum.UserRole
import jakarta.validation.constraints.NotNull

data class RegisterRequest(
    @field:NotNull(message = "MobileNumber is required")
    val mobileNumber: String,
    @field:NotNull(message = "Password is required")
    val password: String,
    @field:NotNull(message = "UserRole is required")
    val role: UserRole
)