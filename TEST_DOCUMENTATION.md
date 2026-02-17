# AdminController Test Cases Documentation

## Overview
This document describes all the test cases written for the `AdminController` class. The test suite covers both endpoints of the AdminController with comprehensive test cases for success and failure scenarios.

## Test Setup

### Framework
- **Testing Framework**: JUnit 5
- **Mocking Framework**: Mockito
- **HTTP Testing**: Spring Test MockMvc
- **Language**: Kotlin

### Test Class
- **Location**: `src/test/kotlin/com/badminton/booking/controller/AdminControllerTest.kt`
- **Total Test Cases**: 16

### Dependencies Used
```kotlin
- org.junit.jupiter:junit-jupiter
- org.mockito:mockito-core
- org.mockito.kotlin:mockito-kotlin
- org.springframework.test:spring-test
- com.fasterxml.jackson.databind:jackson-databind
```

## Setup Method

```kotlin
@BeforeEach
fun setUp() {
    MockitoAnnotations.openMocks(this)
    adminController = AdminController(adminService)
    mockMvc = MockMvcBuilders.standaloneSetup(adminController).build()
    objectMapper = ObjectMapper()
}
```

This method initializes:
- Mockito annotations for mock objects
- AdminController instance with mocked AdminService
- MockMvc for HTTP testing
- ObjectMapper for JSON serialization/deserialization

---

## Test Cases

### 1. Register Location Endpoint Tests

#### **Test Case 1: testRegisterLocationSuccess**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify successful location registration with valid request
- **Input**: Valid RegisterLocationRequest with:
  - adminMobile: "9876543210"
  - locationName: "Sports Complex A"
  - imageUrl: "https://example.com/image.jpg"
  - numberOfCourts: 2
  - complexName: "Main Complex"
- **Expected Response**: 
  - HTTP Status: 200 OK
  - Response Body:
    ```json
    {
      "success": true,
      "message": "Location and courts registered successfully"
    }
    ```
- **Assertions**: Verifies success flag and message in response

#### **Test Case 2: testRegisterLocationWithMaxCourts**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify location registration with maximum allowed courts (4)
- **Input**: Valid request with numberOfCourts: 4
- **Expected Response**: HTTP Status 200 OK, success: true
- **Validates**: System accepts maximum boundary value

#### **Test Case 3: testRegisterLocationWithMinCourts**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify location registration with minimum allowed courts (1)
- **Input**: Valid request with numberOfCourts: 1
- **Expected Response**: HTTP Status 200 OK, success: true
- **Validates**: System accepts minimum boundary value

#### **Test Case 4: testRegisterLocationWithNullAdminMobile**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify request validation rejects null adminMobile
- **Input**: RegisterLocationRequest with adminMobile: null
- **Expected Response**: HTTP Status 400 Bad Request
- **Validates**: Spring validates @NotNull constraint

#### **Test Case 5: testRegisterLocationWithNullLocationName**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify request validation rejects null locationName
- **Input**: RegisterLocationRequest with locationName: null
- **Expected Response**: HTTP Status 400 Bad Request
- **Validates**: Spring validates @NotNull constraint

#### **Test Case 6: testRegisterLocationWithInvalidCourts**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify rejection of courts exceeding maximum (5)
- **Input**: Valid request with numberOfCourts: 5
- **Expected Response**: 
  - HTTP Status: 200 OK
  - success: false
  - message: "Courts per location must be between 1 and 4"
- **Validates**: Business rule enforcement

#### **Test Case 7: testRegisterLocationWithZeroCourts**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify rejection of zero courts
- **Input**: Valid request with numberOfCourts: 0
- **Expected Response**: 
  - HTTP Status: 200 OK
  - success: false
  - message: "Courts per location must be between 1 and 4"
- **Validates**: Business rule enforcement

#### **Test Case 8: testRegisterLocationWithUnregisteredMobile**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify rejection when admin mobile is not registered
- **Input**: Valid request with unregistered adminMobile: "1111111111"
- **Expected Response**:
  - HTTP Status: 200 OK
  - success: false
  - message: "User not found, Please Register!"
- **Validates**: User existence check

#### **Test Case 9: testRegisterLocationExceedsMaxLocations**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify rejection when admin exceeds max locations (3)
- **Input**: Valid request from admin with 3 existing locations
- **Expected Response**:
  - HTTP Status: 200 OK
  - success: false
  - message: "Maximum 3 locations allowed per admin"
- **Validates**: Admin location limit enforcement

#### **Test Case 10: testRegisterLocationWithNonAdminUser**
- **Endpoint**: `POST /api/admin/location/register`
- **Purpose**: Verify rejection when user is not an admin
- **Input**: Valid request from non-admin user
- **Expected Response**:
  - HTTP Status: 200 OK
  - success: false
  - message: "Only admin can register courts"
- **Validates**: Role-based authorization

---

### 2. Admin Dashboard Endpoint Tests

#### **Test Case 11: testGetAdminDashboardSuccess**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify successful dashboard fetch with valid admin
- **Input**: Valid mobile number: "9876543210"
- **Expected Response**: HTTP Status 200 OK with dashboard data containing:
  - List of locations with id, name, imageUrl
  - List of courts per location
  - List of bookings per court with details
