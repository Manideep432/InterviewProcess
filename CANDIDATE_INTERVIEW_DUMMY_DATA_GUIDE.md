# Candidate Interview View - Dummy Data & Testing Guide

## Overview
This guide explains how to test the candidate interview view feature with pre-populated dummy data.

## Dummy Data Created

### HR Profiles (3)
1. **Admin HR Manager**
   - Email: admin@example.com
   - Phone: +91-9999888877
   - Designation: Senior HR Manager
   - Experience: 10 years

2. **Sarah Johnson**
   - Email: hr.manager@example.com
   - Phone: +91-9999888866
   - Designation: HR Manager
   - Experience: 7 years

3. **Michael Roberts**
   - Email: recruiter1@example.com
   - Phone: +91-9999888855
   - Designation: Technical Recruiter
   - Experience: 5 years

### Interview Schedules (6)

#### Interview 1 - John Doe
- **Status**: SCHEDULED
- **Date**: Tomorrow
- **Time**: 10:00 AM - 11:30 AM
- **Position**: Senior Java Developer
- **HR**: Admin HR Manager
- **Panelist**: panelist1 (Java Development)
- **Meeting Link**: https://meet.google.com/abc-defg-hij
- **Notes**: Technical round focusing on Spring Boot, Microservices, and system design

#### Interview 2 - Jane Smith
- **Status**: SCHEDULED
- **Date**: Day After Tomorrow
- **Time**: 2:00 PM - 3:30 PM
- **Position**: React Developer
- **HR**: Admin HR Manager
- **Panelist**: panelist2 (Frontend Development)
- **Meeting Link**: https://meet.google.com/xyz-uvwx-rst
- **Notes**: Frontend technical assessment with live coding

#### Interview 3 - Bob Johnson
- **Status**: COMPLETED
- **Date**: 3 Days Ago
- **Time**: 11:00 AM - 12:30 PM
- **Position**: Full Stack Developer
- **HR**: Admin HR Manager
- **Panelist**: panelist1 (Java Development)
- **Feedback**: "Excellent performance! Strong technical skills in both Java and React. Recommended for hire."

#### Interview 4 - Sarah Davis
- **Status**: SCHEDULED
- **Date**: Next Week (7 days from now)
- **Time**: 3:30 PM - 5:00 PM
- **Position**: UI/UX Designer
- **HR**: Sarah Johnson
- **Panelist**: panelist2 (Frontend Development)
- **Meeting Link**: https://meet.google.com/design-review-001
- **Notes**: Portfolio review and design thinking assessment

#### Interview 5 - Michael Brown
- **Status**: SCHEDULED
- **Date**: 3 Days from Now
- **Time**: 9:30 AM - 11:00 AM
- **Position**: Python Developer
- **HR**: Michael Roberts
- **Panelist**: senior_dev (Database & Backend)
- **Meeting Link**: https://meet.google.com/python-tech-001
- **Notes**: Python technical round focusing on Django/Flask

#### Interview 6 - John Doe (Second Interview)
- **Status**: RESCHEDULED
- **Date**: 5 Days from Now
- **Time**: 4:00 PM - 5:00 PM
- **Position**: Senior Java Developer
- **HR**: Admin HR Manager
- **Panelist**: tech_expert (Cloud Architecture)
- **Meeting Link**: https://meet.google.com/system-design-001
- **Notes**: System design and architecture discussion

## How to Test

### Step 1: Start the Application

1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   
2. **Start Frontend**:
   ```bash
   cd frontend
   npm start
   ```

### Step 2: Login as Candidate

Use any of these candidate credentials:

| Username | Password | Email | Interviews |
|----------|----------|-------|------------|
| john_doe | john123 | john.doe@example.com | 2 interviews |
| jane_smith | jane123 | jane.smith@example.com | 1 interview |
| bob_johnson | bob123 | bob.johnson@example.com | 1 interview (completed) |
| Manideep | password123 | manideep@example.com | 0 interviews |

**Recommended**: Login as **john_doe** to see multiple interviews with different statuses.

### Step 3: Navigate to My Interviews

1. After logging in, you'll see the candidate dashboard
2. Click on the **"My Interviews"** tab in the navigation bar
3. The page will automatically fetch and display all scheduled interviews

### Step 4: Verify Display

Check that the following information is displayed for each interview:

✅ **Interview Header**:
- Position name (e.g., "Senior Java Developer")
- Status badge with appropriate color

✅ **Date & Time**:
- Interview date in readable format
- Time range (From - To)

