# Fix "Failed to Fetch" Error - Complete Guide

## Problem
You're seeing "Error: Failed to fetch" in the Manage Candidates, Add New Candidates, and other tabs in the HR Dashboard.

## Root Cause
The frontend cannot connect to the backend API, usually because:
1. Backend is not running
2. CORS configuration issues
3. Wrong API URL
4. MySQL database not running

## Solution Applied

### 1. Enhanced CORS Configuration
✅ Created `CorsConfig.java` - Global CORS filter
✅ Updated `SecurityConfig.java` - Better CORS settings
✅ Updated `CandidateController.java` - Proper CORS annotations
✅ Updated `HRController.java` - Already has proper CORS

### 2. Steps to Fix

#### Step 1: Start MySQL Database
```bash
# Make sure MySQL is running on port 3306
# Windows: Open Services and start MySQL80 service
# Or use MySQL Workbench to start the server
```

#### Step 2: Restart Backend
```bash
# Open a new terminal in the project root directory
cd backend
mvn clean install
mvn spring-boot:run
```

**Wait for this message:**
```
Started LoginApplication in X.XXX seconds
```

#### Step 3: Verify Backend is Running
Open browser and go to: `http://localhost:8081/api/auth/test`

If you see a response, backend is running!

#### Step 4: Start Frontend
```bash
# Open another terminal
cd frontend
npm start
```

#### Step 5: Clear Browser Cache
1. Open Developer Tools (F12)
2. Right-click the refresh button
3. Select "Empty Cache and Hard Reload"

### 3. Verify the Fix

1. **Login as HR**
   - Username: `hr@company.com`
   - Password: `Hr@123456`

2. **Check Each Tab:**
   - ✅ Home - Should show dashboard statistics
   - ✅ Manage Candidates - Should show candidate list
   - ✅ Add New Candidate - Should show form
   - ✅ Add New Panelist - Should show form
   - ✅ Manage Panelists - Should show panelist list
   - ✅ Interviews - Should show interview list
   - ✅ Candidate Feedback - Should show feedback list

### 4. Common Issues & Solutions

#### Issue: "Failed to fetch" still appears
**Solution:**
1. Check backend console for errors
2. Verify MySQL is running
3. Check `application.properties` database credentials
4. Ensure port 8081 is not blocked by firewall

#### Issue: CORS errors in browser console
**Solution:**
1. Backend restart required after CORS changes
2. Clear browser cache
3. Check browser console for specific CORS error

#### Issue: 401 Unauthorized
**Solution:**
1. Token expired - logout and login again
2. Check if JWT token is in localStorage (F12 > Application > Local Storage)

#### Issue: Backend won't start
**Solution:**
1. Check if MySQL is running
2. Verify database credentials in `application.properties`
3. Check if port 8081 is already in use
4. Run `mvn clean install` first

### 5. Database Configuration

Current settings in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logindb
spring.datasource.username=root
spring.datasource.password=root
```

If your MySQL has different credentials, update these values.

### 6. API Endpoints Being Called

The HR Dashboard calls these endpoints:
- `GET /api/hr/{hrId}/dashboard` - Dashboard data
- `GET /api/hr/{hrId}/my-candidates` - Candidate list
- `GET /api/hr/{hrId}/all-panelists` - Panelist list (if using all-panelists)
- `GET /api/panelists/hr/{hrId}` - Panelists created by HR
- `GET /api/interviews/hr/{hrId}` - All interviews
- `GET /api/interview-feedback/all` - All feedback

All these endpoints now have proper CORS configuration.

### 7. Testing the Fix

**Backend Test:**
```bash
# Test if backend is responding
curl http://localhost:8081/api/auth/test
```

**Frontend Test:**
1. Open browser console (F12)
2. Go to Network tab
3. Login and navigate to tabs
4. Check if API calls are successful (Status 200)

### 8. What Was Changed

**New File:**
- `backend/src/main/java/com/login/config/CorsConfig.java` - Global CORS filter

**Modified Files:**
- `backend/src/main/java/com/login/security/SecurityConfig.java` - Enhanced CORS
- `backend/src/main/java/com/login/controller/CandidateController.java` - Better CORS annotations

**Key Changes:**
- Added `allowedOriginPatterns` instead of `allowedOrigins` for better compatibility
- Added support for both `localhost:3000` and `127.0.0.1:3000`
- Added all HTTP methods including PATCH
- Added exposed headers for better frontend access
- Created global CORS filter as backup

## Quick Fix Commands

```bash
# Terminal 1 - Backend
cd backend
mvn clean spring-boot:run

# Terminal 2 - Frontend (after backend starts)
cd frontend
npm start
```

## Success Indicators

✅ Backend console shows: "Started LoginApplication"
✅ Frontend loads without errors
✅ All tabs show data (not "Failed to fetch")
✅ No CORS errors in browser console
✅ API calls return 200 status

## Still Having Issues?

1. Check backend console for error messages
2. Check browser console (F12) for network errors
3. Verify MySQL is running and accessible
4. Ensure no firewall blocking ports 3000 or 8081
5. Try accessing `http://localhost:8081` directly in browser

---
**Made with ❤️ by Bob**