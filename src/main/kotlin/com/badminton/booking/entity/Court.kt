package com.badminton.booking.entity

import jakarta.persistence.*

@Entity
@Table(name = "courts", uniqueConstraints = [UniqueConstraint(columnNames = ["court_number", "location_id"])])
class Court(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "court_number", nullable = false)
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    var location: Location
)