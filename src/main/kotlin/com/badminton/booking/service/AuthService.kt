package com.badminton.booking.service

import com.badminton.booking.dto.LoginRequest
import com.badminton.booking.dto.RegisterRequest
import com.badminton.booking.entity.User
import com.badminton.booking.exception.InvalidCredentialsException
import com.badminton.booking.exception.MobileNotRegisteredException
import com.badminton.booking.exception.UserAlreadyExistException
import com.badminton.booking.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository) {
    fun register(request: RegisterRequest) {
        if (userRepository.findByMobileNumber(request.mobileNumber).isPresent) {
            throw UserAlreadyExistException("User already exists with this mobile number")
        }
        val user = User(
            mobileNumber = request.mobileNumber,
            password = request.password,
            active = true,
            role = request.role
        )
        userRepository.save(user)
    }

    fun login(request: LoginRequest) {
        val user = userRepository.findByMobileNumber(request.mobileNumber)
            .orElseThrow { MobileNotRegisteredException(request.mobileNumber) }
        if (user.password != request.password) {
            throw InvalidCredentialsException("Invalid credentials")
        }
    }
}