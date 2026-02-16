package com.badminton.booking.entity

import jakarta.persistence.*

@Entity
@Table(name = "locations", uniqueConstraints = [UniqueConstraint(columnNames = ["location_name", "admin_mobile"])])
data class Location(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "location_name", nullable = false)
    val name: String,

    @Column(name = "complex_name", nullable = false)
    val complexName: String,

    @Column(name = "image_url")
    val imageUrl: String? = null,

    @Column(name = "user_mobile", nullable = false)
    val userMobile: String,

    @OneToMany(mappedBy = "location", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val courts: MutableList<Court> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val admin: User
)