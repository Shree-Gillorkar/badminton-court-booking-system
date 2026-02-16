package com.badminton.booking.entity

import jakarta.persistence.*
import java.time.LocalTime

@Entity
data class TimeSlot(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val startTime: LocalTime = LocalTime.MIN,

    @Column(nullable = false)
    val endTime: LocalTime = LocalTime.MIN,
)