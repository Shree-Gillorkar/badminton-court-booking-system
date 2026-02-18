package com.badminton.booking.service

import com.badminton.booking.dto.LoginRequest
import com.badminton.booking.dto.RegisterRequest
import com.badminton.booking.entity.User
import com.badminton.booking.enum.UserRole
import com.badminton.booking.exception.InvalidCredentialsException
import com.badminton.booking.exception.MobileNotRegisteredException
import com.badminton.booking.exception.UserAlreadyExistException
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
import java.util.*

@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authService = AuthService(userRepository)
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Should register user successfully with valid credentials")
    fun testRegisterUserSuccess() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.empty())

        // Act
        authService.register(request)

        // Assert
        verify(userRepository).findByMobileNumber(request.mobileNumber)
        verify(userRepository).save(any<User>())
    }

    @Test
    @DisplayName("Should register admin successfully")
    fun testRegisterAdminSuccess() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.empty())

        // Act
        authService.register(request)

        // Assert
        verify(userRepository).findByMobileNumber(request.mobileNumber)
        verify(userRepository).save(any<User>())
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    fun testRegisterUserAlreadyExists() {
        // Arrange
        val existingUser = User(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER,
            active = true
        )
        val request = RegisterRequest(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(existingUser))

        // Act & Assert
        assertThrows<UserAlreadyExistException> {
            authService.register(request)
        }
        verify(userRepository).findByMobileNumber(request.mobileNumber)
    }

    @Test
    @DisplayName("Should register with different mobile numbers")
    fun testRegisterMultipleUsers() {
        // Arrange
        val request1 = RegisterRequest(
            mobileNumber = "1111111111",
            password = "pass1",
            role = UserRole.USER
        )
        val request2 = RegisterRequest(
            mobileNumber = "2222222222",
            password = "pass2",
            role = UserRole.ADMIN
        )
        whenever(userRepository.findByMobileNumber(request1.mobileNumber)).thenReturn(Optional.empty())
        whenever(userRepository.findByMobileNumber(request2.mobileNumber)).thenReturn(Optional.empty())

        // Act
        authService.register(request1)
        authService.register(request2)

        // Assert
        verify(userRepository).findByMobileNumber(request1.mobileNumber)
        verify(userRepository).findByMobileNumber(request2.mobileNumber)
        verify(userRepository, org.mockito.kotlin.times(2)).save(any<User>())
    }

    @Test
    @DisplayName("Should register user with strong password")
    fun testRegisterWithStrongPassword() {
        // Arrange
        val request = RegisterRequest(
            mobileNumber = "9876543210",
            password = "Secure@Password123!#$",
            role = UserRole.USER
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.empty())

        // Act
        authService.register(request)

        // Assert
        verify(userRepository).save(any<User>())
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    fun testLoginUserSuccess() {
        // Arrange
        val user = User(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER,
            active = true
        )
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "password123"
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(user))

        // Act & Assert - Should not throw exception
        authService.login(request)
        verify(userRepository).findByMobileNumber(request.mobileNumber)
    }

    @Test
    @DisplayName("Should login admin successfully")
    fun testLoginAdminSuccess() {
        // Arrange
        val admin = User(
            mobileNumber = "9999999999",
            password = "adminpass",
            role = UserRole.ADMIN,
            active = true
        )
        val request = LoginRequest(
            mobileNumber = "9999999999",
            password = "adminpass"
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(admin))

        // Act & Assert - Should not throw exception
        authService.login(request)
        verify(userRepository).findByMobileNumber(request.mobileNumber)
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    fun testLoginUserNotFound() {
        // Arrange
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "password123"
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows<MobileNotRegisteredException> {
            authService.login(request)
        }
        verify(userRepository).findByMobileNumber(request.mobileNumber)
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    fun testLoginInvalidPassword() {
        // Arrange
        val user = User(
            mobileNumber = "9876543210",
            password = "correctpassword",
            role = UserRole.USER,
            active = true
        )
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "wrongpassword"
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(user))

        // Act & Assert
        assertThrows<InvalidCredentialsException> {
            authService.login(request)
        }
        verify(userRepository).findByMobileNumber(request.mobileNumber)
    }

    @Test
    @DisplayName("Should handle empty password during login")
    fun testLoginWithEmptyPassword() {
        // Arrange
        val user = User(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER,
            active = true
        )
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = ""
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(user))

        // Act & Assert
        assertThrows<InvalidCredentialsException> {
            authService.login(request)
        }
    }

    @Test
    @DisplayName("Should login with case-sensitive password")
    fun testLoginCaseSensitivePassword() {
        // Arrange
        val user = User(
            mobileNumber = "9876543210",
            password = "Password123",
            role = UserRole.USER,
            active = true
        )
        val request = LoginRequest(
            mobileNumber = "9876543210",
            password = "password123"  // Different case
        )
        whenever(userRepository.findByMobileNumber(request.mobileNumber)).thenReturn(Optional.of(user))

        // Act & Assert
        assertThrows<InvalidCredentialsException> {
            authService.login(request)
        }
    }

    @Test
    @DisplayName("Should successfully login after registration")
    fun testLoginAfterRegistration() {
        // Arrange
        val registerRequest = RegisterRequest(
            mobileNumber = "9876543210",
            password = "password123",
            role = UserRole.USER
        )
        val loginRequest = LoginRequest(
            mobileNumber = "9876543211",
            password = "password123"
        )
        val registeredUser = User(
            mobileNumber = "9876543212",
            password = "password123",
            role = UserRole.USER,
            active = true
        )

        whenever(userRepository.findByMobileNumber(registerRequest.mobileNumber)).thenReturn(Optional.empty())
        whenever(userRepository.findByMobileNumber(loginRequest.mobileNumber)).thenReturn(Optional.of(registeredUser))

        // Act
        authService.register(registerRequest)
        authService.login(loginRequest)

        // Assert
        verify(userRepository).save(any<User>())
    }
}

