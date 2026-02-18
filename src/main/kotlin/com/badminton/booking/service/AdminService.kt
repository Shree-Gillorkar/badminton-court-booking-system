package com.badminton.booking.service

import com.badminton.booking.dto.*
import com.badminton.booking.entity.Court
import com.badminton.booking.entity.Location
import com.badminton.booking.enum.UserRole
import com.badminton.booking.exception.MobileNotRegisteredException
import com.badminton.booking.exception.UnauthorizedBookingAccessException
import com.badminton.booking.exception.ValidationException
import com.badminton.booking.repository.BookingRepository
import com.badminton.booking.repository.CourtRepository
import com.badminton.booking.repository.LocationRepository
import com.badminton.booking.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val courtRepository: CourtRepository,
    private val bookingRepository: BookingRepository
) {

    @Transactional
    fun registerLocation(req: RegisterLocationRequest): ApiResponse<String> {

        val admin = userRepository.findByMobileNumber(req.adminMobile)
            .orElseThrow { MobileNotRegisteredException("User not found, Please Register!") }

        if (admin.role != UserRole.ADMIN) {
            throw UnauthorizedBookingAccessException("Only admin can register courts")
        }

        val locationCount = locationRepository.countByAdminId(admin.id)
        if (locationCount >= 3) {
            throw ValidationException("Maximum 3 locations allowed per admin")
        }

        if (req.numberOfCourts !in 1..4) {
            throw ValidationException("Courts per location must be between 1 and 4")
        }

        val location = locationRepository.save(
            Location(
                name = req.locationName,
                imageUrl = req.imageUrl,
                admin = admin,
                userMobile = admin.mobileNumber,
                complexName = req.complexName
            )
        )

        repeat(req.numberOfCourts) { index ->
            courtRepository.save(
                Court(name = "Court-${index + 1}", location = location)
            )
        }

        return ApiResponse(
            success = true,
            message = "Location and courts registered successfully"
        )
    }

    fun getAdminDashboard(mobile: String): ApiResponse<AdminDashboardResponse> {
        val admin = userRepository.findByMobileNumber(mobile)
            .orElseThrow { RuntimeException("Admin not found") }
        if (admin.role != UserRole.ADMIN) {
            throw UnauthorizedBookingAccessException("Only admin allowed")
        }
        val locations = locationRepository.findByAdminId(admin.id)
        val locationDtos = locations.map { location ->
            val courtDtos = location.courts.map { court ->
                val bookings = bookingRepository.findByCourtId(court.id)
                    .map {
                        BookingDTO(
                            bookingId = it.id,
                            bookingDate = it.bookingDate,
                            startTime = it.timeSlot.startTime,
                            endTime = it.timeSlot.endTime,
                            status = it.status.name,
                            userMobile = it.user.mobileNumber
                        )
                    }

                CourtDashboardDTO(
                    courtId = court.id,
                    courtName = court.name,
                    bookings = bookings
                )
            }
            LocationDashboardDTO(
                locationId = location.id,
                locationName = location.name,
                imageUrl = location.imageUrl,
                courts = courtDtos
            )
        }

        return ApiResponse(
            success = true,
            message = "Admin dashboard fetched",
            data = AdminDashboardResponse(locationDtos)
        )
    }
}