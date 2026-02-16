package com.badminton.booking.controller

import com.badminton.booking.dto.ApiResponse
import com.badminton.booking.dto.LoginRequest
import com.badminton.booking.dto.RegisterRequest
import com.badminton.booking.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth API", description = "Register/Login operations")
@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "Register User")
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<Void>> {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse(success = true, message = "Registered successfully!"));
    }

    @Operation(summary = "Login User")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<Void>> {
        authService.login(request);
        return ResponseEntity.ok(ApiResponse(success = true, message = "Login successful!"))
    }
}