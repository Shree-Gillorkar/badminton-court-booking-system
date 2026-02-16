package com.badminton.booking.controller

import com.badminton.booking.dto.ApiResponse
import com.badminton.booking.dto.AvailabilityResponse
import com.badminton.booking.dto.BookCourtRequest
import com.badminton.booking.dto.BookCourtResponse
import com.badminton.booking.service.BookingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "Booking API", description = "Court booking operations")
@RestController
@RequestMapping("/api/bookings")
class BookingController(
    private val bookingService: BookingService
) {

    @Operation(summary = "Get Booking Information")
    @GetMapping("/availability")
    fun getAvailability(
        @RequestParam(
            "date",
            required = true
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ApiResponse<AvailabilityResponse> {
        return ApiResponse(
            success = true,
            message = "Availability fetched",
            data = bookingService.getFullAvailability(date)
        )
    }

    @Operation(summary = "Book a court slot")
    @PostMapping
    fun bookCourt(
        @RequestHeader("mobileNumber", required = true) mobileNumber: String,
        @RequestBody request: BookCourtRequest
    ): ApiResponse<BookCourtResponse> {
        val booking = bookingService.bookCourt(mobileNumber, request);
        return ApiResponse(
            success = true,
            message = "Booking confirmed successfully",
            data = booking
        )
    }

    @Operation(summary = "Cancel Booking")
    @PostMapping("/{bookingId}/cancel")
    fun cancelBooking(
        @RequestHeader("userId", required = true) userMobile: String,
        @PathVariable("bookingId", required = true) bookingId: Long
    ): ApiResponse<String> {
        return bookingService.cancelBooking(bookingId, userMobile)
    }
}