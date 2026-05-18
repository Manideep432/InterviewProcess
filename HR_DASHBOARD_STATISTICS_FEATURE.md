# HR Dashboard Statistics Feature

## Overview
Enhanced the HR Dashboard home page to display comprehensive statistics including:
1. **Total Candidates** - All candidates in the system
2. **Panelists Assigned by HR** - Panelists specifically assigned by this HR (highlighted)
3. **Total Interviews Scheduled** - All interviews scheduled by this HR (highlighted)
4. **Total Panelists** - All panelists in the system

## Features Implemented

### Backend Changes

#### 1. HRService.java Updates
**File**: `backend/src/main/java/com/login/service/HRService.java`

Added new statistics to the `getHRDashboard()` method:

```java
// Get panelists assigned by THIS HR specifically
List<Panelist> hrAssignedPanelists = panelistRepository.findByAssignedHr(hr);
dashboard.put("totalPanelistsAssignedByHR", hrAssignedPanelists.size());
dashboard.put("activePanelistsAssignedByHR", hrAssignedPanelists.stream().filter(Panelist::isActive).count());

// Get all interviews scheduled by THIS HR
List<Interview> hrInterviews = interviewRepository.findByHrId(hrId);
dashboard.put("interviews", hrInterviews);
dashboard.put("totalInterviews", hrInterviews.size());

// Interview statistics by status
Map<String, Long> interviewsByStatus = new HashMap<>();
interviewsByStatus.put("SCHEDULED", hrInterviews.stream().filter(i -> "SCHEDULED".equals(i.getStatus().toString())).count());
interviewsByStatus.put("COMPLETED", hrInterviews.stream().filter(i -> "COMPLETED".equals(i.getStatus().toString())).count());
interviewsByStatus.put("CANCELLED", hrInterviews.stream().filter(i -> "CANCELLED".equals(i.getStatus().toString())).count());
dashboard.put("interviewsByStatus", interviewsByStatus);
```

**New Dashboard Data Fields**:
- `totalPanelistsAssignedByHR` - Count of panelists assigned by this HR
- `activePanelistsAssignedByHR` - Count of active panelists assigned by this HR
- `totalInterviews` - Count of interviews scheduled by this HR
- `interviewsByStatus` - Breakdown of interviews by status (SCHEDULED, COMPLETED, CANCELLED)

### Frontend Changes

#### 2. HRDashboard.js Updates
**File**: `frontend/src/components/HRDashboard.js`

Updated the home page statistics cards to display HR-specific metrics:

```javascript
<div className="dashboard-stats">
  {/* Total Candidates */}
  <div className="stat-card-large">
    <div className="stat-icon-large">👥</div>
    <div className="stat-content">
      <div className="stat-number-large">{dashboardData.totalCandidates || 0}</div>
      <div className="stat-label-large">Total Candidates</div>
      <div className="stat-sublabel">All candidates in system</div>
    </div>
  </div>
  
  {/* Panelists Assigned by HR - HIGHLIGHTED */}
  <div className="stat-card-large stat-card-highlight">
    <div className="stat-icon-large">👨‍💼</div>
    <div className="stat-content">
      <div className="stat-number-large">{dashboardData.totalPanelistsAssignedByHR || 0}</div>
      <div className="stat-label-large">Panelists Assigned by You</div>
      <div className="stat-sublabel">
        {dashboardData.activePanelistsAssignedByHR || 0} active
      </div>
    </div>
  </div>
  
  {/* Total Interviews - HIGHLIGHTED */}
  <div className="stat-card-large stat-card-highlight">
    <div className="stat-icon-large">📅</div>
    <div className="stat-content">
      <div className="stat-number-large">{dashboardData.totalInterviews || 0}</div>
      <div className="stat-label-large">Total Interviews Scheduled</div>
      <div className="stat-sublabel">
        {dashboardData.interviewsByStatus ? 
          `${dashboardData.interviewsByStatus.SCHEDULED || 0} scheduled, ${dashboardData.interviewsByStatus.COMPLETED || 0} completed` 
          : 'By you'}
      </div>
    </div>
  </div>
  
  {/* Total Panelists in System */}
  <div className="stat-card-large">
    <div className="stat-icon-large">🌐</div>
    <div className="stat-content">
      <div className="stat-number-large">{dashboardData.totalPanelists || 0}</div>
      <div className="stat-label-large">Total Panelists</div>
      <div className="stat-sublabel">All panelists in system</div>
    </div>
  </div>
</div>
```

