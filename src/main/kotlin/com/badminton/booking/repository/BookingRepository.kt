package com.badminton.booking.repository

import com.badminton.booking.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalTime

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUser_MobileNumber(userMobile: String): List<Booking>
    fun findByBookingDate(date: LocalDate): List<Booking>
    fun findByCourtId(courtId: Long): List<Booking>
    fun existsByCourt_IdAndCourt_Location_IdAndAndBookingDateAndTimeSlot_StartTimeLessThanAndTimeSlot_EndTimeGreaterThan(
        locationId: Long,
        courtId: Long,
        bookingDate: LocalDate,
        endTime: LocalTime,
        startTime: LocalTime
    ): Boolean
}
