package com.badminton.booking.service

import com.badminton.booking.dto.ApiResponse
import com.badminton.booking.dto.AvailabilityResponse
import com.badminton.booking.dto.BookCourtRequest
import com.badminton.booking.dto.BookCourtResponse
import com.badminton.booking.entity.Booking
import com.badminton.booking.enum.BookingStatus
import com.badminton.booking.exception.BookingCancellationNotAllowedException
import com.badminton.booking.exception.BookingNotFoundException
import com.badminton.booking.exception.MobileNotRegisteredException
import com.badminton.booking.exception.SlotAlreadyBookedException
import com.badminton.booking.exception.LocationNotFoundException
import com.badminton.booking.repository.*
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository,
    private val timeSlotRepository: TimeSlotRepository,
    private val locationRepository: LocationRepository,
) {

    fun getFullAvailability(date: LocalDate): AvailabilityResponse {

        val locations = locationRepository.findAll()
        val slots = timeSlotRepository.findAll()
        val bookings = bookingRepository.findByBookingDate(date)
        val bookedPairs = bookings.map { it.court.id to it.timeSlot.id }.toSet()
        val locationDtos = locations.map { location ->
            val courtDtos = location.courts.map { court ->
                val slotDtos = slots.map { slot ->
                    val booked = bookedPairs.contains(
                        court.id to slot.id
                    )
                    AvailabilityResponse.SlotAvailability(
                        slotId = slot.id,
                        startTime = slot.startTime,
                        endTime = slot.endTime,
                        status = if (!booked) "AVAILABLE" else "BOOKED"
                    )
                }
                AvailabilityResponse.CourtAvailability(
                    courtId = court.id,
                    courtName = court.name,
                    slots = slotDtos
                )
            }
            AvailabilityResponse.LocationAvailability(
                locationId = location.id,
                locationName = location.name,
                courts = courtDtos
            )
        }

        return AvailabilityResponse(
            date = date,
            locations = locationDtos
        )
    }


    fun bookCourt(mobileNumber: String, request: BookCourtRequest): BookCourtResponse {

        val user = userRepository.findByMobileNumber(mobileNumber)
            .orElseThrow { MobileNotRegisteredException("Please register first!") }
        val location =
            locationRepository.findById(request.locationId).orElseThrow { LocationNotFoundException ("Location not found!") }
        val court = courtRepository.findById(request.courtId).orElseThrow { RuntimeException("Court not found") }
        val slot = timeSlotRepository.findById(request.slotId).orElseThrow { RuntimeException("Slot not found") }
        val endTime = slot.startTime.plusHours(1)
        val isOverlapping = bookingRepository
            .existsByCourt_IdAndCourt_Location_IdAndAndBookingDateAndTimeSlot_StartTimeLessThanAndTimeSlot_EndTimeGreaterThan(
                locationId = location.id,
                court.id,
                request.bookingDate,
                endTime,
                slot.startTime
            )

        if (isOverlapping) {
            throw SlotAlreadyBookedException("Selected slot is already booked")
        }

        val booking = Booking(
            user = user,
            court = court,
            bookingDate = request.bookingDate,
            timeSlot = slot
        )

        bookingRepository.save(booking)

        return BookCourtResponse(
            location = booking.court.location.name,
            court = booking.court.name,
            bookingDate = request.bookingDate,
            startTime = slot.startTime,
            endTime = slot.endTime,
            status = booking.status.name
        );
    }


    fun cancelBooking(bookingId: Long, mobileNumber: String): ApiResponse<String> {

        val booking =
            bookingRepository.findById(bookingId).orElseThrow { BookingNotFoundException("Booking not found!") }
        if (booking.user.mobileNumber != mobileNumber) {
            throw BookingCancellationNotAllowedException("You cannot cancel this booking!")
        }
        if (booking.status == BookingStatus.CANCELLED) {
            throw BookingCancellationNotAllowedException("Booking already cancelled")
        }
        val bookingStartDateTime = LocalDateTime.of(
            booking.bookingDate,
            booking.timeSlot.startTime
        )
        val now = LocalDateTime.now()
        if (bookingStartDateTime.isBefore(now)) {
            throw BookingCancellationNotAllowedException("Past booking cannot be cancelled")
        }
        val hoursDiff = Duration.between(now, bookingStartDateTime).toHours()

        if (hoursDiff < 4) {
            throw BookingCancellationNotAllowedException("Booking can only be cancelled 4 hours before start time")
        }
        booking.status = BookingStatus.CANCELLED
        bookingRepository.save(booking)

        return ApiResponse(
            success = true,
            message = "Booking cancelled successfully"
        )
    }
}