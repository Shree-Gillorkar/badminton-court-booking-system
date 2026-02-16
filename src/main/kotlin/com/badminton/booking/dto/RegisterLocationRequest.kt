package com.badminton.booking.dto

import jakarta.validation.constraints.NotNull

data class RegisterLocationRequest(
    @field:NotNull(message = "AdminMobile is required")
    val adminMobile: String,
    @field:NotNull(message = "LocationName is required")
    val locationName: String,
    @field:NotNull(message = "ImageUrl is required")
    val imageUrl: String,
    @field:NotNull(message = "NumberOfCourts is required")
    val numberOfCourts: Int,
    @field:NotNull(message = "ComplexName is required")
    val complexName: String
)