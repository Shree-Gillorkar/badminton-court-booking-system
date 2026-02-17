# AdminController Test Cases - Quick Reference Guide

## Overview
Complete test suite for the AdminController with 18 comprehensive test cases covering all functionality.

## What Was Created

### Files Modified/Created:
1. ✅ **AdminControllerTest.kt** - Complete test suite with 18 test cases
2. ✅ **pom.xml** - Added Mockito dependencies for testing
3. ✅ **kotlinc.xml** - Updated Kotlin version to 2.2.21
4. ✅ **TEST_DOCUMENTATION.md** - Detailed documentation of all test cases
5. ✅ **KOTLIN_VERSION_FIX.md** - Resolution guide for version compatibility

## Running Tests

### Run All Tests in AdminControllerTest
```powershell
cd "C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking"
.\mvnw test -Dtest=AdminControllerTest
```

### Run Specific Test Case
```powershell
# Example: Run testRegisterLocationSuccess
.\mvnw test -Dtest=AdminControllerTest#testRegisterLocationSuccess
```

### Run All Project Tests
```powershell
.\mvnw test
```

### View Test Reports
After running tests, reports are available at:
```
target/surefire-reports/
```

## Test Case Categories

### Location Registration Endpoint (10 Test Cases)
1. ✅ **testRegisterLocationSuccess** - Valid registration
2. ✅ **testRegisterLocationWithMaxCourts** - Max courts (4)
3. ✅ **testRegisterLocationWithMinCourts** - Min courts (1)
4. ✅ **testRegisterLocationWithNullAdminMobile** - Null mobile validation
5. ✅ **testRegisterLocationWithNullLocationName** - Null name validation
6. ✅ **testRegisterLocationWithInvalidCourts** - Courts > 4
7. ✅ **testRegisterLocationWithZeroCourts** - Courts = 0
8. ✅ **testRegisterLocationWithUnregisteredMobile** - Non-existent user
9. ✅ **testRegisterLocationExceedsMaxLocations** - Admin max limit (3)
10. ✅ **testRegisterLocationWithNonAdminUser** - Authorization check

**Endpoint**: `POST /api/admin/location/register`

### Admin Dashboard Endpoint (8 Test Cases)
1. ✅ **testGetAdminDashboardSuccess** - Fetch dashboard
2. ✅ **testGetAdminDashboardWithMultipleLocations** - Multiple locations
3. ✅ **testGetAdminDashboardWithMultipleCourts** - Multiple courts
4. ✅ **testGetAdminDashboardWithNonAdminUser** - Authorization
5. ✅ **testGetAdminDashboardWithUnregisteredMobile** - Non-existent user
6. ✅ **testGetAdminDashboardWithNoLocations** - Empty locations
7. ✅ **testGetAdminDashboardWithDifferentBookingStatuses** - Various statuses
8. ✅ **testGetAdminDashboardWithMissingMobileParam** - Missing parameter

**Endpoint**: `GET /api/admin/dashboard?mobile={mobile}`

## Test Framework & Tools

| Tool | Version | Purpose |
|------|---------|---------|
| JUnit 5 | Latest | Test framework |
| Mockito | 5.1.0 | Mocking library |
| Kotlin | 2.2.21 | Language |
| Spring Test | Latest | HTTP testing (MockMvc) |
| Jackson | Latest | JSON serialization |

## Test Structure

Each test follows the **Arrange-Act-Assert (AAA)** pattern:

```kotlin
@Test
fun testExample() {
    // Arrange: Setup test data and mocks
    val request = RegisterLocationRequest(...)
    val expectedResponse = ApiResponse(...)
    `when`(adminService.registerLocation(request)).thenReturn(expectedResponse)
    
    // Act: Execute the API call
    mockMvc.perform(
        post("/api/admin/location/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    )
    
    // Assert: Verify the response
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.success").value(true))
}
```

## Key Features

✅ **Comprehensive Coverage**
- Success paths
- Error scenarios
- Boundary values
- Authorization checks

✅ **Mockito Integration**
- AdminService is mocked
- Controller logic tested in isolation
- No database dependencies

✅ **JSON Response Validation**
- Uses Spring's jsonPath matchers
- Validates nested objects
- Checks arrays and lists

✅ **Descriptive Test Names**
- Test names clearly indicate what is tested
- @DisplayName provides human-readable descriptions
- Easy to identify failing tests

## Troubleshooting

### Issue: "Kotlin version not supported"
**Solution**: See `KOTLIN_VERSION_FIX.md` and invalidate IDE cache

### Issue: "AdminService not found"
**Solution**: Ensure Mockito dependencies are installed:
```powershell
.\mvnw dependency:resolve
```

### Issue: "Test fails with assertion error"
**Solution**: Check the actual vs expected values in test output

### Issue: "MockMvc not available"
**Solution**: Ensure Spring Test dependency is in pom.xml

## Best Practices Used

1. **Isolated Unit Tests** - Each test is independent
2. **Clear Naming** - Test names describe what they test
3. **Proper Mocking** - Only AdminService is mocked
4. **Comprehensive Assertions** - Multiple assertions per test
5. **Edge Case Coverage** - Boundary values are tested
6. **No Side Effects** - Tests don't affect each other

## Next Steps

After confirming tests pass:

1. ✅ Add integration tests for database interactions
2. ✅ Add end-to-end tests for full workflows
3. ✅ Add performance tests for high-load scenarios
4. ✅ Add security tests for authentication/authorization
5. ✅ Setup CI/CD pipeline for automated testing

## Support

For detailed test documentation, refer to: **TEST_DOCUMENTATION.md**

For Kotlin version issues, refer to: **KOTLIN_VERSION_FIX.md**

