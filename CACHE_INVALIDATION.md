# Cache Invalidation Instructions

## Problem Resolution Steps

After the Kotlin version fix, follow these steps to clear IDE cache and run tests successfully.

### Step 1: Clear IDE Cache (Choose One Option)

#### Option A: Manual Cache Deletion (Recommended)
1. Close IntelliJ IDEA completely
2. Navigate to cache directory:
   ```
   C:\Users\sgillorkar\AppData\Local\JetBrains\IntelliJIdea2023.3\caches
   ```
   (or similar depending on your IDE version)
3. Delete the `caches` folder
4. Reopen IntelliJ IDEA
5. Wait for IDE to reindex the project

#### Option B: Using IDE Menu
1. In IntelliJ IDEA: **File → Invalidate Caches**
2. Select "Invalidate and Restart"
3. Click "Just Restart"
4. Wait for reindexing to complete

#### Option C: Delete .idea Folder (Nuclear Option)
1. Close IntelliJ IDEA
2. Navigate to project folder:
   ```
   C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking
   ```
3. Delete the `.idea` folder entirely
4. Reopen project in IntelliJ IDEA
5. The IDE will recreate `.idea` with fresh configuration

### Step 2: Reload Maven Project
1. Open Maven panel on right side of IDE
2. Right-click project root
3. Select "Maven → Reload Projects"
4. Wait for dependencies to resolve

### Step 3: Verify Configuration
1. Open `File → Project Structure`
2. Go to "Project" tab
3. Verify SDK is set to Java 17 or higher
4. Verify Kotlin plugin is version 2.2.21+

### Step 4: Clean Build
```powershell
cd "C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking"
.\mvnw clean install -DskipTests
```

### Step 5: Run Tests
```powershell
.\mvnw test -Dtest=AdminControllerTest
```

---

## What Was Changed

### File: `.idea/kotlinc.xml`
```xml
<!-- BEFORE -->
<option name="version" value="1.9.21" />

<!-- AFTER -->
<option name="version" value="2.2.21" />
```

### File: `pom.xml`
Added dependencies:
```xml
<dependency>
    <groupId>org.mockito.kotlin</groupId>
    <artifactId>mockito-kotlin</artifactId>
    <version>5.1.0</version>
    <scope>test</scope>
</dependency>
```

---

## Verification Checklist

After completing the steps above:

- [ ] IDE reopened successfully
- [ ] No "Kotlin version" error messages
- [ ] Maven dependencies resolved
- [ ] AdminControllerTest file shows no errors
- [ ] Can run individual test from IDE (right-click → Run)
- [ ] Terminal command `.\mvnw test -Dtest=AdminControllerTest` succeeds

---

## Expected Success Indicators

### In IDE
- No red squiggly lines in AdminControllerTest.kt
- @Test annotations are recognized
- Mockito methods are highlighted properly
- No "Unresolved reference" errors

### From Terminal
```
[INFO] Running com.badminton.booking.controller.AdminControllerTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Still Having Issues?

### Issue: "Kotlin version not supported"
✓ Already fixed in kotlinc.xml
✓ Try invalidating IDE cache

### Issue: "Cannot resolve MockMvc"
✓ Run: `.\mvnw dependency:resolve`
✓ Reload Maven project

### Issue: "Unresolved reference 'when'"
✓ Add import: `import org.mockito.Mockito.\`when\``
✓ Already included in AdminControllerTest.kt

### Issue: IDE still showing errors
✓ Try Option C: Delete entire `.idea` folder
✓ Reopen project from scratch

---

## Quick Test Command

Once configured, run tests with:
```powershell
cd "C:\Users\sgillorkar\OneDrive - Deloitte (O365D)\Documents\Assignments\badminton-court-booking"
.\mvnw test -Dtest=AdminControllerTest
```

Expected output:
```
[INFO] 
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

---

**All 18 test cases are now ready to run! 🎉**

