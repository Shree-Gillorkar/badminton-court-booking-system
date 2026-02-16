package com.badminton.booking.dto

import java.time.LocalDate
import java.time.LocalTime

data class AvailabilityResponse(
    val date: LocalDate,
    val locations: List<LocationAvailability>
) {
    data class LocationAvailability(
        val locationId: Long,
        val locationName: String,
        val courts: List<CourtAvailability>
    )

    data class CourtAvailability(
        val courtId: Long,
        val courtName: String,
        val slots: List<SlotAvailability>
    )

    data class SlotAvailability(
        val slotId: Long,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val status: String
    )
}