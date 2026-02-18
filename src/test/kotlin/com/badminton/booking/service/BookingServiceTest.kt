package com.badminton.booking.service

import com.badminton.booking.dto.BookCourtRequest
import com.badminton.booking.entity.*
import com.badminton.booking.enum.BookingStatus
import com.badminton.booking.enum.UserRole
import com.badminton.booking.exception.*
import com.badminton.booking.repository.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@DisplayName("BookingService Tests")
class BookingServiceTest {

    @Mock
    private lateinit var bookingRepository: BookingRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var courtRepository: CourtRepository

    @Mock
    private lateinit var timeSlotRepository: TimeSlotRepository

    @Mock
    private lateinit var locationRepository: LocationRepository

    private lateinit var bookingService: BookingService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        bookingService = BookingService(
            bookingRepository,
            userRepository,
            courtRepository,
            timeSlotRepository,
            locationRepository
        )
    }


    @Test
    @DisplayName("Should get full availability for a specific date")
    fun testGetFullAvailabilitySuccess() {
        val date = LocalDate.now().plusDays(1)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = "9999999999",
            admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN),
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        location.courts.add(court)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))

        whenever(locationRepository.findAll()).thenReturn(listOf(location))
        whenever(timeSlotRepository.findAll()).thenReturn(listOf(timeSlot))
        whenever(bookingRepository.findByBookingDate(date)).thenReturn(emptyList())

        val response = bookingService.getFullAvailability(date)

        assert(response.date == date)
        assert(response.locations.isNotEmpty())
        assert(response.locations[0].courts[0].slots[0].status == "AVAILABLE")
    }

    @Test
    @DisplayName("Should show booked slots correctly")
    fun testGetFullAvailabilityWithBookedSlots() {
        val date = LocalDate.now().plusDays(1)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Sports Complex",
            imageUrl = "https://example.com/image.jpg",
            userMobile = admin.mobileNumber,
            admin = admin,
            courts = mutableListOf()
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        location.courts.add(court)
        val user = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.USER)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val booking = Booking(id = 1, bookingDate = date, user = user, court = court, timeSlot = timeSlot)

        whenever(locationRepository.findAll()).thenReturn(listOf(location))
        whenever(timeSlotRepository.findAll()).thenReturn(listOf(timeSlot))
        whenever(bookingRepository.findByBookingDate(date)).thenReturn(listOf(booking))

        val response = bookingService.getFullAvailability(date)

        assert(response.locations[0].courts[0].slots[0].status == "BOOKED")
    }

    @Test
    @DisplayName("Should return availability with multiple locations and courts")
    fun testGetFullAvailabilityMultipleLocationsAndCourts() {
        val date = LocalDate.now().plusDays(1)
        val admin1 = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val admin2 = User(id = 2, mobileNumber = "8888888888", password = "pass", role = UserRole.ADMIN)

        val location1 = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex 1",
            imageUrl = "url1",
            userMobile = admin1.mobileNumber,
            admin = admin1,
            courts = mutableListOf()
        )
        val location2 = Location(
            id = 2,
            name = "Bangalore",
            complexName = "Complex 2",
            imageUrl = "url2",
            userMobile = admin2.mobileNumber,
            admin = admin2,
            courts = mutableListOf()
        )

        val court1 = Court(id = 1, name = "Court 1", location = location1)
        val court2 = Court(id = 2, name = "Court 2", location = location1)
        val court3 = Court(id = 3, name = "Court 1", location = location2)

        location1.courts.addAll(listOf(court1, court2))
        location2.courts.add(court3)

        val timeSlot1 = TimeSlot(id = 1, startTime = LocalTime.of(6, 0), endTime = LocalTime.of(7, 0))
        val timeSlot2 = TimeSlot(id = 2, startTime = LocalTime.of(7, 0), endTime = LocalTime.of(8, 0))

        whenever(locationRepository.findAll()).thenReturn(listOf(location1, location2))
        whenever(timeSlotRepository.findAll()).thenReturn(listOf(timeSlot1, timeSlot2))
        whenever(bookingRepository.findByBookingDate(date)).thenReturn(emptyList())

        val response = bookingService.getFullAvailability(date)

        assert(response.locations.size == 2)
        assert(response.locations[0].courts.size == 2)
        assert(response.locations[1].courts.size == 1)
    }


    @Test
    @DisplayName("Should book court successfully")
    fun testBookCourtSuccess() {
        val mobileNumber = "8888888888"
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val startTime = LocalTime.of(6, 0)
        val timeSlot = TimeSlot(id = 1, startTime = startTime, endTime = LocalTime.of(7, 0))
        val bookingDate = LocalDate.now().plusDays(1)
        val request = BookCourtRequest(
            locationId = 1,
            courtId = 1,
            bookingDate = bookingDate,
            slotId = 1
        )

        whenever(userRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(user))
        whenever(locationRepository.findById(1L)).thenReturn(Optional.of(location))
        whenever(courtRepository.findById(1L)).thenReturn(Optional.of(court))
        whenever(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot))
        val expectedEndTime = startTime.plusHours(1)
        whenever(
            bookingRepository.existsByCourt_IdAndCourt_Location_IdAndAndBookingDateAndTimeSlot_StartTimeLessThanAndTimeSlot_EndTimeGreaterThan(
                locationId = location.id,
                courtId = court.id,
                bookingDate = bookingDate,
                endTime = expectedEndTime,
                startTime = startTime
            )
        ).thenReturn(false)
        whenever(bookingRepository.save(any<Booking>())).thenAnswer { it.arguments[0] as Booking }

        val response = bookingService.bookCourt(mobileNumber, request)

        assert(response.location == location.name)
        assert(response.court == court.name)
        assert(response.bookingDate == bookingDate)
        verify(bookingRepository).save(any<Booking>())
    }

    @Test
    @DisplayName("Should throw exception when user not registered")
    fun testBookCourtUserNotRegistered() {
        val mobileNumber = "8888888888"
        val request = BookCourtRequest(
            locationId = 1,
            courtId = 1,
            bookingDate = LocalDate.now().plusDays(1),
            slotId = 1
        )
        whenever(userRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.empty())

        assertThrows<MobileNotRegisteredException> {
            bookingService.bookCourt(mobileNumber, request)
        }
    }

    @Test
    @DisplayName("Should throw exception when location not found")
    fun testBookCourtLocationNotFound() {
        val mobileNumber = "8888888888"
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val request = BookCourtRequest(
            locationId = 99,
            courtId = 1,
            bookingDate = LocalDate.now().plusDays(1),
            slotId = 1
        )
        whenever(userRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(user))
        whenever(locationRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<LocationNotFoundException> {
            bookingService.bookCourt(mobileNumber, request)
        }
    }

    @Test
    @DisplayName("Should throw exception when slot already booked")
    fun testBookCourtSlotAlreadyBooked() {
        val mobileNumber = "8888888888"
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val startTime = LocalTime.of(6, 0)
        val timeSlot = TimeSlot(id = 1, startTime = startTime, endTime = LocalTime.of(7, 0))
        val bookingDate = LocalDate.now().plusDays(1)
        val request = BookCourtRequest(
            locationId = 1,
            courtId = 1,
            bookingDate = bookingDate,
            slotId = 1
        )

        whenever(userRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(user))
        whenever(locationRepository.findById(1L)).thenReturn(Optional.of(location))
        whenever(courtRepository.findById(1L)).thenReturn(Optional.of(court))
        whenever(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot))
        val expectedEndTime = startTime.plusHours(1)
        whenever(
            bookingRepository.existsByCourt_IdAndCourt_Location_IdAndAndBookingDateAndTimeSlot_StartTimeLessThanAndTimeSlot_EndTimeGreaterThan(
                locationId = location.id,
                courtId = court.id,
                bookingDate = bookingDate,
                endTime = expectedEndTime,
                startTime = startTime
            )
        ).thenReturn(true)

        assertThrows<SlotAlreadyBookedException> {
            bookingService.bookCourt(mobileNumber, request)
        }
    }


    @Test
    @DisplayName("Should cancel booking successfully")
    fun testCancelBookingSuccess() {
        val mobileNumber = "8888888888"
        val bookingId = 1L
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(14, 0), endTime = LocalTime.of(15, 0))
        val booking = Booking(
            id = bookingId,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))
        whenever(bookingRepository.save(any<Booking>())).thenAnswer { it.arguments[0] as Booking }

        val response = bookingService.cancelBooking(bookingId, mobileNumber)

        assert(response.success)
        assert(response.message.contains("cancelled"))
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    fun testCancelBookingNotFound() {
        val mobileNumber = "8888888888"
        val bookingId = 99L
        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.empty())

        assertThrows<BookingNotFoundException> {
            bookingService.cancelBooking(bookingId, mobileNumber)
        }
    }

    @Test
    @DisplayName("Should throw exception when canceling booking of another user")
    fun testCancelBookingUnauthorized() {
        val mobileNumber = "8888888888"
        val otherMobileNumber = "7777777777"
        val bookingId = 1L
        val user = User(id = 2, mobileNumber = otherMobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(14, 0), endTime = LocalTime.of(15, 0))
        val booking = Booking(
            id = bookingId,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))

        assertThrows<BookingCancellationNotAllowedException> {
            bookingService.cancelBooking(bookingId, mobileNumber)
        }
    }

    @Test
    @DisplayName("Should throw exception when canceling already cancelled booking")
    fun testCancelBookingAlreadyCancelled() {
        val mobileNumber = "8888888888"
        val bookingId = 1L
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot = TimeSlot(id = 1, startTime = LocalTime.of(14, 0), endTime = LocalTime.of(15, 0))
        val booking = Booking(
            id = bookingId,
            bookingDate = LocalDate.now().plusDays(1),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.CANCELLED
        )

        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))

        assertThrows<BookingCancellationNotAllowedException> {
            bookingService.cancelBooking(bookingId, mobileNumber)
        }
    }

    @Test
    @DisplayName("Should throw exception when canceling booking less than 4 hours before")
    fun testCancelBookingLessThan4HoursBefore() {
        val mobileNumber = "8888888888"
        val bookingId = 1L
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot =
            TimeSlot(id = 1, startTime = LocalTime.now().plusHours(2), endTime = LocalTime.now().plusHours(3))
        val booking = Booking(
            id = bookingId,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))

        assertThrows<BookingCancellationNotAllowedException> {
            bookingService.cancelBooking(bookingId, mobileNumber)
        }
    }

    @Test
    @DisplayName("Should throw exception when canceling past booking")
    fun testCancelBookingPastBooking() {
        val mobileNumber = "8888888888"
        val bookingId = 1L
        val user = User(id = 2, mobileNumber = mobileNumber, password = "pass", role = UserRole.USER)
        val admin = User(id = 1, mobileNumber = "9999999999", password = "pass", role = UserRole.ADMIN)
        val location = Location(
            id = 1,
            name = "Mumbai",
            complexName = "Complex",
            imageUrl = "url",
            userMobile = admin.mobileNumber,
            admin = admin
        )
        val court = Court(id = 1, name = "Court 1", location = location)
        val timeSlot =
            TimeSlot(id = 1, startTime = LocalTime.now().minusHours(2), endTime = LocalTime.now().minusHours(1))
        val booking = Booking(
            id = bookingId,
            bookingDate = LocalDate.now(),
            user = user,
            court = court,
            timeSlot = timeSlot,
            status = BookingStatus.BOOKED
        )

        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))

        assertThrows<BookingCancellationNotAllowedException> {
            bookingService.cancelBooking(bookingId, mobileNumber)
        }
    }
}

