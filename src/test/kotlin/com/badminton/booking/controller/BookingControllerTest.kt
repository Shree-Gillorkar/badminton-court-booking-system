package com.badminton.booking.controller

import com.badminton.booking.dto.*
import com.badminton.booking.service.BookingService
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

@DisplayName("Booking Controller Tests")
class BookingControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var bookingService: BookingService

    private lateinit var bookingController: BookingController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        bookingController = BookingController(bookingService)
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build()
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    // ===================== Get Availability Tests =====================

    @Test
    @DisplayName("Should fetch availability for a valid date")
    fun testGetAvailabilitySuccess() {
        // Arrange
        val date = LocalDate.now().plusDays(1)

        val slotAvailability = AvailabilityResponse.SlotAvailability(
            slotId = 1L,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = "AVAILABLE"
        )

        val courtAvailability = AvailabilityResponse.CourtAvailability(
            courtId = 1L,
            courtName = "Court-1",
            slots = listOf(slotAvailability)
        )

        val locationAvailability = AvailabilityResponse.LocationAvailability(
            locationId = 1L,
            locationName = "Sports Complex A",
            courts = listOf(courtAvailability)
        )

        val availabilityResponse = AvailabilityResponse(
            date = date,
            locations = listOf(locationAvailability)
        )

        `when`(bookingService.getFullAvailability(date)).thenReturn(availabilityResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/bookings/availability")
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Availability fetched"))
            .andExpect(jsonPath("$.data.date").value(date.toString()))
            .andExpect(jsonPath("$.data.locations").isArray)
            .andExpect(jsonPath("$.data.locations[0].locationName").value("Sports Complex A"))
    }

    @Test
    @DisplayName("Should fetch availability with multiple locations")
    fun testGetAvailabilityWithMultipleLocations() {
        // Arrange
        val date = LocalDate.now().plusDays(2)

        val slot1 = AvailabilityResponse.SlotAvailability(1L, LocalTime.of(9, 0), LocalTime.of(10, 0), "AVAILABLE")
        val slot2 = AvailabilityResponse.SlotAvailability(2L, LocalTime.of(10, 0), LocalTime.of(11, 0), "BOOKED")

        val court1 = AvailabilityResponse.CourtAvailability(1L, "Court-1", listOf(slot1))
        val court2 = AvailabilityResponse.CourtAvailability(2L, "Court-2", listOf(slot2))

        val location1 = AvailabilityResponse.LocationAvailability(1L, "Location A", listOf(court1))
        val location2 = AvailabilityResponse.LocationAvailability(2L, "Location B", listOf(court2))

        val availabilityResponse = AvailabilityResponse(date, listOf(location1, location2))

        `when`(bookingService.getFullAvailability(date)).thenReturn(availabilityResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/bookings/availability")
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.locations.length()").value(2))
            .andExpect(jsonPath("$.data.locations[0].locationName").value("Location A"))
            .andExpect(jsonPath("$.data.locations[1].locationName").value("Location B"))
    }

    @Test
    @DisplayName("Should fetch availability with multiple courts per location")
    fun testGetAvailabilityWithMultipleCourts() {
        // Arrange
        val date = LocalDate.now()

        val slots = listOf(
            AvailabilityResponse.SlotAvailability(1L, LocalTime.of(9, 0), LocalTime.of(10, 0), "AVAILABLE"),
            AvailabilityResponse.SlotAvailability(2L, LocalTime.of(10, 0), LocalTime.of(11, 0), "AVAILABLE"),
            AvailabilityResponse.SlotAvailability(3L, LocalTime.of(11, 0), LocalTime.of(12, 0), "BOOKED")
        )

        val court1 = AvailabilityResponse.CourtAvailability(1L, "Court-1", slots)
        val court2 = AvailabilityResponse.CourtAvailability(2L, "Court-2", slots)
        val court3 = AvailabilityResponse.CourtAvailability(3L, "Court-3", slots)

        val location = AvailabilityResponse.LocationAvailability(1L, "Complex", listOf(court1, court2, court3))
        val availabilityResponse = AvailabilityResponse(date, listOf(location))

        `when`(bookingService.getFullAvailability(date)).thenReturn(availabilityResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/bookings/availability")
                .param("date", date.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.locations[0].courts.length()").value(3))
    }

    @Test
    @DisplayName("Should handle availability with various slot statuses")
    fun testGetAvailabilityWithDifferentSlotStatuses() {
        // Arrange
        val date = LocalDate.now()

        val availableSlot = AvailabilityResponse.SlotAvailability(1L, LocalTime.of(9, 0), LocalTime.of(10, 0), "AVAILABLE")
        val bookedSlot = AvailabilityResponse.SlotAvailability(2L, LocalTime.of(10, 0), LocalTime.of(11, 0), "BOOKED")
        val maintenanceSlot = AvailabilityResponse.SlotAvailability(3L, LocalTime.of(14, 0), LocalTime.of(15, 0), "MAINTENANCE")

        val court = AvailabilityResponse.CourtAvailability(1L, "Court-1", listOf(availableSlot, bookedSlot, maintenanceSlot))
        val location = AvailabilityResponse.LocationAvailability(1L, "Complex", listOf(court))
        val availabilityResponse = AvailabilityResponse(date, listOf(location))

        `when`(bookingService.getFullAvailability(date)).thenReturn(availabilityResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/bookings/availability")
                .param("date", date.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.locations[0].courts[0].slots.length()").value(3))
            .andExpect(jsonPath("$.data.locations[0].courts[0].slots[0].status").value("AVAILABLE"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].slots[1].status").value("BOOKED"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].slots[2].status").value("MAINTENANCE"))
    }

    @Test
    @DisplayName("Should fail availability fetch with missing date parameter")
    fun testGetAvailabilityWithMissingDateParam() {
        // Act & Assert
        mockMvc.perform(
            get("/api/bookings/availability")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should handle availability for different dates")
    fun testGetAvailabilityForDifferentDates() {
        // Test today
        val today = LocalDate.now()
        val slot = AvailabilityResponse.SlotAvailability(1L, LocalTime.of(9, 0), LocalTime.of(10, 0), "AVAILABLE")
        val court = AvailabilityResponse.CourtAvailability(1L, "Court-1", listOf(slot))
        val location = AvailabilityResponse.LocationAvailability(1L, "Complex", listOf(court))

        `when`(bookingService.getFullAvailability(today)).thenReturn(AvailabilityResponse(today, listOf(location)))

        mockMvc.perform(get("/api/bookings/availability").param("date", today.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.date").value(today.toString()))

        // Test future date
        val futureDate = today.plusDays(7)
        `when`(bookingService.getFullAvailability(futureDate)).thenReturn(AvailabilityResponse(futureDate, listOf(location)))

        mockMvc.perform(get("/api/bookings/availability").param("date", futureDate.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.date").value(futureDate.toString()))
    }

    // ===================== Book Court Tests =====================

    @Test
    @DisplayName("Should book court successfully with valid request")
    fun testBookCourtSuccess() {
        // Arrange
        val mobileNumber = "9876543210"
        val request = BookCourtRequest(
            locationId = 1L,
            courtId = 1L,
            bookingDate = LocalDate.now().plusDays(1),
            slotId = 1L
        )

        val response = BookCourtResponse(
            location = "Sports Complex A",
            court = "Court-1",
            bookingDate = request.bookingDate,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = "CONFIRMED"
        )

        `when`(bookingService.bookCourt(mobileNumber, request)).thenReturn(response)

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Booking confirmed successfully"))
            .andExpect(jsonPath("$.data.location").value("Sports Complex A"))
            .andExpect(jsonPath("$.data.court").value("Court-1"))
            .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
    }

    @Test
    @DisplayName("Should book court at different time slots")
    fun testBookCourtAtDifferentTimeSlots() {
        // Arrange
        val mobileNumber = "9876543210"
        val date = LocalDate.now().plusDays(1)

        // Morning slot
        val morningRequest = BookCourtRequest(1L, 1L, date, 1L)
        val morningResponse = BookCourtResponse(
            "Complex", "Court-1", date, LocalTime.of(9, 0), LocalTime.of(10, 0), "CONFIRMED"
        )
        `when`(bookingService.bookCourt(mobileNumber, morningRequest)).thenReturn(morningResponse)

        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(morningRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.startTime").value("09:00:00"))

        // Afternoon slot
        val afternoonRequest = BookCourtRequest(1L, 1L, date, 2L)
        val afternoonResponse = BookCourtResponse(
            "Complex", "Court-1", date, LocalTime.of(14, 0), LocalTime.of(15, 0), "CONFIRMED"
        )
        `when`(bookingService.bookCourt(mobileNumber, afternoonRequest)).thenReturn(afternoonResponse)

        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(afternoonRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.startTime").value("14:00:00"))
    }

    @Test
    @DisplayName("Should book court at different locations")
    fun testBookCourtAtDifferentLocations() {
        // Arrange
        val mobileNumber = "9876543210"
        val date = LocalDate.now().plusDays(1)

        // Location 1
        val request1 = BookCourtRequest(1L, 1L, date, 1L)
        val response1 = BookCourtResponse("Location A", "Court-1", date, LocalTime.of(9, 0), LocalTime.of(10, 0), "CONFIRMED")
        `when`(bookingService.bookCourt(mobileNumber, request1)).thenReturn(response1)

        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.location").value("Location A"))

        // Location 2
        val request2 = BookCourtRequest(2L, 3L, date, 2L)
        val response2 = BookCourtResponse("Location B", "Court-3", date, LocalTime.of(10, 0), LocalTime.of(11, 0), "CONFIRMED")
        `when`(bookingService.bookCourt(mobileNumber, request2)).thenReturn(response2)

        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.location").value("Location B"))
    }

    @Test
    @DisplayName("Should fail booking without mobile header")
    fun testBookCourtWithoutMobileHeader() {
        // Arrange
        val request = BookCourtRequest(1L, 1L, LocalDate.now().plusDays(1), 1L)

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail booking with null location ID")
    fun testBookCourtWithNullLocationId() {
        // Arrange
        val mobileNumber = "9876543210"
        val jsonRequest = """
        {
            "locationId": null,
            "courtId": 1,
            "bookingDate": "${LocalDate.now().plusDays(1)}",
            "slotId": 1
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail booking with null court ID")
    fun testBookCourtWithNullCourtId() {
        // Arrange
        val mobileNumber = "9876543210"
        val jsonRequest = """
        {
            "locationId": 1,
            "courtId": null,
            "bookingDate": "${LocalDate.now().plusDays(1)}",
            "slotId": 1
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail booking with null date")
    fun testBookCourtWithNullDate() {
        // Arrange
        val mobileNumber = "9876543210"
        val jsonRequest = """
        {
            "locationId": 1,
            "courtId": 1,
            "bookingDate": null,
            "slotId": 1
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail booking with null slot ID")
    fun testBookCourtWithNullSlotId() {
        // Arrange
        val mobileNumber = "9876543210"
        val jsonRequest = """
        {
            "locationId": 1,
            "courtId": 1,
            "bookingDate": "${LocalDate.now().plusDays(1)}",
            "slotId": null
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings")
                .header("mobileNumber", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    // ===================== Cancel Booking Tests =====================

    @Test
    @DisplayName("Should cancel booking successfully")
    fun testCancelBookingSuccess() {
        // Arrange
        val userMobile = "9876543210"
        val bookingId = 1L
        val expectedResponse = ApiResponse<String>(
            success = true,
            message = "Booking cancelled successfully"
        )

        `when`(bookingService.cancelBooking(bookingId, userMobile)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/bookings/{bookingId}/cancel", bookingId)
                .header("userId", userMobile)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Booking cancelled successfully"))
    }

    @Test
    @DisplayName("Should cancel multiple bookings")
    fun testCancelMultipleBookings() {
        // Arrange
        val userMobile = "9876543210"
        val successResponse = ApiResponse<String>(success = true, message = "Booking cancelled successfully")

        `when`(bookingService.cancelBooking(1L, userMobile)).thenReturn(successResponse)
        `when`(bookingService.cancelBooking(2L, userMobile)).thenReturn(successResponse)

        // Cancel booking 1
        mockMvc.perform(
            post("/api/bookings/{bookingId}/cancel", 1L)
                .header("userId", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Cancel booking 2
        mockMvc.perform(
            post("/api/bookings/{bookingId}/cancel", 2L)
                .header("userId", userMobile)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should fail cancel without userId header")
    fun testCancelBookingWithoutUserIdHeader() {
        // Act & Assert
        mockMvc.perform(
            post("/api/bookings/{bookingId}/cancel", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should handle cancel on different booking IDs")
    fun testCancelBookingWithDifferentIds() {
        // Arrange
        val userMobile = "9876543210"
        val successResponse = ApiResponse<String>(success = true, message = "Booking cancelled successfully")

        // Test different booking IDs
        for (bookingId in 1..5) {
            `when`(bookingService.cancelBooking(bookingId.toLong(), userMobile)).thenReturn(successResponse)

            mockMvc.perform(
                post("/api/bookings/{bookingId}/cancel", bookingId)
                    .header("userId", userMobile)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
        }
    }
}

