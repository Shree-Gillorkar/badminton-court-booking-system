package com.badminton.booking.controller

import com.badminton.booking.dto.BookingResponseDto
import com.badminton.booking.enum.BookingStatus
import com.badminton.booking.service.BookingService
import com.badminton.booking.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.time.LocalTime

@DisplayName("User Controller Tests")
class UserControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var bookingService: BookingService

    private lateinit var userController: UserController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userController = UserController(userService, bookingService)
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    // ===================== Get User Bookings Tests =====================

    @Test
    @DisplayName("Should fetch user bookings successfully")
    fun testGetUserBookingsSuccess() {
        // Arrange
        val userMobile = "9876543210"

        val booking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Sports Complex A",
            complexName = "Main Complex",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(listOf(booking))

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Bookings fetched successfully"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].bookingId").value(1))
            .andExpect(jsonPath("$.data[0].locationName").value("Sports Complex A"))
            .andExpect(jsonPath("$.data[0].courtName").value("Court-1"))
            .andExpect(jsonPath("$.data[0].status").value("BOOKED"))
            .andExpect(jsonPath("$.data[0].canCancel").value(true))
    }

    @Test
    @DisplayName("Should fetch multiple bookings for user")
    fun testGetMultipleUserBookings() {
        // Arrange
        val userMobile = "9876543210"

        val booking1 = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex A",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val booking2 = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex B",
            complexName = "Secondary",
            courtName = "Court-2",
            bookingDate = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val booking3 = BookingResponseDto(
            bookingId = 3L,
            locationName = "Complex A",
            complexName = "Main",
            courtName = "Court-3",
            bookingDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0),
            status = BookingStatus.CANCELLED,
            canCancel = false
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(listOf(booking1, booking2, booking3))

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].bookingId").value(1))
            .andExpect(jsonPath("$.data[1].bookingId").value(2))
            .andExpect(jsonPath("$.data[2].bookingId").value(3))
            .andExpect(jsonPath("$.data[0].status").value("BOOKED"))
            .andExpect(jsonPath("$.data[2].status").value("CANCELLED"))
    }

    @Test
    @DisplayName("Should fetch bookings with different statuses")
    fun testGetBookingsWithDifferentStatuses() {
        // Arrange
        val userMobile = "9876543210"

        val bookedBooking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val cancelledBooking = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-2",
            bookingDate = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = BookingStatus.CANCELLED,
            canCancel = false
        )

        val pastBooking = BookingResponseDto(
            bookingId = 3L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-3",
            bookingDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = BookingStatus.BOOKED,
            canCancel = false
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(
            listOf(bookedBooking, cancelledBooking, pastBooking)
        )

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].status").value("BOOKED"))
            .andExpect(jsonPath("$.data[1].status").value("CANCELLED"))
            .andExpect(jsonPath("$.data[2].status").value("BOOKED"))
    }

    @Test
    @DisplayName("Should fetch empty bookings list for user with no bookings")
    fun testGetUserBookingsEmpty() {
        // Arrange
        val userMobile = "1111111111"

        `when`(userService.getUserBookings(userMobile)).thenReturn(emptyList())

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Bookings fetched successfully"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(0))
    }

    @Test
    @DisplayName("Should fetch bookings with different cancellation eligibility")
    fun testGetBookingsWithDifferentCancellationEligibility() {
        // Arrange
        val userMobile = "9876543210"

        val cancellableBooking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(5),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val nonCancellableBooking = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-2",
            bookingDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = BookingStatus.BOOKED,
            canCancel = false
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(
            listOf(cancellableBooking, nonCancellableBooking)
        )

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].canCancel").value(true))
            .andExpect(jsonPath("$.data[1].canCancel").value(false))
    }

    @Test
    @DisplayName("Should fail fetch bookings without mobile header")
    fun testGetUserBookingsWithoutMobileHeader() {
        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fetch bookings for different user mobile numbers")
    fun testGetBookingsForDifferentUsers() {
        // User 1
        val user1Mobile = "9876543210"
        val user1Booking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(user1Mobile)).thenReturn(listOf(user1Booking))

        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", user1Mobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(1))

        // User 2
        val user2Mobile = "8765432109"
        val user2Booking = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-2",
            bookingDate = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(user2Mobile)).thenReturn(listOf(user2Booking))

        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", user2Mobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(1))
    }

    @Test
    @DisplayName("Should fetch bookings with complete response structure")
    fun testGetUserBookingsResponseStructure() {
        // Arrange
        val userMobile = "9876543210"

        val booking = BookingResponseDto(
            bookingId = 100L,
            locationName = "Test Complex",
            complexName = "Test Main",
            courtName = "Court-5",
            bookingDate = LocalDate.now().plusDays(3),
            startTime = LocalTime.of(15, 30),
            endTime = LocalTime.of(16, 30),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(listOf(booking))

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.data[0].bookingId").value(100))
            .andExpect(jsonPath("$.data[0].locationName").value("Test Complex"))
            .andExpect(jsonPath("$.data[0].complexName").value("Test Main"))
            .andExpect(jsonPath("$.data[0].courtName").value("Court-5"))
            .andExpect(jsonPath("$.data[0].bookingDate").value(LocalDate.now().plusDays(3).toString()))
            .andExpect(jsonPath("$.data[0].startTime").value("15:30:00"))
            .andExpect(jsonPath("$.data[0].endTime").value("16:30:00"))
    }

    @Test
    @DisplayName("Should fetch bookings with various location names")
    fun testGetBookingsWithDifferentLocations() {
        // Arrange
        val userMobile = "9876543210"

        val booking1 = BookingResponseDto(
            bookingId = 1L,
            locationName = "Downtown Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val booking2 = BookingResponseDto(
            bookingId = 2L,
            locationName = "Uptown Courts",
            complexName = "Secondary",
            courtName = "Court-2",
            bookingDate = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val booking3 = BookingResponseDto(
            bookingId = 3L,
            locationName = "Suburban Badminton Hub",
            complexName = "Tertiary",
            courtName = "Court-3",
            bookingDate = LocalDate.now().plusDays(3),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(listOf(booking1, booking2, booking3))

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].locationName").value("Downtown Complex"))
            .andExpect(jsonPath("$.data[1].locationName").value("Uptown Courts"))
            .andExpect(jsonPath("$.data[2].locationName").value("Suburban Badminton Hub"))
    }

    @Test
    @DisplayName("Should fetch bookings across different time slots")
    fun testGetBookingsAcrossDifferentTimeSlots() {
        // Arrange
        val userMobile = "9876543210"

        val earlyMorningBooking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(6, 0),
            endTime = LocalTime.of(7, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val morningBooking = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-2",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val afternoonBooking = BookingResponseDto(
            bookingId = 3L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-3",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val eveningBooking = BookingResponseDto(
            bookingId = 4L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-4",
            bookingDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(19, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(
            listOf(earlyMorningBooking, morningBooking, afternoonBooking, eveningBooking)
        )

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(4))
            .andExpect(jsonPath("$.data[0].startTime").value("06:00:00"))
            .andExpect(jsonPath("$.data[1].startTime").value("09:00:00"))
            .andExpect(jsonPath("$.data[2].startTime").value("14:00:00"))
            .andExpect(jsonPath("$.data[3].startTime").value("18:00:00"))
    }

    @Test
    @DisplayName("Should fetch bookings across different dates")
    fun testGetBookingsAcrossDifferentDates() {
        // Arrange
        val userMobile = "9876543210"
        val today = LocalDate.now()

        val tomorrowBooking = BookingResponseDto(
            bookingId = 1L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-1",
            bookingDate = today.plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val nextWeekBooking = BookingResponseDto(
            bookingId = 2L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-2",
            bookingDate = today.plusDays(7),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        val nextMonthBooking = BookingResponseDto(
            bookingId = 3L,
            locationName = "Complex",
            complexName = "Main",
            courtName = "Court-3",
            bookingDate = today.plusDays(30),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0),
            status = BookingStatus.BOOKED,
            canCancel = true
        )

        `when`(userService.getUserBookings(userMobile)).thenReturn(
            listOf(tomorrowBooking, nextWeekBooking, nextMonthBooking)
        )

        // Act & Assert
        mockMvc.perform(
            get("/api/user/bookings")
                .header("mobileNumber", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].bookingDate").value(today.plusDays(1).toString()))
            .andExpect(jsonPath("$.data[1].bookingDate").value(today.plusDays(7).toString()))
            .andExpect(jsonPath("$.data[2].bookingDate").value(today.plusDays(30).toString()))
    }
}

