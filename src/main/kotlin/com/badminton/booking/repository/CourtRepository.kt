package com.badminton.booking.repository

import com.badminton.booking.entity.Court
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourtRepository : JpaRepository<Court, Long> {
}