#### 3. HRDashboard.css Updates
**File**: `frontend/src/components/HRDashboard.css`

Added new CSS classes for enhanced styling:

```css
/* Sublabel for additional information */
.stat-sublabel {
  font-size: 0.85rem;
  font-weight: 400;
  opacity: 0.85;
  margin-top: 5px;
  font-style: italic;
}

/* Highlighted stat cards for HR-specific metrics */
.stat-card-highlight {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 8px 25px rgba(245, 87, 108, 0.3);
}

.stat-card-highlight:hover {
  box-shadow: 0 12px 35px rgba(245, 87, 108, 0.4);
}
```

## Visual Design

### Statistics Cards Layout
The dashboard displays 4 main statistics cards in a responsive grid:

1. **Total Candidates** (Blue gradient)
   - Shows total number of candidates in the system
   - Subtitle: "All candidates in system"

2. **Panelists Assigned by You** (Pink gradient - HIGHLIGHTED)
   - Shows panelists specifically assigned by this HR
   - Subtitle: Shows count of active panelists
   - Visually distinct with pink/red gradient

3. **Total Interviews Scheduled** (Pink gradient - HIGHLIGHTED)
   - Shows total interviews scheduled by this HR
   - Subtitle: Shows breakdown (scheduled vs completed)
   - Visually distinct with pink/red gradient

4. **Total Panelists** (Blue gradient)
   - Shows all panelists in the system
   - Subtitle: "All panelists in system"

### Color Scheme
- **Standard cards**: Blue-purple gradient (#667eea to #764ba2)
- **Highlighted cards**: Pink-red gradient (#f093fb to #f5576c)
- **Hover effects**: Enhanced shadow and slight lift animation

## API Endpoints Used

### GET /api/hr/{hrId}/dashboard
Returns comprehensive dashboard data including:
- `totalCandidates`: Total candidates in system
- `totalPanelists`: Total panelists in system
- `totalPanelistsAssignedByHR`: Panelists assigned by this HR
- `activePanelistsAssignedByHR`: Active panelists assigned by this HR
- `totalInterviews`: Interviews scheduled by this HR
- `interviewsByStatus`: Interview breakdown by status
- `candidatesByStatus`: Candidate breakdown by status
- `dashboardRecords`: Detailed candidate information

## Testing Instructions

### 1. Start the Backend
```bash
cd backend
mvn spring-boot:run
```

### 2. Start the Frontend
```bash
cd frontend
npm start
```

### 3. Login as HR
- Navigate to http://localhost:3000
- Login with HR credentials
- You should see the enhanced dashboard

### 4. Verify Statistics
Check that the dashboard displays:
- ✅ Total Candidates count
- ✅ Panelists Assigned by You (highlighted in pink)
- ✅ Total Interviews Scheduled (highlighted in pink)
- ✅ Total Panelists count
- ✅ Sublabels showing additional details
- ✅ Proper color coding (blue for system-wide, pink for HR-specific)

### 5. Test Functionality
1. **Add a new panelist** → Verify "Panelists Assigned by You" increases
2. **Schedule an interview** → Verify "Total Interviews Scheduled" increases
3. **Add a new candidate** → Verify "Total Candidates" increases
4. **Check sublabels** → Verify they show correct active counts and status breakdowns

## Key Benefits

1. **Clear Visibility**: HR can immediately see their specific contributions
2. **Visual Distinction**: Highlighted cards make HR-specific metrics stand out
3. **Detailed Information**: Sublabels provide additional context
4. **Real-time Updates**: Statistics update automatically when data changes
5. **Professional Design**: Modern gradient cards with smooth animations

## Database Requirements

The feature uses existing database tables:
- `users` - For HR and panelist information
- `panelists` - For panelist assignments
- `candidates` - For candidate information
- `interviews` - For interview scheduling

No database schema changes required.

## Browser Compatibility

Tested and working on:
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Future Enhancements

Potential improvements:
1. Add charts/graphs for visual representation
2. Add date range filters for statistics
3. Add export functionality for reports
4. Add comparison with previous periods
5. Add drill-down capability for detailed views

## Troubleshooting

### Statistics not showing
- Check backend logs for errors
- Verify HR is logged in correctly
- Check browser console for API errors
- Ensure database has data

### Incorrect counts
- Verify database relationships are correct
- Check that HR ID is being passed correctly
- Review backend logs for query issues

## Author
Bob - Software Engineer

## Last Updated
2026-05-18