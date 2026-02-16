package com.badminton.booking.config

import com.badminton.booking.entity.Court
import com.badminton.booking.entity.Location
import com.badminton.booking.entity.TimeSlot
import com.badminton.booking.entity.User
import com.badminton.booking.enum.UserRole
import com.badminton.booking.repository.CourtRepository
import com.badminton.booking.repository.LocationRepository
import com.badminton.booking.repository.TimeSlotRepository
import com.badminton.booking.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalTime

@Configuration
class DataInitializer(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val courtRepository: CourtRepository,
    private val timeSlotRepository: TimeSlotRepository
) {

    @Bean
    fun loadMasterData() = CommandLineRunner {

        // -------- USERS --------
        if (userRepository.count() == 0L) {
            val admin = User(
                mobileNumber = "9999999999",
                role = UserRole.ADMIN,
                password = "Admin123"
            )

            val user = User(
                mobileNumber = "9876543210",
                role = UserRole.USER,
                password = "User123"
            )
            userRepository.saveAll(listOf(admin, user))
        }

        // -------- LOCATION --------
        if (locationRepository.count() == 0L) {
            val location = Location(
                complexName = "Andheri Sports Club",
                name = "Mumbai",
                userMobile = "9999999999",
                imageUrl = "https://c8.alamy.com/comp/D9Y5RA/badminton-court-D9Y5RA.jpg",
                admin = userRepository.findById(1).get()
            )
            locationRepository.save(location)
        }
        val location = locationRepository.findAll().first()

        // -------- COURTS --------
        if (courtRepository.count() == 0L) {
            val courts = listOf(
                Court(name = "Court 1", location = location),
                Court(name = "Court 2", location = location)
            )
            courtRepository.saveAll(courts)
        }

        // -------- TIME SLOTS --------
        if (timeSlotRepository.count() == 0L) {
            val slots = listOf(
                TimeSlot(startTime = LocalTime.of(18, 0), endTime = LocalTime.of(19, 0)),
                TimeSlot(startTime = LocalTime.of(19, 0), endTime = LocalTime.of(20, 0)),
                TimeSlot(startTime = LocalTime.of(20, 0), endTime = LocalTime.of(21, 0))
            )
            timeSlotRepository.saveAll(slots)
        }

        println("✅ Master data loaded successfully")
    }
}