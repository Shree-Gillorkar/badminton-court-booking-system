package com.badminton.booking.entity

import com.badminton.booking.enum.UserRole
import jakarta.persistence.*

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["mobile_number"])])
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "mobile_number", nullable = false, length = 10)
    val mobileNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Column(nullable = false)
    val active: Boolean = true,

    var password: String
)