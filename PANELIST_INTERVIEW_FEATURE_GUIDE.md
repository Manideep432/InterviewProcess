# Panelist Interview Management Feature Guide

## Overview
This guide covers the new Panelist Interview Management feature that has been implemented in the Login Microservice Application.

## Features Implemented

### 1. Enhanced UI Components

#### Improved Login Buttons
- **Resend OTP Button**: Enhanced with green gradient, better hover effects, and improved styling
- **Back to Login Button**: Enhanced with blue gradient, better hover effects, and improved styling
- Both buttons now have:
  - Modern gradient backgrounds
  - Smooth animations
  - Better disabled states
  - Uppercase text with letter spacing
  - Enhanced shadows on hover

### 2. Panelist Dashboard

#### New Component: `PanelistDashboard.js`
A comprehensive dashboard specifically for users with the PANELIST role.

#### Dashboard Tabs

##### 🏠 HOME Tab
- **Dashboard Overview** with statistics cards:
  - Total Interviews
  - Upcoming Interviews
  - Completed Interviews
  - Cancelled Interviews
- **Quick Actions**:
  - Schedule New Interview
  - View All Interviews
  - Update Profile
- **Logout** button

##### 👤 Panelist Profile Tab
Displays panelist information:
- Username
- Email
- Specialization
- Experience (years)
- Expertise
- Status (Active/Inactive)
- Panelist ID

##### ➕ New Interview Tab
Form to schedule a new interview with fields:
- Candidate Name (required)
- Candidate Email (required)
- Interview Date (required)
- Interview Time (required)
- Position (required)
- Notes (optional)

##### 📊 Number of Interviews Tab
- Filter buttons: All, Upcoming, Completed, Cancelled
- Interview cards showing:
  - Candidate Name
  - Candidate Email
  - Position
  - Date and Time
  - Status badge
  - Notes (if any)
- Empty state with call-to-action

### 3. Backend Implementation

