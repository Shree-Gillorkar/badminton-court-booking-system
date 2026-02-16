package com.badminton.booking.service

import com.badminton.booking.dto.BookingResponseDto
import com.badminton.booking.entity.Booking
import com.badminton.booking.repository.BookingRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalTime


@Service
class UserService(
    private val bookingRepository: BookingRepository,
) {

    fun getUserBookings(userMobile: String): List<BookingResponseDto> {
        val bookings = bookingRepository.findByUser_MobileNumber(userMobile)
        if (bookings.isEmpty()) {
            return emptyList();
        }
        return bookings.map { booking ->
            BookingResponseDto(
                bookingId = booking.id,
                locationName = booking.court.location.name,
                complexName = booking.court.location.complexName,
                courtName = booking.court.name,
                bookingDate = booking.bookingDate,
                startTime = booking.timeSlot.startTime,
                endTime = booking.timeSlot.endTime,
                status = booking.status,
                canCancel = cancelBooking(booking)
            )
        }
    }

    fun cancelBooking(booking: Booking): Boolean {
        val now = LocalTime.now()
        if (booking.timeSlot.startTime.isBefore(now)) {
            return false;
        }
        val hoursDiff = Duration.between(now, booking.timeSlot.startTime).toHours()
        if (hoursDiff < 4) {
            return false;
        }
        return true

    }
}