# HR Dashboard Real-Time Updates Guide

## Overview
The HR Dashboard now features real-time updates that automatically refresh when new candidates or panelists login or register. This provides HR with immediate visibility into all users in the system.

## Features Implemented

### 1. **Auto-Refresh Functionality**
- Dashboard automatically polls for updates every 30 seconds
- Can be toggled on/off by the user
- Shows last update timestamp
- Minimal performance impact

### 2. **Real-Time Notifications**
- Visual notification banner when new users are detected
- Shows count of new panelists and candidates
- Auto-dismisses after 5 seconds
- Animated bell icon for attention

### 3. **Separate Tabs for Users**
- **All Panelists Tab**: Complete list of all panelists with details
- **All Candidates Tab**: Complete list of all candidates with details
- Each tab has a manual refresh button
- Data loads on-demand when tab is clicked

### 4. **Visual Indicators**
- "NEW" badge for users registered in last 24 hours
- Highlighted rows for new users
- Animated pulse effect on new badges
- Color-coded status badges

### 5. **Comprehensive User Information**

#### Panelist Details:
- ID, Username, Email
- Specialization and Experience
- Active/Inactive status
- Number of assigned candidates
- Total and completed interviews
- Registration date

#### Candidate Details:
- ID, Name, Email, Phone
- Position and Status
- Experience and Skills
- JD Details
- Joining Date
- Old and New CTC
- Employment Type and Location
- Assigned Panelist
- Registration date

## Backend Endpoints

### 1. Get All Panelists with Details
```
GET /api/hr/{hrId}/all-panelists
```
Returns detailed information about all panelists including their interview statistics.

### 2. Get All Candidates with Details
```
GET /api/hr/{hrId}/all-candidates
```
Returns detailed information about all candidates including their assigned panelist and interview status.

### 3. Get Dashboard Updates (Polling)
```
GET /api/hr/{hrId}/dashboard-updates
```
Returns real-time counts and new user statistics for polling.

**Response:**
```json
{
  "success": true,
  "updates": {
    "totalPanelists": 5,
    "totalCandidates": 10,
    "activePanelists": 4,
    "newPanelists": 2,
    "newCandidates": 3,
    "timestamp": "2026-05-14T17:00:00"
  }
}
```

## Frontend Components

### Auto-Refresh Toggle
Located in the header, allows users to:
- Enable/disable auto-refresh
- See last update time
- Control polling behavior

### New Users Notification
Appears at the top when new users are detected:
- Gradient background (pink to red)
- Animated bell icon
- Clear message with counts
- Auto-dismisses after 5 seconds

### User Tables
Both panelist and candidate tables feature:
- Sortable columns
- Hover effects
- Responsive design
- Scroll for overflow
- Color-coded status badges

## How It Works

### 1. Initial Load
When HR logs in:
1. Dashboard loads with current statistics
2. Auto-refresh is enabled by default
3. Polling starts immediately

### 2. Polling Mechanism
Every 30 seconds:
1. Frontend calls `/dashboard-updates` endpoint
2. Compares new counts with current counts
3. If changes detected, shows notification
4. Automatically reloads dashboard data
5. Updates last refresh timestamp

### 3. New User Detection
Users are considered "new" if:
- Registered within last 24 hours
- Calculated using `LocalDateTime.now().minusDays(1)`
- Highlighted with "NEW" badge
- Row has subtle background color

### 4. Manual Refresh
Users can manually refresh:
- Click on tab to reload data
- Use refresh button in tab content
- Toggle auto-refresh off/on

## CSS Styling

### Key Animations
```css
/* Notification slide-in */
@keyframes slideIn {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* Bell ring animation */
@keyframes ring {
  0%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(-10deg); }
  20%, 40% { transform: rotate(10deg); }
}

/* New badge pulse */
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

/* Row highlight */
@keyframes highlight {
  0%, 100% { background: transparent; }
  50% { background: rgba(102, 126, 234, 0.2); }
}
```

### Color Scheme
- Primary: `#667eea` (Purple-blue)
- Secondary: `#764ba2` (Deep purple)
- Notification: `#f093fb` to `#f5576c` (Pink to red gradient)
- Success: `#2e7d32` (Green)
- Warning: `#f57c00` (Orange)
- Error: `#c62828` (Red)

## Usage Instructions

### For HR Users

1. **Login to HR Dashboard**
   - Use HR credentials
   - Dashboard loads automatically

2. **View Real-Time Updates**
   - Auto-refresh is ON by default
   - Watch for notification banner
   - Check last update time in header

