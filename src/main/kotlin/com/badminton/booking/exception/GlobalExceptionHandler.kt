package com.badminton.booking.exception

import com.badminton.booking.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MobileNotRegisteredException::class)
    fun handleMobileNotRegistered(ex: MobileNotRegisteredException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Mobile not registered!"
            )
        )
    }

    @ExceptionHandler(BookingCancellationNotAllowedException::class)
    fun handleBookingCancellationNotAllowedException(ex: BookingCancellationNotAllowedException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Booking Cancellation Not Allowed!"
            )
        )
    }

    @ExceptionHandler(BookingNotFoundException::class)
    fun handleBookingNotFoundException(ex: BookingNotFoundException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Booking Not Found!"
            )
        )
    }


    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(ex: InvalidCredentialsException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Invalid Credentials!"
            )
        )
    }
    @ExceptionHandler(LocationNotFoundException::class)
    fun handleLocationNotFoundException(ex: LocationNotFoundException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Location Not Found!"
            )
        )
    }
    @ExceptionHandler(SlotAlreadyBookedException::class)
    fun handleSlotAlreadyBookedException(ex: SlotAlreadyBookedException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Slot Already Booked!"
            )
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Validation Failed!"
            )
        )
    }

    @ExceptionHandler(UnauthorizedBookingAccessException::class)
    fun handleUnauthorizedBookingAccessException(ex: UnauthorizedBookingAccessException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Unauthorized Booking Access!"
            )
        )
    }

    @ExceptionHandler(UserAlreadyExistException::class)
    fun handleUserAlreadyExistException(ex: UserAlreadyExistException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                message = ex.message ?: "User Already Exist!"
            )
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntime(ex: RuntimeException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.badRequest().body(
            ApiResponse(
                success = false,
                message = ex.message ?: "Bad request"
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse(
                success = false,
                message = "Unexpected error occurred"
            )
        )
    }
}