✅ **HR Details Section**:
- HR name
- HR designation
- HR email
- HR phone number

✅ **Panelist Details Section**:
- Panelist name
- Panelist email

✅ **Additional Information**:
- Interview notes (if available)
- Meeting link button (if available)
- Feedback (for completed interviews)

### Step 5: Test Different Scenarios

#### Scenario 1: Multiple Interviews
- Login as: **john_doe / john123**
- Expected: See 2 interviews
  - One SCHEDULED for tomorrow
  - One RESCHEDULED for 5 days from now

#### Scenario 2: Completed Interview with Feedback
- Login as: **bob_johnson / bob123**
- Expected: See 1 COMPLETED interview with feedback

#### Scenario 3: Single Upcoming Interview
- Login as: **jane_smith / jane123**
- Expected: See 1 SCHEDULED interview for day after tomorrow

#### Scenario 4: No Interviews
- Login as: **Manideep / password123**
- Expected: See message "No interviews scheduled yet"

### Step 6: Test UI Features

1. **Status Badge Colors**:
   - SCHEDULED → Blue badge
   - COMPLETED → Green badge
   - RESCHEDULED → Purple badge
   - IN_PROGRESS → Orange badge
   - CANCELLED → Red badge

2. **Hover Effects**:
   - Hover over interview cards
   - Cards should lift up slightly with shadow

3. **Meeting Links**:
   - Click on "Join Meeting" button
   - Should open meeting link in new tab

4. **Responsive Design**:
   - Resize browser window
   - Check mobile view (< 768px)
   - Verify all information is accessible

## Expected UI Layout

```
┌─────────────────────────────────────────────────────────┐
│  My Scheduled Interviews                                │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │ Senior Java Developer          [SCHEDULED]     │    │
│  ├────────────────────────────────────────────────┤    │
│  │ 📅 Date: May 19, 2026                          │    │
│  │ 🕐 Time: 10:00:00 - 11:30:00                   │    │
│  │                                                 │    │
│  │ 👤 Scheduled By (HR)                           │    │
│  │    Name: Admin HR Manager                      │    │
│  │    Designation: Senior HR Manager              │    │
│  │    Email: admin@example.com                    │    │
│  │    Phone: +91-9999888877                       │    │
│  │                                                 │    │
│  │ 👨‍💼 Interviewer (Panelist)                      │    │
│  │    Name: Panelist One                          │    │
│  │    Email: panelist1@example.com                │    │
│  │                                                 │    │
│  │ 📝 Notes                                        │    │
│  │    Technical round focusing on Spring Boot...  │    │
│  │                                                 │    │
│  │ 🔗 Meeting Link                                │    │
│  │    [Join Meeting]                              │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Troubleshooting

### Issue: No Interviews Showing

**Solution**:
1. Check browser console for errors
2. Verify backend is running on port 8081
3. Check if candidate email matches interview records
4. Clear browser cache and reload

### Issue: HR Details Not Showing

**Solution**:
1. Verify HR profiles were created in database
2. Check if `hr_id` in interviews matches user ID
3. Restart backend to ensure DataInitializer ran

### Issue: Styling Issues

**Solution**:
1. Clear browser cache
2. Check if CandidateInfo.css is loaded
3. Verify no CSS conflicts

### Issue: "Loading interviews..." Stuck

**Solution**:
1. Check network tab in browser dev tools
2. Verify API endpoint is accessible
3. Check JWT token is valid
4. Verify CORS settings

## Database Verification

To verify data was created, you can check the H2 console:

1. Go to: `http://localhost:8081/h2-console`
2. Use JDBC URL from application.properties
3. Run these queries:

```sql
-- Check HR Profiles
SELECT * FROM hr_profiles;

-- Check Interviews
SELECT * FROM interviews;

-- Check Interview with HR details
SELECT i.*, u.email as hr_email, h.full_name as hr_name
FROM interviews i
JOIN users u ON i.hr_id = u.id
LEFT JOIN hr_profiles h ON u.id = h.user_id;
```

## API Testing

You can also test the API directly using curl or Postman:

```bash
# Get interviews for john.doe@example.com
curl -X GET \
  'http://localhost:8081/api/interviews/candidate/email/john.doe@example.com/details' \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN'
```

## Next Steps

After verifying the feature works:

1. Test with different candidates
2. Try creating new interviews via HR dashboard
3. Verify real-time updates
4. Test on mobile devices
5. Check accessibility features

## Made with Bob