3. **View All Panelists**
   - Click "All Panelists" tab
   - See complete list with statistics
   - Look for "NEW" badges on recent additions
   - Use refresh button to reload

4. **View All Candidates**
   - Click "All Candidates" tab
   - See complete list with all details
   - Check assigned panelists
   - Review interview status

5. **Control Auto-Refresh**
   - Toggle checkbox in header
   - Disable if you want static view
   - Enable to resume polling

### For Developers

1. **Modify Polling Interval**
   ```javascript
   // In HRDashboard.js, line ~23
   const interval = setInterval(() => {
     checkForUpdates();
   }, 30000); // Change 30000 to desired milliseconds
   ```

2. **Adjust "New" User Timeframe**
   ```java
   // In HRService.java, getDashboardUpdates method
   LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
   // Change minusDays(1) to desired duration
   ```

3. **Customize Notification Duration**
   ```javascript
   // In HRDashboard.js, checkForUpdates method
   setTimeout(() => {
     setNewUsersCount({ panelists: 0, candidates: 0 });
   }, 5000); // Change 5000 to desired milliseconds
   ```

## Testing

### Test Scenario 1: New Panelist Registration
1. Login as HR
2. In another browser/incognito, register new panelist
3. Wait up to 30 seconds
4. Notification should appear on HR dashboard
5. Click "All Panelists" tab
6. New panelist should have "NEW" badge

### Test Scenario 2: New Candidate Registration
1. Login as HR
2. In another browser/incognito, register new candidate
3. Wait up to 30 seconds
4. Notification should appear
5. Click "All Candidates" tab
6. New candidate should be highlighted

### Test Scenario 3: Auto-Refresh Toggle
1. Login as HR
2. Disable auto-refresh toggle
3. Register new user in another browser
4. Wait 30+ seconds
5. No notification should appear
6. Enable auto-refresh
7. Notification should appear within 30 seconds

### Test Scenario 4: Manual Refresh
1. Login as HR
2. Click "All Panelists" tab
3. Register new panelist in another browser
4. Click refresh button in tab
5. New panelist should appear immediately

## Performance Considerations

### Polling Optimization
- 30-second interval balances real-time vs. server load
- Only fetches counts, not full data
- Full data loads only when tabs are clicked
- Minimal network overhead

### Memory Management
- Intervals are cleaned up on component unmount
- No memory leaks from polling
- Efficient state management

### Database Impact
- Lightweight queries for updates
- Indexed fields for fast counting
- No N+1 query problems
- Proper use of JPA relationships

## Troubleshooting

### Issue: Notifications Not Appearing
**Solution:**
1. Check auto-refresh is enabled
2. Verify backend is running on port 8081
3. Check browser console for errors
4. Ensure JWT token is valid

### Issue: "NEW" Badges Not Showing
**Solution:**
1. Check user registration timestamp
2. Verify 24-hour calculation
3. Ensure `createdAt` field is populated
4. Check timezone settings

### Issue: Polling Stops Working
**Solution:**
1. Check browser console for errors
2. Verify network connectivity
3. Ensure backend endpoints are accessible
4. Check JWT token expiration

### Issue: Data Not Refreshing
**Solution:**
1. Click manual refresh button
2. Toggle auto-refresh off/on
3. Reload page
4. Check backend logs for errors

## Security Considerations

1. **Authentication Required**
   - All endpoints require valid JWT token
   - HR role verification on backend
   - Token included in all requests

2. **Authorization Checks**
   - HR can only see their assigned users
   - Panelists filtered by assigned HR
   - Candidates filtered by HR ownership

3. **Data Privacy**
   - Sensitive data not exposed in polling endpoint
   - Full details only on authenticated requests
   - Proper CORS configuration

## Future Enhancements

1. **WebSocket Integration**
   - Replace polling with WebSocket for true real-time
   - Instant notifications without delay
   - Reduced server load

2. **Advanced Filtering**
   - Filter by status, date range
   - Search functionality
   - Export to CSV/Excel

3. **Detailed Analytics**
   - Charts and graphs
   - Trend analysis
   - Performance metrics

4. **Push Notifications**
   - Browser notifications
   - Email alerts
   - SMS notifications

## Conclusion

The HR Dashboard now provides real-time visibility into all panelists and candidates, with automatic updates and visual indicators for new users. This enhances HR's ability to manage the recruitment process efficiently and stay informed about system activity.

---

**Made with Bob** 🤖