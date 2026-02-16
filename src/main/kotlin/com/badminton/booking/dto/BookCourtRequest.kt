package com.badminton.booking.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class BookCourtRequest(
    @field:NotNull(message = "LocationId is required")
    val locationId: Long,
    @field:NotNull(message = "CourtId is required")
    val courtId: Long,
    @field:NotNull(message = "BookingDate is required")
    val bookingDate: LocalDate,
    @field:NotNull(message = "SlotId is required")
    val slotId: Long,
)