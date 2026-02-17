package com.badminton.booking.controller

import com.badminton.booking.dto.ApiResponse
import com.badminton.booking.dto.LoginRequest
import com.badminton.booking.dto.RegisterRequest
import com.badminton.booking.enum.UserRole
import com.badminton.booking.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var authService: AuthService

    private lateinit var authController: AuthController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authController = AuthController(authService)
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build()
        objectMapper = ObjectMapper()
    }

    // ===================== Register Tests =====================

    @Test
    @DisplayName("Should register user successfully with valid request")
    fun testRegisterUserSuccess() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Registered successfully!"))
    }

    @Test
    @DisplayName("Should register admin successfully")
    fun testRegisterAdminSuccess() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9999999999",
            password = "adminpass123",
            role = UserRole.ADMIN
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Registered successfully!"))
    }

    @Test
    @DisplayName("Should fail registration with null mobile number")
    fun testRegisterWithNullMobileNumber() {
        // Arrange
        val jsonRequest = """
        {
            "mobileNumber": null,
            "password": "password123",
            "role": "USER"
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail registration with null password")
    fun testRegisterWithNullPassword() {
        // Arrange
        val jsonRequest = """
        {
            "mobileNumber": "9876543210",
            "password": null,
            "role": "USER"
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail registration with null role")
    fun testRegisterWithNullRole() {
        // Arrange
        val jsonRequest = """
        {
            "mobileNumber": "9876543210",
            "password": "password123",
            "role": null
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should register with different valid roles")
    fun testRegisterWithDifferentRoles() {
        // Arrange - Test USER role
        val userRequest = RegisterRequest(
            mobileNumber = "8765432100",
            password = "userpass",
            role = UserRole.USER
        )

        // Act & Assert - USER role
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Arrange - Test ADMIN role
        val adminRequest = RegisterRequest(
            mobileNumber = "8765432101",
            password = "adminpass",
            role = UserRole.ADMIN
        )

        // Act & Assert - ADMIN role
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should register with 10-digit mobile number")
    fun testRegisterWithValidMobileFormat() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "1234567890",
            password = "password",
            role = UserRole.USER
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should register with strong password")
    fun testRegisterWithStrongPassword() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9876543210",
            password = "Secure@Password123!",
            role = UserRole.USER
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    // ===================== Login Tests =====================

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    fun testLoginUserSuccess() {
        // Arrange
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "password123"
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful!"))
    }

    @Test
    @DisplayName("Should login admin successfully")
    fun testLoginAdminSuccess() {
        // Arrange
        val request = LoginRequest(
            mobileNumber = "9999999999",
            password = "adminpass123"
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful!"))
    }

    @Test
    @DisplayName("Should fail login with null mobile number")
    fun testLoginWithNullMobileNumber() {
        // Arrange
        val jsonRequest = """
        {
            "mobileNumber": null,
            "password": "password123"
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should fail login with null password")
    fun testLoginWithNullPassword() {
        // Arrange
        val jsonRequest = """
        {
            "mobileNumber": "9876543210",
            "password": null
        }
        """.trimIndent()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should login with different valid mobile numbers")
    fun testLoginWithDifferentMobileNumbers() {
        // Test mobile 1
        val request1 = LoginRequest(
            mobileNumber = "1234567890",
            password = "password"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Test mobile 2
        val request2 = LoginRequest(
            mobileNumber = "9876543210",
            password = "password"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should login with various password formats")
    fun testLoginWithDifferentPasswordFormats() {
        // Simple password
        val request1 = LoginRequest(
            mobileNumber = "9876543210",
            password = "pass"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Complex password
        val request2 = LoginRequest(
            mobileNumber = "9876543210",
            password = "Complex@Pass#123!Secure"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should handle login with whitespace in password")
    fun testLoginWithWhitespacePassword() {
        // Arrange
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "pass with spaces"
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should authenticate and return success response structure")
    fun testAuthResponseStructure() {
        // Arrange
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "password123"
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    @DisplayName("Should register multiple users sequentially")
    fun testMultipleRegistrations() {
        // Register user 1
        val request1 = RegisterRequest(
            mobileNumber = "1111111111",
            password = "pass1",
            role = UserRole.USER
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Register user 2
        val request2 = RegisterRequest(
            mobileNumber = "2222222222",
            password = "pass2",
            role = UserRole.ADMIN
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("Should handle login after registration")
    fun testLoginAfterRegistration() {
        // Register
        val registerRequest = RegisterRequest(
            mobileNumber = "5555555555",
            password = "testpass",
            role = UserRole.USER
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        // Login
        val loginRequest = LoginRequest(
            mobileNumber = "5555555555",
            password = "testpass"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }
}

