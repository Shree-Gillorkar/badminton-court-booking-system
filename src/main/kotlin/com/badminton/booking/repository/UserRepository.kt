package com.badminton.booking.repository

import com.badminton.booking.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByMobileNumber(mobileNumber: String): Optional<User>
}