package com.badminton.booking.dto

import java.time.LocalDate
import java.time.LocalTime

class BookCourtResponse(
    val location: String,
    val court: String,
    val bookingDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: String

)