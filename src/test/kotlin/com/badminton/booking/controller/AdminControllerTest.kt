package com.badminton.booking.controller

import com.badminton.booking.dto.*
import com.badminton.booking.service.AdminService
import com.fasterxml.jackson.databind.ObjectMapper
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

@DisplayName("Admin Controller Tests")
class AdminControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var adminService: AdminService

    private lateinit var adminController: AdminController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        adminController = AdminController(adminService)
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build()
        objectMapper = ObjectMapper()
    }

    // ===================== Register Location Tests =====================

    @Test
    @DisplayName("Should register location successfully with valid request")
    fun testRegisterLocationSuccess() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Sports Complex A",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2,
            complexName = "Main Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = true,
            message = "Location and courts registered successfully",
            data = null
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Location and courts registered successfully"))
    }

    @Test
    @DisplayName("Should register location with maximum allowed courts")
    fun testRegisterLocationWithMaxCourts() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Elite Courts",
            imageUrl = "https://example.com/elite.jpg",
            numberOfCourts = 4,
            complexName = "Premium Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = true,
            message = "Location and courts registered successfully"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should register location with minimum allowed courts")
    fun testRegisterLocationWithMinCourts() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Basic Courts",
            imageUrl = "https://example.com/basic.jpg",
            numberOfCourts = 1,
            complexName = "Simple Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = true,
            message = "Location and courts registered successfully"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should fail registration with null admin mobile")
    fun testRegisterLocationWithNullAdminMobile() {
        // Arrange
        val jsonRequest = """
        {
            "adminMobile": null,
            "locationName": "Test Location",
            "imageUrl": "https://example.com/image.jpg",
            "numberOfCourts": 2,
            "complexName": "Test Complex"
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail registration with null location name")
    fun testRegisterLocationWithNullLocationName() {
        // Arrange
        val jsonRequest = """
        {
            "adminMobile": "9876543210",
            "locationName": null,
            "imageUrl": "https://example.com/image.jpg",
            "numberOfCourts": 2,
            "complexName": "Test Complex"
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail registration with invalid number of courts")
    fun testRegisterLocationWithInvalidCourts() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Invalid Courts",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 5,
            complexName = "Invalid Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = false,
            message = "Courts per location must be between 1 and 4"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Courts per location must be between 1 and 4"))
    }

    @Test
    @DisplayName("Should fail registration with zero courts")
    fun testRegisterLocationWithZeroCourts() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "No Courts",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 0,
            complexName = "No Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = false,
            message = "Courts per location must be between 1 and 4"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
    }

    @Test
    @DisplayName("Should fail registration with unregistered admin mobile")
    fun testRegisterLocationWithUnregisteredMobile() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "1111111111",
            locationName = "Test Location",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2,
            complexName = "Test Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = false,
            message = "User not found, Please Register!"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User not found, Please Register!"))
    }

    @Test
    @DisplayName("Should fail registration when admin reaches maximum locations")
    fun testRegisterLocationExceedsMaxLocations() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Fourth Location",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2,
            complexName = "Fourth Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = false,
            message = "Maximum 3 locations allowed per admin"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Maximum 3 locations allowed per admin"))
    }

    @Test
    @DisplayName("Should fail registration for non-admin user")
    fun testRegisterLocationWithNonAdminUser() {
        // Arrange
        val request = RegisterLocationRequest(
            adminMobile = "9876543210",
            locationName = "Test Location",
            imageUrl = "https://example.com/image.jpg",
            numberOfCourts = 2,
            complexName = "Test Complex"
        )

        val expectedResponse = ApiResponse<String>(
            success = false,
            message = "Only admin can register courts"
        )

        `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/admin/location/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Only admin can register courts"))
    }

    // ===================== Admin Dashboard Tests =====================

    @Test
    @DisplayName("Should fetch admin dashboard successfully")
    fun testGetAdminDashboardSuccess() {
        // Arrange
        val mobileNumber = "9876543210"

        val bookingDto = BookingDTO(
            bookingId = 1L,
            bookingDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = "CONFIRMED",
            userMobile = "9999999999"
        )

        val courtDashboard = CourtDashboardDTO(
            courtId = 1L,
            courtName = "Court-1",
            bookings = listOf(bookingDto)
        )

        val locationDashboard = LocationDashboardDTO(
            locationId = 1L,
            locationName = "Sports Complex A",
            imageUrl = "https://example.com/image.jpg",
            courts = listOf(courtDashboard)
        )

        val adminDashboardResponse = AdminDashboardResponse(
            locations = listOf(locationDashboard)
        )

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = true,
            message = "Admin dashboard fetched",
            data = adminDashboardResponse
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Admin dashboard fetched"))
            .andExpect(jsonPath("$.data.locations").isArray)
            .andExpect(jsonPath("$.data.locations[0].locationName").value("Sports Complex A"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].courtName").value("Court-1"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].bookings[0].status").value("CONFIRMED"))
    }

    @Test
    @DisplayName("Should fetch admin dashboard with multiple locations")
    fun testGetAdminDashboardWithMultipleLocations() {
        // Arrange
        val mobileNumber = "9876543210"

        val bookingDto1 = BookingDTO(
            bookingId = 1L,
            bookingDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = "CONFIRMED",
            userMobile = "9999999999"
        )

        val bookingDto2 = BookingDTO(
            bookingId = 2L,
            bookingDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = "CANCELLED",
            userMobile = "8888888888"
        )

        val courtDashboard1 = CourtDashboardDTO(
            courtId = 1L,
            courtName = "Court-1",
            bookings = listOf(bookingDto1)
        )

        val courtDashboard2 = CourtDashboardDTO(
            courtId = 2L,
            courtName = "Court-2",
            bookings = listOf(bookingDto2)
        )

        val locationDashboard1 = LocationDashboardDTO(
            locationId = 1L,
            locationName = "Location A",
            imageUrl = "https://example.com/location-a.jpg",
            courts = listOf(courtDashboard1)
        )

        val locationDashboard2 = LocationDashboardDTO(
            locationId = 2L,
            locationName = "Location B",
            imageUrl = "https://example.com/location-b.jpg",
            courts = listOf(courtDashboard2)
        )

        val adminDashboardResponse = AdminDashboardResponse(
            locations = listOf(locationDashboard1, locationDashboard2)
        )

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = true,
            message = "Admin dashboard fetched",
            data = adminDashboardResponse
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.locations").isArray)
            .andExpect(jsonPath("$.data.locations.length()").value(2))
            .andExpect(jsonPath("$.data.locations[0].locationName").value("Location A"))
            .andExpect(jsonPath("$.data.locations[1].locationName").value("Location B"))
    }

    @Test
    @DisplayName("Should fetch admin dashboard with multiple courts per location")
    fun testGetAdminDashboardWithMultipleCourts() {
        // Arrange
        val mobileNumber = "9876543210"

        val courtDashboard1 = CourtDashboardDTO(
            courtId = 1L,
            courtName = "Court-1",
            bookings = emptyList()
        )

        val courtDashboard2 = CourtDashboardDTO(
            courtId = 2L,
            courtName = "Court-2",
            bookings = emptyList()
        )

        val courtDashboard3 = CourtDashboardDTO(
            courtId = 3L,
            courtName = "Court-3",
            bookings = emptyList()
        )

        val locationDashboard = LocationDashboardDTO(
            locationId = 1L,
            locationName = "Sports Complex",
            imageUrl = "https://example.com/complex.jpg",
            courts = listOf(courtDashboard1, courtDashboard2, courtDashboard3)
        )

        val adminDashboardResponse = AdminDashboardResponse(
            locations = listOf(locationDashboard)
        )

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = true,
            message = "Admin dashboard fetched",
            data = adminDashboardResponse
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.locations[0].courts.length()").value(3))
    }

    @Test
    @DisplayName("Should fail dashboard fetch with non-admin user")
    fun testGetAdminDashboardWithNonAdminUser() {
        // Arrange
        val mobileNumber = "9876543210"

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = false,
            message = "Only admin allowed"
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Only admin allowed"))
    }

    @Test
    @DisplayName("Should fail dashboard fetch with unregistered mobile")
    fun testGetAdminDashboardWithUnregisteredMobile() {
        // Arrange
        val mobileNumber = "1111111111"

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = false,
            message = "Admin not found"
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Admin not found"))
    }

    @Test
    @DisplayName("Should return empty locations list when admin has no locations")
    fun testGetAdminDashboardWithNoLocations() {
        // Arrange
        val mobileNumber = "9876543210"

        val adminDashboardResponse = AdminDashboardResponse(
            locations = emptyList()
        )

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = true,
            message = "Admin dashboard fetched",
            data = adminDashboardResponse
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.locations").isArray)
            .andExpect(jsonPath("$.data.locations.length()").value(0))
    }

    @Test
    @DisplayName("Should handle dashboard fetch with various booking statuses")
    fun testGetAdminDashboardWithDifferentBookingStatuses() {
        // Arrange
        val mobileNumber = "9876543210"

        val confirmedBooking = BookingDTO(
            bookingId = 1L,
            bookingDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            status = "CONFIRMED",
            userMobile = "9999999999"
        )

        val cancelledBooking = BookingDTO(
            bookingId = 2L,
            bookingDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = "CANCELLED",
            userMobile = "8888888888"
        )

        val completedBooking = BookingDTO(
            bookingId = 3L,
            bookingDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = "COMPLETED",
            userMobile = "7777777777"
        )

        val courtDashboard = CourtDashboardDTO(
            courtId = 1L,
            courtName = "Court-1",
            bookings = listOf(confirmedBooking, cancelledBooking, completedBooking)
        )

        val locationDashboard = LocationDashboardDTO(
            locationId = 1L,
            locationName = "Complex",
            imageUrl = "https://example.com/complex.jpg",
            courts = listOf(courtDashboard)
        )

        val adminDashboardResponse = AdminDashboardResponse(
            locations = listOf(locationDashboard)
        )

        val expectedResponse = ApiResponse<AdminDashboardResponse>(
            success = true,
            message = "Admin dashboard fetched",
            data = adminDashboardResponse
        )

        `when`(adminService.getAdminDashboard(mobileNumber)).thenReturn(expectedResponse)

        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .param("mobile", mobileNumber)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.locations[0].courts[0].bookings.length()").value(3))
            .andExpect(jsonPath("$.data.locations[0].courts[0].bookings[0].status").value("CONFIRMED"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].bookings[1].status").value("CANCELLED"))
            .andExpect(jsonPath("$.data.locations[0].courts[0].bookings[2].status").value("COMPLETED"))
    }

    @Test
    @DisplayName("Should fail dashboard fetch with missing mobile parameter")
    fun testGetAdminDashboardWithMissingMobileParam() {
        // Act & Assert
        mockMvc.perform(
            get("/api/admin/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }
}