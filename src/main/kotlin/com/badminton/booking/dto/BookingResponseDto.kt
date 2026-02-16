package com.badminton.booking.dto

import com.badminton.booking.enum.BookingStatus
import java.time.LocalDate
import java.time.LocalTime

data class BookingResponseDto(
    val bookingId: Long,
    val locationName: String,
    val complexName: String,
    val courtName: String,
    val bookingDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: BookingStatus,
    val canCancel: Boolean,
)