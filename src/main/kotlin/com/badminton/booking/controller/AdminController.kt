package com.badminton.booking.controller

import com.badminton.booking.dto.RegisterLocationRequest
import com.badminton.booking.service.AdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin API", description = "Admin operations")
@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    @Operation(summary = "Register Court")
    @PostMapping("/location/register")
    fun registerLocation(@RequestBody req: RegisterLocationRequest) = adminService.registerLocation(req)

    @Operation(summary = "Admin Dashboard")
    @GetMapping("/dashboard")
    fun dashboard(@RequestParam("mobile", required = true) mobile: String) = adminService.getAdminDashboard(mobile)
}