#### New Model: `Interview.java`
```java
@Entity
@Table(name = "interviews")
public class Interview {
    private Long id;
    private Long panelistId;
    private String candidateName;
    private String candidateEmail;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String position;
    private String notes;
    private InterviewStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Interview Statuses:**
- SCHEDULED
- COMPLETED
- CANCELLED
- RESCHEDULED

#### New Repository: `InterviewRepository.java`
Methods for data access:
- `findByPanelistId(Long panelistId)`
- `findByPanelistIdAndStatus(Long panelistId, InterviewStatus status)`
- `findByPanelistIdAndInterviewDate(Long panelistId, LocalDate date)`
- `findByCandidateEmail(String email)`
- `countByPanelistId(Long panelistId)`
- `countByPanelistIdAndStatus(Long panelistId, InterviewStatus status)`

#### New Service: `InterviewService.java`
Business logic methods:
- `scheduleInterview()` - Create new interview
- `getInterviewsByPanelist()` - Get all interviews for a panelist
- `getInterviewsByPanelistAndStatus()` - Filter by status
- `updateInterviewStatus()` - Change interview status
- `updateInterview()` - Update interview details
- `deleteInterview()` - Remove interview
- `cancelInterview()` - Cancel interview
- `completeInterview()` - Mark as completed
- `rescheduleInterview()` - Change date/time
- `getInterviewCountByPanelist()` - Get statistics

#### New Controller: `InterviewController.java`
REST API endpoints:

**POST Endpoints:**
- `POST /api/interviews/schedule` - Schedule new interview

**GET Endpoints:**
- `GET /api/interviews/panelist/{panelistId}` - Get all interviews
- `GET /api/interviews/panelist/{panelistId}/status/{status}` - Filter by status
- `GET /api/interviews/{id}` - Get specific interview
- `GET /api/interviews/panelist/{panelistId}/stats` - Get statistics
- `GET /api/interviews/all` - Get all interviews (admin)

**PUT Endpoints:**
- `PUT /api/interviews/{id}` - Update interview
- `PUT /api/interviews/{id}/status` - Update status
- `PUT /api/interviews/{id}/cancel` - Cancel interview
- `PUT /api/interviews/{id}/complete` - Complete interview
- `PUT /api/interviews/{id}/reschedule` - Reschedule interview

**DELETE Endpoints:**
- `DELETE /api/interviews/{id}` - Delete interview

### 4. Security Configuration
Updated `SecurityConfig.java` to allow authenticated access to:
- `/api/interviews/**` - All interview endpoints

### 5. Dashboard Routing
Updated `Dashboard.js` to route Panelist users to `PanelistDashboard` component.

## How to Use

### For Panelists

#### 1. Login as Panelist
- Register with role "PANELIST" or use existing panelist account
- Complete OTP verification
- You'll be redirected to the Panelist Dashboard

#### 2. View Dashboard Overview
- See statistics of your interviews
- Use quick action buttons for common tasks

#### 3. Schedule New Interview
- Click "New Interview" tab or "Schedule New Interview" button
- Fill in all required fields:
  - Candidate name and email
  - Interview date (must be today or future)
  - Interview time
  - Position
  - Optional notes
- Click "Schedule Interview"
- Interview will be created with SCHEDULED status

#### 4. View Interviews
- Click "Number of Interviews" tab
- Use filter buttons to view:
  - All interviews
  - Upcoming (SCHEDULED)
  - Completed
  - Cancelled
- Each interview card shows full details

#### 5. Update Profile
- Click "Panelist Profile" tab
- View your panelist information
- Contact HR to update specialization or expertise

## API Usage Examples

### Schedule Interview
```bash
POST http://localhost:8080/api/interviews/schedule
Authorization: Bearer <token>
Content-Type: application/json

{
  "panelistId": 1,
  "candidateName": "John Doe",
  "candidateEmail": "john@example.com",
  "interviewDate": "2026-05-20",
  "interviewTime": "14:30",
  "position": "Senior Software Engineer",
  "notes": "Technical round - focus on system design"
}
```

### Get Panelist Interviews
```bash
GET http://localhost:8080/api/interviews/panelist/1
Authorization: Bearer <token>
```

### Get Interview Statistics
```bash
GET http://localhost:8080/api/interviews/panelist/1/stats
Authorization: Bearer <token>
```

### Update Interview Status
```bash
PUT http://localhost:8080/api/interviews/5/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

### Cancel Interview
```bash
PUT http://localhost:8080/api/interviews/5/cancel
Authorization: Bearer <token>
```

## Database Schema

### interviews Table
```sql
CREATE TABLE interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    panelist_id BIGINT NOT NULL,
    candidate_name VARCHAR(255) NOT NULL,
    candidate_email VARCHAR(255) NOT NULL,
    interview_date DATE NOT NULL,
    interview_time TIME NOT NULL,
    position VARCHAR(255) NOT NULL,
    notes VARCHAR(1000),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Styling

### Color Scheme
- **Primary Gradient**: Purple to Pink (#667eea to #764ba2)
- **Success/Green**: #4CAF50 (Resend OTP, Upcoming interviews)
- **Info/Blue**: #2196F3 (Back to Login, Completed interviews)
- **Danger/Red**: #f44336 (Cancelled interviews, Logout)
- **Warning/Orange**: #FF9800 (Profile actions)

### Responsive Design
- Mobile-friendly layout
- Stacked tabs on small screens
- Responsive grid for statistics
- Flexible interview cards

## Testing Checklist

### Frontend Testing
- [ ] Login with Panelist role redirects to PanelistDashboard
- [ ] All tabs are accessible and display correctly
- [ ] Statistics cards show correct counts
- [ ] Schedule interview form validates inputs
- [ ] Interview list displays all interviews
- [ ] Filter buttons work correctly
- [ ] Buttons have proper hover effects
- [ ] Responsive design works on mobile

### Backend Testing
- [ ] Interview can be created via API
- [ ] Interviews are retrieved correctly
- [ ] Status updates work
- [ ] Statistics are calculated correctly
- [ ] Date/time validation works
- [ ] Authorization is enforced
- [ ] CORS is configured properly

### Integration Testing
- [ ] End-to-end interview scheduling flow
- [ ] Data persists in H2 database
- [ ] Real-time updates after actions
- [ ] Error handling displays properly

## Troubleshooting

### Issue: Panelist Dashboard not showing
**Solution**: Ensure user has role "PANELIST" (case-sensitive)

### Issue: Interview API returns 403
**Solution**: Check JWT token is valid and included in Authorization header

### Issue: Interview not saving
**Solution**: Verify all required fields are provided and date is not in the past

### Issue: Statistics not updating
**Solution**: Refresh the page or check if interviews are being saved correctly

## Future Enhancements

Potential improvements:
1. Email notifications for scheduled interviews
2. Calendar view for interviews
3. Interview feedback/notes system
4. Candidate profile integration
5. Interview recording/documentation
6. Bulk interview scheduling
7. Interview reminders
8. Export interview data to CSV/PDF
9. Interview analytics and reports
10. Video conferencing integration

## Files Modified/Created

### Frontend
- ✅ `frontend/src/components/Login.css` - Enhanced button styling
- ✅ `frontend/src/components/Dashboard.js` - Added PanelistDashboard routing
- ✅ `frontend/src/components/PanelistDashboard.js` - New component
- ✅ `frontend/src/components/PanelistDashboard.css` - New styles

### Backend
- ✅ `backend/src/main/java/com/login/model/Interview.java` - New model
- ✅ `backend/src/main/java/com/login/repository/InterviewRepository.java` - New repository
- ✅ `backend/src/main/java/com/login/service/InterviewService.java` - New service
- ✅ `backend/src/main/java/com/login/controller/InterviewController.java` - New controller
- ✅ `backend/src/main/java/com/login/security/SecurityConfig.java` - Updated security

## Support

For issues or questions:
1. Check this guide first
2. Review the code comments
3. Check the H2 console for database issues
4. Verify JWT token validity
5. Check browser console for frontend errors
6. Check Spring Boot logs for backend errors

---

**Made with ❤️ by Bob**

Last Updated: May 14, 2026