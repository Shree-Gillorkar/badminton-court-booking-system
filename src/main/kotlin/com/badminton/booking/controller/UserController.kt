package com.badminton.booking.controller

import com.badminton.booking.dto.ApiResponse
import com.badminton.booking.dto.BookingResponseDto
import com.badminton.booking.service.BookingService
import com.badminton.booking.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User/Admin API", description = "User/Admin operations")
@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val bookingService: BookingService,
) {

    @Operation(summary = "Get a list of user Booking")
    @GetMapping("/bookings")
    fun getUserBookings(
        @RequestHeader(
            "mobileNumber", required = true
        ) userMobile: String
    ): ApiResponse<List<BookingResponseDto>> {
        val bookings = userService.getUserBookings(userMobile)
        return ApiResponse(
            success = true, message = "Bookings fetched successfully", data = bookings
        )
    }
}