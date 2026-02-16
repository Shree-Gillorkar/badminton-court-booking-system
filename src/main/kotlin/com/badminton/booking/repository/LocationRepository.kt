package com.badminton.booking.repository

import com.badminton.booking.entity.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long> {
    fun countByAdminId(adminId: Long): Long
    fun findByAdminId(adminId: Long): List<Location>
}