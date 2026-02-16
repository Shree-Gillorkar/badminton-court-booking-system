package com.badminton.booking.repository

import com.badminton.booking.entity.TimeSlot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeSlotRepository : JpaRepository<TimeSlot, Long> {
}