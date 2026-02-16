package com.badminton.booking.entity

import com.badminton.booking.enum.BookingStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "bookings",
    indexes = [
        Index(name = "idx_booking_mobile", columnList = "user_mobile"),
        Index(name = "idx_booking_date", columnList = "booking_date")
    ]
)
class Booking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "booking_date", nullable = false)
    var bookingDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mobile", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    var court: Court,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    var timeSlot: TimeSlot,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookingStatus = BookingStatus.BOOKED,
)