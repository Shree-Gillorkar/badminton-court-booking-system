package com.badminton.booking.dto

import jakarta.validation.constraints.NotNull


data class LoginRequest(
    @field:NotNull(message = "MobileNumber is required")
    val mobileNumber: String,
    @field:NotNull(message = "Password is required")
    val password: String
)