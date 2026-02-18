package com.badminton.booking.service

import com.badminton.booking.entity.*
import com.badminton.booking.enum.BookingStatus
import com.badminton.booking.enum.UserRole
import com.badminton.booking.repository.BookingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private lateinit var bookingRepository: BookingRepository

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userService = UserService(bookingRepository)
    }


    @Test
    @DisplayName("Should get user bookings successfully")
    fun testGetUserBookingsSuccess() {
        val userMobile = "8888888888"
        val user = User(id = 2, mobileNumber = userMobile, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )
        whenever(bookingRepository.findByUser_MobileNumber(userMobile)).thenReturn(listOf(booking))
        val response = userService.getUserBookings(userMobile)
        assert(response.isNotEmpty())
        assert(response.size == 1)
        assert(response[0].bookingId == 1L)
        assert(response[0].locationName == "Mumbai")
        assert(response[0].courtName == "Court 1")
    }

    @Test
    @DisplayName("Should return empty list when user has no bookings")
    fun testGetUserBookingsEmpty() {
        val userMobile = "8888888888"
        whenever(bookingRepository.findByUser_MobileNumber(userMobile)).thenReturn(emptyList())
        val response = userService.getUserBookings(userMobile)
        assert(response.isEmpty())
    }

    @Test
    @DisplayName("Should get multiple bookings for user")
    fun testGetUserBookingsMultiple() {
        val userMobile = "8888888888"
        val user = User(id = 2, mobileNumber = userMobile, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court1 = Court(id = 1, name = "Court 1", location = location)
        val court2 = Court(id = 2, name = "Court 2", location = location)
        val timeSlot1 = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val timeSlot2 = TimeSlot(id = 2, startTime = LocalTime.of(7, 0), endTime = LocalTime.of(8, 0))

        val booking1 = Booking(
            id = 1,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court1,
            timeSlot = timeSlot1,
            status = BookingStatus.BOOKED
        )
        val booking2 = Booking(
            id = 2,
            bookingDate = LocalDate.now().plusDays(2),
            user = user,
            court = court2,
            timeSlot = timeSlot2,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findByUser_MobileNumber(userMobile)).thenReturn(listOf(booking1, booking2))

        val response = userService.getUserBookings(userMobile)

        assert(response.size == 2)
        assert(response[0].bookingId == 1L)
        assert(response[1].bookingId == 2L)
        assert(response[0].courtName == "Court 1")
        assert(response[1].courtName == "Court 2")
    }

    @Test
    @DisplayName("Should return booking with correct details")
    fun testGetUserBookingsCorrectDetails() {
        val userMobile = "8888888888"
        val user = User(id = 2, mobileNumber = userMobile, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Andheri Sports Club",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court A", location = location)
        val startTime = LocalTime.of(14, 30)
        val endTime = LocalTime.of(15, 30)
        val bookingDate = LocalDate.now().plusDays(3)
        val timeSlot = TimeSlot(id = 1, startTime = startTime, endTime = endTime)
        val booking = Booking(
            id = 42,
            bookingDate = bookingDate,
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findByUser_MobileNumber(userMobile)).thenReturn(listOf(booking))

        val response = userService.getUserBookings(userMobile)

        assert(response.size == 1)
        val bookingResponse = response[0]
        assert(bookingResponse.bookingId == 42L)
        assert(bookingResponse.locationName == "Mumbai")
        assert(bookingResponse.complexName == "Andheri Sports Club")
        assert(bookingResponse.courtName == "Court A")
        assert(bookingResponse.bookingDate == bookingDate)
        assert(bookingResponse.startTime == startTime)
        assert(bookingResponse.endTime == endTime)
        assert(bookingResponse.status == BookingStatus.BOOKED)
    }

    @Test
    @DisplayName("Should get bookings for different users independently")
    fun testGetUserBookingsDifferentUsers() {
        val userMobile1 = "8888888888"
        val userMobile2 = "7777777777"
        val user1 = User(id = 2, mobileNumber = userMobile1, password = "pass", role = UserRole.USER)
        val user2 = User(id = 3, mobileNumber = userMobile2, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))

        val booking1 = Booking(
            id = 1,
            bookingDate = LocalDate.now().plusDays(1),
            user = user1,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )
        val booking2 = Booking(
            id = 2,
            bookingDate = LocalDate.now().plusDays(1),
            user = user2,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findByUser_MobileNumber(userMobile1)).thenReturn(listOf(booking1))
        whenever(bookingRepository.findByUser_MobileNumber(userMobile2)).thenReturn(listOf(booking2))

        val response1 = userService.getUserBookings(userMobile1)
        val response2 = userService.getUserBookings(userMobile2)

        assert(response1.size == 1)
        assert(response2.size == 1)
        assert(response1[0].bookingId == 1L)
        assert(response2[0].bookingId == 2L)
    }


    @Test
    @DisplayName("Should return true for cancellable booking (future booking with 4+ hours)")
    fun testCancelBookingCancel() {
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = currentTime.plusHours(5).plusMinutes(1),
            endTime = currentTime.plusHours(6).plusMinutes(1)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(result)
    }

    @Test
    @DisplayName("Should return false for past booking")
    fun testCancelBookingPastBooking() {
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = LocalTime.now(),
            endTime = currentTime.plusHours(1)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(!result)
    }

    @Test
    @DisplayName("Should return false for booking within 4 hours")
    fun testCancelBookingWithin4Hours() {
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = currentTime.plusHours(2),
            endTime = currentTime.plusHours(3)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(!result)
    }

    @Test
    @DisplayName("Should return true for booking exactly 4 hours away")
    fun testCancelBookingExactly4Hours() {
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = currentTime.plusHours(4).plusMinutes(1),
            endTime = currentTime.plusHours(5).plusMinutes(1)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(result)
    }

    @Test
    @DisplayName("Should return true for far future booking")
    fun testCancelBookingFarFuture() {
        // Arrange
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = currentTime.plusHours(5).plusMinutes(1),
            endTime = currentTime.plusHours(6).plusMinutes(1)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(result)
    }

    @Test
    @DisplayName("Should return false for booking 3 hours 59 minutes away")
    fun testCancelBookingJustUnder4Hours() {
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val currentTime = LocalTime.now()
        val timeSlot = TimeSlot(
            id = 1,
            startTime = currentTime.plusHours(3).plusMinutes(59),
            endTime = currentTime.plusHours(4).plusMinutes(59)
        )
        val booking = Booking(
            id = 1,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        val result = userService.cancelBooking(booking)

        assert(!result)
    }
}