- **Validates**: Full dashboard structure and data retrieval

#### **Test Case 12: testGetAdminDashboardWithMultipleLocations**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify dashboard returns multiple locations correctly
- **Input**: Mobile number of admin with 2 locations
- **Expected Response**: HTTP Status 200 OK with 2 LocationDashboardDTOs
- **Assertions**:
  - `$.data.locations.length()` equals 2
  - Locations have correct names: "Location A", "Location B"
- **Validates**: Handling multiple locations in dashboard

#### **Test Case 13: testGetAdminDashboardWithMultipleCourts**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify dashboard shows multiple courts per location
- **Input**: Mobile number with location containing 3 courts
- **Expected Response**: HTTP Status 200 OK with 3 CourtDashboardDTOs
- **Assertions**: `$.data.locations[0].courts.length()` equals 3
- **Validates**: Handling multiple courts

#### **Test Case 14: testGetAdminDashboardWithNonAdminUser**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify rejection when user is not an admin
- **Input**: Mobile number of non-admin user
- **Expected Response**:
  - HTTP Status: 200 OK
  - success: false
  - message: "Only admin allowed"
- **Validates**: Role-based authorization

#### **Test Case 15: testGetAdminDashboardWithUnregisteredMobile**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify rejection when mobile number is not registered
- **Input**: Unregistered mobile: "1111111111"
- **Expected Response**:
  - HTTP Status: 200 OK
  - success: false
  - message: "Admin not found"
- **Validates**: User existence check

#### **Test Case 16: testGetAdminDashboardWithNoLocations**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify dashboard returns empty locations list when admin has no locations
- **Input**: Mobile number of admin with no locations
- **Expected Response**: HTTP Status 200 OK with empty locations array
- **Assertions**: `$.data.locations.length()` equals 0
- **Validates**: Edge case handling

#### **Test Case 17: testGetAdminDashboardWithDifferentBookingStatuses**
- **Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`
- **Purpose**: Verify dashboard correctly displays bookings with various statuses
- **Input**: Mobile number with bookings in different states (CONFIRMED, CANCELLED, COMPLETED)
- **Expected Response**: HTTP Status 200 OK with all booking statuses preserved
- **Assertions**:
  - 3 bookings returned
  - Status 0: "CONFIRMED"
  - Status 1: "CANCELLED"
  - Status 2: "COMPLETED"
- **Validates**: Status handling and data preservation

#### **Test Case 18: testGetAdminDashboardWithMissingMobileParam**
- **Endpoint**: `GET /api/admin/dashboard` (without mobile parameter)
- **Purpose**: Verify request validation requires mobile parameter
- **Input**: Request without mobile query parameter
- **Expected Response**: HTTP Status 400 Bad Request
- **Validates**: Required parameter enforcement

---

## Test Data

### Sample Data Used

#### RegisterLocationRequest
```kotlin
RegisterLocationRequest(
    adminMobile = "9876543210",
    locationName = "Sports Complex A",
    imageUrl = "https://example.com/image.jpg",
    numberOfCourts = 2,
    complexName = "Main Complex"
)
```

#### BookingDTO
```kotlin
BookingDTO(
    bookingId = 1L,
    bookingDate = LocalDate.now(),
    startTime = LocalTime.of(9, 0),
    endTime = LocalTime.of(10, 0),
    status = "CONFIRMED",
    userMobile = "9999999999"
)
```

#### LocationDashboardDTO
```kotlin
LocationDashboardDTO(
    locationId = 1L,
    locationName = "Sports Complex A",
    imageUrl = "https://example.com/image.jpg",
    courts = listOf(courtDashboard)
)
```

---

## Running the Tests

### Run All Tests
```bash
./mvnw clean test
```

### Run Only AdminControllerTest
```bash
./mvnw clean test -Dtest=AdminControllerTest
```

### Run Specific Test Case
```bash
./mvnw clean test -Dtest=AdminControllerTest#testRegisterLocationSuccess
```

---

## Key Testing Patterns

1. **Arrange-Act-Assert (AAA)**: Each test follows the AAA pattern for clarity
2. **Mocking**: AdminService is mocked to isolate controller testing
3. **JSON Path Assertions**: Response JSON is validated using jsonPath expressions
4. **Descriptive Names**: Test names clearly describe what is being tested
5. **Display Names**: @DisplayName annotations provide human-readable test descriptions

---

## Coverage Summary

| Feature | Test Cases | Coverage |
|---------|-----------|----------|
| Location Registration | 10 | ✓ Success, Validation, Business Rules, Authorization |
| Admin Dashboard | 8 | ✓ Success, Multiple Data, Edge Cases, Authorization |
| **Total** | **18** | **Comprehensive** |

---

## Notes

- All tests use Mockito to mock the AdminService, ensuring controller logic is tested in isolation
- MockMvc provides comprehensive HTTP request/response testing
- JSON responses are validated using Spring's jsonPath matchers
- Tests cover both happy path and error scenarios
- Boundary values are tested for validation rules

