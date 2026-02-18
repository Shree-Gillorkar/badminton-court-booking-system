package com.badminton.booking.service

import com.badminton.booking.dto.RegisterLocationRequest
import com.badminton.booking.entity.*
import com.badminton.booking.enum.BookingStatus
import com.badminton.booking.enum.UserRole
import com.badminton.booking.exception.MobileNotRegisteredException
import com.badminton.booking.exception.UnauthorizedBookingAccessException
import com.badminton.booking.exception.ValidationException
import com.badminton.booking.repository.BookingRepository
import com.badminton.booking.repository.CourtRepository
import com.badminton.booking.repository.LocationRepository
import com.badminton.booking.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var locationRepository: LocationRepository

    @Mock
    private lateinit var courtRepository: CourtRepository

    @Mock
    private lateinit var bookingRepository: BookingRepository

    private lateinit var adminService: AdminService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        adminService = AdminService(userRepository, locationRepository, courtRepository, bookingRepository)
    }


    @Test
    @DisplayName("Should register location successfully with valid admin and court count")
    fun testRegisterLocationSuccess() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2
        )
        val savedLocation = Location(
            id = 1,
            name = request.locationName,
            complexName = request.complexName,
            imageUrl = request.imageUrl,
            userMobile = admin.mobileNumber,
            admin = admin
        )

        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(0)
        whenever(locationRepository.save(any<Location>())).thenReturn(savedLocation)

        val response = adminService.registerLocation(request)

        assert(response.success)
        assert(response.message.contains("successfully"))
        verify(userRepository).findByMobileNumber(request.adminMobile)
        verify(locationRepository).countByAdminId(admin.id)
        verify(locationRepository).save(any<Location>())
        verify(courtRepository, org.mockito.kotlin.times(2)).save(any<Court>())
    }

    @Test
    @DisplayName("Should throw exception when admin not found")
    fun testRegisterLocationAdminNotFound() {
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.empty())

        assertThrows<MobileNotRegisteredException> {
            adminService.registerLocation(request)
        }
        verify(userRepository).findByMobileNumber(request.adminMobile)
    }

    @Test
    @DisplayName("Should throw exception when non-admin user tries to register location")
    fun testRegisterLocationNonAdminUser() {
        val user = User(
            id = 2,
            mobileNumber = "8888888888",
            password = "userpass",
            role = UserRole.USER,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "8888888888",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(user))

        assertThrows<UnauthorizedBookingAccessException> {
            adminService.registerLocation(request)
        }
    }

    @Test
    @DisplayName("Should throw exception when admin exceeds maximum locations (3)")
    fun testRegisterLocationMaximumLocationsExceeded() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(3)

        assertThrows<ValidationException> {
            adminService.registerLocation(request)
        }
    }

    @Test
    @DisplayName("Should throw exception when court count is less than 1")
    fun testRegisterLocationInvalidCourtCountTooLow() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 0
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(0)

        assertThrows<ValidationException> {
            adminService.registerLocation(request)
        }
    }

    @Test
    @DisplayName("Should throw exception when court count exceeds maximum (4)")
    fun testRegisterLocationInvalidCourtCountTooHigh() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 5
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(0)

        assertThrows<ValidationException> {
            adminService.registerLocation(request)
        }
    }

    @Test
    @DisplayName("Should register location with minimum courts (1)")
    fun testRegisterLocationMinimumCourts() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 1
        )
        val savedLocation = Location(
            id = 1,
            name = request.locationName,
            complexName = request.complexName,
            imageUrl = request.imageUrl,
            userMobile = admin.mobileNumber,
            admin = admin
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(0)
        whenever(locationRepository.save(any<Location>())).thenReturn(savedLocation)

        val response = adminService.registerLocation(request)

        assert(response.success)
        verify(courtRepository, org.mockito.kotlin.times(1)).save(any<Court>())
    }

    @Test
    @DisplayName("Should register location with maximum courts (4)")
    fun testRegisterLocationMaximumCourts() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Mumbai Courts",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 4
        )
        val savedLocation = Location(
            id = 1,
            name = request.locationName,
            complexName = request.complexName,
            imageUrl = request.imageUrl,
            userMobile = admin.mobileNumber,
            admin = admin
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(0)
        whenever(locationRepository.save(any<Location>())).thenReturn(savedLocation)

        val response = adminService.registerLocation(request)

        assert(response.success)
        verify(courtRepository, org.mockito.kotlin.times(4)).save(any<Court>())
    }

    @Test
    @DisplayName("Should register location with 2 existing locations")
    fun testRegisterLocationWith2ExistingLocations() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = RegisterLocationRequest(
            adminMobile = "9999999999",
            locationName = "Third Location",
            complexName = "Sports Complex 3",
            imageUrl = "https://example.com/image3.jpg",
            numberOfCourts = 2
        )
        val savedLocation = Location(
            id = 3,
            name = request.locationName,
            complexName = request.complexName,
            imageUrl = request.imageUrl,
            userMobile = admin.mobileNumber,
            admin = admin
        )
        whenever(userRepository.findByMobileNumber(request.adminMobile)).thenReturn(Optional.of(admin))
        whenever(locationRepository.countByAdminId(admin.id)).thenReturn(2)
        whenever(locationRepository.save(any<Location>())).thenReturn(savedLocation)

        val response = adminService.registerLocation(request)

        assert(response.success)
        verify(locationRepository).countByAdminId(admin.id)
    }


    @Test
    @DisplayName("Should get admin dashboard successfully")
    fun testGetAdminDashboardSuccess() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location))
        whenever(bookingRepository.findByCourtId(any())).thenReturn(emptyList())

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        assert(response.message.contains("fetched"))
        verify(userRepository).findByMobileNumber(admin.mobileNumber)
        verify(locationRepository).findByAdminId(admin.id)
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to get dashboard")
    fun testGetAdminDashboardNonAdminUser() {
        val user = User(
            id = 2,
            mobileNumber = "8888888888",
            password = "userpass",
            role = UserRole.USER,
            active = true
        )
        whenever(userRepository.findByMobileNumber(user.mobileNumber)).thenReturn(Optional.of(user))

        assertThrows<UnauthorizedBookingAccessException> {
            adminService.getAdminDashboard(user.mobileNumber)
        }
    }

    @Test
    @DisplayName("Should return empty dashboard for admin with no locations")
    fun testGetAdminDashboardNoLocations() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(emptyList())

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        assert(response.data?.locations?.isEmpty() == true)
    }

    @Test
    @DisplayName("Should return dashboard with single location and single court without bookings")
    fun testGetAdminDashboardSingleLocationSingleCourt() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        location.courts.add(court)

        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location))
        whenever(bookingRepository.findByCourtId(court.id)).thenReturn(emptyList())

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        assert(response.data?.locations?.size == 1)
        assert(response.data?.locations?.get(0)?.locationName == "Mumbai")
        assert(response.data?.locations?.get(0)?.courts?.size == 1)
        assert(response.data?.locations?.get(0)?.courts?.get(0)?.courtName == "Court 1")
        assert(response.data?.locations?.get(0)?.courts?.get(0)?.bookings?.isEmpty() == true)
    }

    @Test
    @DisplayName("Should return dashboard with location, court and bookings")
    fun testGetAdminDashboardWithBookings() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val user = User(
            id = 2,
            mobileNumber = "8888888888",
            password = "userpass",
            role = UserRole.USER,
            active = true
        )
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        location.courts.add(court)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location))
        whenever(bookingRepository.findByCourtId(court.id)).thenReturn(listOf(booking))

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        assert(response.data?.locations?.size == 1)
        val locationDto = response.data?.locations?.get(0)
        assert(locationDto?.locationName == "Mumbai")
        assert(locationDto?.courts?.size == 1)
        assert(locationDto?.courts?.get(0)?.bookings?.size == 1)
        val bookingDto = locationDto?.courts?.get(0)?.bookings?.get(0)
        assert(bookingDto?.bookingId == 1L)
        assert(bookingDto?.userMobile == "8888888888")
        assert(bookingDto?.status == "BOOKED")
    }

    @Test
    @DisplayName("Should return dashboard with multiple locations and courts")
    fun testGetAdminDashboardMultipleLocationsAndCourts() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )

        val location1 = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex 1",
            imageUrl = "url1",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court1 = Court(id = 1, name = "Court 1", location = location1)
        val court2 = Court(id = 2, name = "Court 2", location = location1)
        location1.courts.addAll(listOf(court1, court2))

        val location2 = Location(
            id = 2,
            name = "Bangalore",
            complexName = "Complex 2",
            imageUrl = "url2",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court3 = Court(id = 3, name = "Court 1", location = location2)
        location2.courts.add(court3)

        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location1, location2))
        whenever(bookingRepository.findByCourtId(1L)).thenReturn(emptyList())
        whenever(bookingRepository.findByCourtId(2L)).thenReturn(emptyList())
        whenever(bookingRepository.findByCourtId(3L)).thenReturn(emptyList())

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        assert(response.data?.locations?.size == 2)

        val location1Dto = response.data?.locations?.get(0)
        assert(location1Dto?.locationName == "Mumbai")
        assert(location1Dto?.courts?.size == 2)
        assert(location1Dto?.courts?.get(0)?.courtName == "Court 1")
        assert(location1Dto?.courts?.get(1)?.courtName == "Court 2")

        val location2Dto = response.data?.locations?.get(1)
        assert(location2Dto?.locationName == "Bangalore")
        assert(location2Dto?.courts?.size == 1)
        assert(location2Dto?.courts?.get(0)?.courtName == "Court 1")
    }

    @Test
    @DisplayName("Should return dashboard with multiple bookings per court")
    fun testGetAdminDashboardMultipleBookingsPerCourt() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val user1 = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER, active = true)
        val user2 = User(id = 3, mobileNumber = "7777777777", password = "pass", role = UserRole.USER, active = true)

        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        location.courts.add(court)

        val timeSlot1 = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val timeSlot2 = TimeSlot(id = 2, startTime = LocalTime.of(7, 0), endTime = LocalTime.of(8, 0))

        val booking1 = Booking(
            id = 1,
            bookingDate = LocalDate.now().plusDays(1),
            user = user1,
            court = court,
            timeSlot = timeSlot1,
            status = BookingStatus.BOOKED
        )
        val booking2 = Booking(
            id = 2,
            bookingDate = LocalDate.now().plusDays(2),
            user = user2,
            court = court,
            timeSlot = timeSlot2,
            status = BookingStatus.BOOKED
        )

        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location))
        whenever(bookingRepository.findByCourtId(court.id)).thenReturn(listOf(booking1, booking2))

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        val courtDto = response.data?.locations?.get(0)?.courts?.get(0)
        assert(courtDto?.bookings?.size == 2)
        assert(courtDto?.bookings?.get(0)?.userMobile == "8888888888")
        assert(courtDto?.bookings?.get(1)?.userMobile == "7777777777")
    }

    @Test
    @DisplayName("Should map booking details correctly in dashboard")
    fun testGetAdminDashboardBookingDetailsMapping() {
        val admin = User(
            id = 1,
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val user = User(
            id = 2,
            mobileNumber = "8888888888",
            password = "userpass",
            role = UserRole.USER,
            active = true
        )
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court A", location = location)
        location.courts.add(court)

        val startTime = LocalTime.of(14, 30)
        val endTime = LocalTime.of(15, 30)
        val bookingDate = LocalDate.now().plusDays(5)

        val timeSlot = TimeSlot(id = 1, startTime = startTime, endTime = endTime)
        val booking = Booking(
            id = 42,
            bookingDate = bookingDate,
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.CANCELLED
        )

        whenever(userRepository.findByMobileNumber(admin.mobileNumber)).thenReturn(Optional.of(admin))
        whenever(locationRepository.findByAdminId(admin.id)).thenReturn(listOf(location))
        whenever(bookingRepository.findByCourtId(court.id)).thenReturn(listOf(booking))

        val response = adminService.getAdminDashboard(admin.mobileNumber)

        assert(response.success)
        val bookingDto = response.data?.locations?.get(0)?.courts?.get(0)?.bookings?.get(0)

        assert(bookingDto?.bookingId == 42L)
        assert(bookingDto?.bookingDate == bookingDate)
        assert(bookingDto?.startTime == startTime)
        assert(bookingDto?.endTime == endTime)
        assert(bookingDto?.status == "CANCELLED")
        assert(bookingDto?.userMobile == "8888888888")
    }
}

