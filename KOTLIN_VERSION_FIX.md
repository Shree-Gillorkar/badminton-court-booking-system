# Kotlin Version Compatibility Fix

## Issue
When trying to run the test case class, you received the error:
```
The selected Kotlin version (1.9.21) does not support JDK versions newer than 25. Please upgrade to at least Kotlin 2.1.10.
```

## Root Cause
The IDE configuration file (`.idea/kotlinc.xml`) had Kotlin version 1.9.21, while the project's `pom.xml` had Kotlin version 2.2.21. This mismatch caused the IDE to use the older Kotlin compiler which doesn't support your JDK version.

## Solution Applied
Updated the Kotlin version in `.idea/kotlinc.xml` from `1.9.21` to `2.2.21` to match the Maven configuration in `pom.xml`.

### Changes Made
**File**: `.idea/kotlinc.xml`

**Before**:
```xml
<option name="version" value="1.9.21" />
```

**After**:
```xml
<option name="version" value="2.2.21" />
```

## Steps to Complete the Fix

### Option 1: Automatic (In IDE)
1. Close IntelliJ IDEA
2. Delete the `.idea` folder (or just the cache):
   ```
   C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking\.idea
   ```
3. Reopen the project in IntelliJ IDEA
4. The IDE will recreate the `.idea` folder with correct settings
5. Accept any Kotlin version upgrade prompts

### Option 2: Manual IDE Cache Invalidation
1. In IntelliJ IDEA, go to: **File → Invalidate Caches**
2. Select "Invalidate and Restart"
3. Choose "Just Restart" to refresh all caches
4. The IDE should now recognize Kotlin 2.2.21

### Option 3: Project Sync (Quick)
1. In IntelliJ IDEA, open the Maven panel
2. Right-click on the project root
3. Select "Maven → Reload Projects"
4. This will reload the project configuration

## Verification

After applying the fix, you should be able to:

1. ✅ Run individual test cases without Kotlin version errors
2. ✅ Run all test cases in AdminControllerTest
3. ✅ Build the project successfully

## Commands to Verify

Run the following commands in PowerShell:

```powershell
# Navigate to project
cd "C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking"

# Compile the project
.\mvnw clean compile -q

# Compile tests
.\mvnw test-compile -q

# Run all tests
.\mvnw test

# Run only AdminControllerTest
.\mvnw test -Dtest=AdminControllerTest
```

## Additional Notes

- **Kotlin 2.2.21** supports JDK versions 8 through 25+
- The pom.xml was already correctly configured with Kotlin 2.2.21
- This fix aligns the IDE configuration with the Maven build configuration
- No code changes were necessary; this was purely a configuration alignment issue

## Summary of Test Cases

Your AdminControllerTest now contains **18 comprehensive test cases**:
- **10 test cases** for the `/api/admin/location/register` endpoint
- **8 test cases** for the `/api/admin/dashboard` endpoint

All tests validate:
- ✅ Success scenarios
- ✅ Input validation
- ✅ Business rule enforcement  
- ✅ Authorization checks
- ✅ Edge cases
- ✅ Error handling

