# Verify Dashboard Counts - Step by Step Guide

## Problem
The HR Dashboard shows "0" for Total Candidates and Total Panelists even though data might exist.

## Solution Steps

### Step 1: Verify Backend is Running
1. Open a terminal in the project root
2. Navigate to backend:
   ```powershell
   cd backend
   ```
3. Start the backend:
   ```powershell
   mvn spring-boot:run
   ```
4. Wait for the message: "Started LoginApplication"
5. Backend should be running on: http://localhost:8081

### Step 2: Check Database Has Data

#### Option A: Using H2 Console (if using H2 database)
1. Open browser: http://localhost:8081/h2-console
2. Login with credentials from application.properties
3. Run these queries:
   ```sql
   SELECT COUNT(*) as total_candidates FROM candidates;
   SELECT COUNT(*) as total_panelists FROM panelists;
   ```

#### Option B: Check Backend Logs
When you login as HR, the backend logs should show:
```
=== HR Dashboard Request ===
HR ID: X
Total Candidates: Y
Total Panelists: Z
===========================
```

### Step 3: Test API Directly

1. First, login to get a token:
   ```powershell
   $response = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"hr1","password":"password123"}'
   $token = $response.token
   $userId = $response.user.id
   ```

2. Then call the dashboard API:
   ```powershell
   $headers = @{
       "Authorization" = "Bearer $token"
       "Content-Type" = "application/json"
   }
   $dashboard = Invoke-RestMethod -Uri "http://localhost:8081/api/hr/$userId/dashboard" -Method GET -Headers $headers
   Write-Host "Total Candidates: $($dashboard.dashboard.totalCandidates)"
   Write-Host "Total Panelists: $($dashboard.dashboard.totalPanelists)"
   ```

### Step 4: Add Sample Data (If Counts are 0)

If the database is empty, add sample data:

#### Add a Candidate:
1. Go to HR Dashboard
2. Click "Add New Candidate" tab
3. Fill in the form:
   - Name: John Doe
   - Email: john@example.com
   - Phone: 1234567890
   - Position: Java Developer
   - Username: john_doe
   - Password: Password@123
4. Click "Create Candidate"

#### Add a Panelist:
1. Go to HR Dashboard
2. Click "Add New Panelist" tab
3. Fill in the form:
   - Email: panelist@example.com
   - Username: panelist1
   - Password: Password@123
   - Specialization: Java
   - Experience: 5 years
4. Click "Create Panelist"

### Step 5: Verify Frontend is Fetching Data

1. Open browser console (F12)
2. Go to HR Dashboard Home tab
3. Look for these console logs:
   ```
   Fetching HR dashboard data...
   User ID: X
   Token exists: true
   Response status: 200
   Response ok: true
   Dashboard data received: {success: true, dashboard: {...}}
   Dashboard loaded successfully
   Total Candidates: X
   Total Panelists: Y
   ```

### Step 6: Check Frontend Display

The Home tab should show:
```
📊 Dashboard Overview

┌─────────────────────────────┐  ┌─────────────────────────────┐
│  👥                         │  │  🎯                         │
│  TOTAL CANDIDATES           │  │  TOTAL PANELISTS            │
│  X                          │  │  Y                          │
└─────────────────────────────┘  └─────────────────────────────┘
```

## Common Issues and Fixes

### Issue 1: Backend Not Running
**Symptom**: Frontend shows "Failed to fetch dashboard data"
**Fix**: Start the backend server

### Issue 2: Database is Empty
**Symptom**: Counts show "0"
**Fix**: Add sample candidates and panelists using the forms

### Issue 3: Token Expired
**Symptom**: "Authentication failed" error
**Fix**: Logout and login again

### Issue 4: Wrong User Role
**Symptom**: "Access denied" or "Invalid HR ID"
**Fix**: Make sure you're logged in as an HR user, not Candidate or Panelist

### Issue 5: CORS Error
**Symptom**: Console shows CORS policy error
**Fix**: Backend CORS is already configured for localhost:3000

### Issue 6: Data Not Updating
**Symptom**: Added data but count still shows old value
**Fix**: 
1. Refresh the page (F5)
2. Or switch to another tab and back to Home tab
3. The `useEffect` should re-fetch data

## Debugging Checklist

- [ ] Backend is running on port 8081
- [ ] Frontend is running on port 3000
- [ ] Logged in as HR user
- [ ] Database has at least 1 candidate
- [ ] Database has at least 1 panelist
- [ ] Browser console shows no errors
- [ ] Console logs show correct counts
- [ ] API response includes totalCandidates and totalPanelists

## Expected Console Output

When everything works correctly, you should see:

```javascript
// In browser console:
Fetching HR dashboard data...
User ID: 1
Token exists: true
Response status: 200
Response ok: true
Dashboard data received: {
  success: true,
  dashboard: {
    totalCandidates: 5,
    totalPanelists: 3,
    candidates: [...],
    panelists: [...],
    // ... other data
  },
  timestamp: 1234567890
}
Dashboard loaded successfully
Total Candidates: 5
Total Panelists: 3
```

## Quick Test Script

Save this as `test-dashboard.ps1` and run it:

```powershell
# Test Dashboard API
Write-Host "Testing HR Dashboard API..." -ForegroundColor Cyan

# 1. Login
Write-Host "`n1. Logging in as HR..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "hr1"
        password = "password123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.token
    $userId = $loginResponse.user.id
    Write-Host "✓ Login successful! User ID: $userId" -ForegroundColor Green
} catch {
    Write-Host "✗ Login failed: $_" -ForegroundColor Red
    exit
}

# 2. Get Dashboard Data
Write-Host "`n2. Fetching dashboard data..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }

    $dashboardResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/hr/$userId/dashboard" `
        -Method GET `
        -Headers $headers

    if ($dashboardResponse.success) {
        Write-Host "✓ Dashboard data fetched successfully!" -ForegroundColor Green
        Write-Host "`nStatistics:" -ForegroundColor Cyan
        Write-Host "  Total Candidates: $($dashboardResponse.dashboard.totalCandidates)" -ForegroundColor White
        Write-Host "  Total Panelists: $($dashboardResponse.dashboard.totalPanelists)" -ForegroundColor White
        
        if ($dashboardResponse.dashboard.totalCandidates -eq 0) {
            Write-Host "`n⚠ Warning: No candidates in database. Add some candidates first!" -ForegroundColor Yellow
        }
        if ($dashboardResponse.dashboard.totalPanelists -eq 0) {
            Write-Host "⚠ Warning: No panelists in database. Add some panelists first!" -ForegroundColor Yellow
        }
    } else {
        Write-Host "✗ Dashboard fetch failed: $($dashboardResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Dashboard fetch error: $_" -ForegroundColor Red
}

Write-Host "`nTest complete!" -ForegroundColor Cyan
```

Run it with:
```powershell
.\test-dashboard.ps1
```

## Next Steps

Once you verify the counts are working:
1. The frontend will automatically display them
2. No additional code changes needed
3. The implementation is complete!

---

**Made with ❤️ by Bob**