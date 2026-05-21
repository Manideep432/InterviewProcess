# Interview Auto-Completion Feature Guide

## Overview
This feature automatically marks interviews as "COMPLETED" once their scheduled end time has passed. It includes both automatic background processing and manual refresh capabilities.

## Features Implemented

### 1. Backend Scheduled Service
**File:** `backend/src/main/java/com/login/service/InterviewSchedulerService.java`

- **Automatic Check:** Runs every 60 seconds (1 minute)
- **Logic:** 
  - Finds all interviews with status "SCHEDULED"
  - Checks if interview date is in the past OR if it's today and end time has passed
  - Automatically updates status to "COMPLETED"
- **Logging:** Logs all auto-completed interviews for tracking

### 2. Manual Trigger API Endpoint
**Endpoint:** `POST /api/interviews/update-expired`

- Allows manual triggering of interview status updates
- Returns count of updated interviews
- Can be called from frontend or external tools

### 3. Frontend Auto-Refresh
**File:** `frontend/src/components/HRDashboard.js`

**Features:**
- Auto-refreshes interview list every 60 seconds when on "Interviews" tab
- Shows "Last updated" timestamp
- Manual "Refresh Now" button for immediate updates
- Visual indicator showing auto-refresh is active

## How It Works

### Backend Process Flow
```
Every 60 seconds:
1. Fetch all SCHEDULED interviews
2. For each interview:
   - Check if interview date < today
   - OR if interview date = today AND current time > end time
3. If condition met:
   - Update status to COMPLETED
   - Save to database
   - Log the update
```

### Frontend Process Flow
```
When on Interviews tab:
1. Load interviews immediately
2. Set up 60-second interval timer
3. Auto-refresh interviews every 60 seconds
4. Update "Last updated" timestamp
5. Clean up timer when leaving tab
```

## Testing the Feature

### Test Scenario 1: Past Interview
1. Create an interview with date in the past (e.g., yesterday)
2. Set status as "SCHEDULED"
3. Wait 60 seconds or click "Refresh Now"
4. Interview should automatically change to "COMPLETED"

### Test Scenario 2: Today's Completed Interview
1. Create an interview for today
2. Set end time to a time that has already passed (e.g., 2 hours ago)
3. Set status as "SCHEDULED"
4. Wait 60 seconds or click "Refresh Now"
5. Interview should automatically change to "COMPLETED"

### Test Scenario 3: Future Interview
1. Create an interview for tomorrow
2. Set status as "SCHEDULED"
3. Wait and refresh multiple times
4. Interview should remain "SCHEDULED" (not auto-completed)

### Test Scenario 4: Today's Ongoing Interview
1. Create an interview for today
2. Set end time to 2 hours from now
3. Set status as "SCHEDULED"
4. Interview should remain "SCHEDULED" until end time passes

## Manual Testing Steps

### Step 1: Start the Backend
```bash
cd backend
mvn spring-boot:run
```

### Step 2: Check Logs
Look for scheduled task logs:
```
Running scheduled interview status check at [timestamp]
Auto-completed interview ID: X for candidate: [name]
Auto-completed N interview(s)
```

### Step 3: Test Frontend
1. Login as HR user
2. Navigate to "Interviews" tab
3. Observe:
   - "Last updated" timestamp
   - "Auto-refreshes every 60 seconds" message
   - "Refresh Now" button
4. Create test interviews with past dates
5. Click "Refresh Now" or wait 60 seconds
6. Verify status changes to "COMPLETED"

### Step 4: Test Manual API Endpoint
```bash
# Using curl
curl -X POST http://localhost:8081/api/interviews/update-expired \
  -H "Authorization: Bearer YOUR_TOKEN"

# Expected response:
{
  "success": true,
  "message": "Interview statuses updated successfully",
  "updatedCount": 2
}
```

## Configuration

### Adjust Auto-Check Interval
Edit `InterviewSchedulerService.java`:
```java
@Scheduled(fixedRate = 60000) // Change 60000 to desired milliseconds
```

### Adjust Frontend Refresh Interval
Edit `HRDashboard.js`:
```javascript
intervalId = setInterval(() => {
  fetchAllInterviews();
}, 60000); // Change 60000 to desired milliseconds
```

## Database Impact

### Queries Executed
- Every 60 seconds: `SELECT * FROM interviews WHERE status = 'SCHEDULED'`
- For each expired interview: `UPDATE interviews SET status = 'COMPLETED' WHERE id = ?`

### Performance Considerations
- Minimal impact with < 1000 scheduled interviews
- Indexed on `status` column for fast queries
- Transaction-based updates ensure data consistency

## Troubleshooting

### Issue: Interviews Not Auto-Completing
**Check:**
1. Backend logs for scheduler execution
2. Interview date and time values in database
3. System timezone settings
4. `@EnableScheduling` annotation in `LoginApplication.java`

### Issue: Frontend Not Refreshing
**Check:**
1. Browser console for errors
2. Network tab for API calls
3. Active tab is "Interviews"
4. No JavaScript errors blocking execution

### Issue: Wrong Timezone
**Solution:**
- Backend uses server timezone
- Ensure server timezone matches expected timezone
- Consider adding timezone configuration in `application.properties`

## API Reference

### Update Expired Interviews
```
POST /api/interviews/update-expired
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Interview statuses updated successfully",
  "updatedCount": 5
}
```

### Get All Interviews (HR)
```
GET /api/interviews/hr/{hrId}
Authorization: Bearer {token}

Response:
{
  "success": true,
  "interviews": [...],
  "count": 10
}
```

## Benefits

1. **Automatic Status Management:** No manual intervention needed
2. **Real-time Updates:** HR sees current status within 60 seconds
3. **Audit Trail:** All auto-completions are logged
4. **Manual Override:** Can trigger immediate update if needed
5. **User-Friendly:** Visual indicators and timestamps for transparency

## Future Enhancements

1. Email notifications when interviews auto-complete
2. Configurable check intervals per HR user
3. Bulk status update for multiple interviews
4. Dashboard widget showing recently completed interviews
5. Analytics on interview completion patterns

## Notes

- The scheduler starts automatically when the application starts
- No additional configuration required
- Works with existing interview data
- Does not affect other interview statuses (IN_PROGRESS, CANCELLED, etc.)
- Only updates SCHEDULED interviews that have passed their end time

---

**Created:** 2026-05-20  
**Author:** Bob  
**Version:** 1.0