package com.badminton.booking.dto

import java.time.LocalDate
import java.time.LocalTime

data class AdminDashboardResponse(
    val locations: List<LocationDashboardDTO>
)

data class LocationDashboardDTO(
    val locationId: Long,
    val locationName: String,
    val imageUrl: String?,
    val courts: List<CourtDashboardDTO>
)

data class CourtDashboardDTO(
    val courtId: Long,
    val courtName: String,
    val bookings: List<BookingDTO>
)

data class BookingDTO(
    val bookingId: Long,
    val bookingDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: String,
    val userMobile: String
)