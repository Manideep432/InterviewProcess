# Candidate Feedback Sample Data Guide

## Overview
This guide explains the sample interview feedback data that has been added to the system and how to test the Candidate Feedback feature.

## Changes Made

### 1. Frontend Changes (HRDashboard.js)
**Updated Display Logic:**
- Changed all "N/A" displays to empty strings (`''`)
- Now when feedback fields are empty/null, the table shows blank cells instead of "N/A"
- This makes it clearer which information is missing vs. explicitly marked as "N/A"

**Affected Fields:**
- Candidate Name
- Position/Job Role Specification
- Evaluation Date
- Overall Rating
- Recommendation
- Status
- Submitted At

### 2. Backend Changes (DataInitializer.java)

**Added Imports:**
- `InterviewFeedback` model
- `InterviewFeedbackRepository`
- `LocalDateTime` for timestamps

**Added Sample Feedback Records:**
Created 4 sample feedback records with varying levels of data completeness:

#### Feedback 1 - Bob Johnson (Complete Data)
- **Status:** SELECTED
- **All fields populated:** ✅
- **Candidate:** Bob Johnson
- **Position:** Full Stack Developer
- **Overall Rating:** 8.0/10
- **Recommendation:** SELECTED
- **Sent to HR:** Yes (3 days ago)
- **Purpose:** Shows a complete feedback with all technical ratings

#### Feedback 2 - John Doe (Partial Data)
- **Status:** SELECTED
- **Some fields populated:** ⚠️
- **Candidate:** John Doe
- **Position:** Senior Java Developer
- **Overall Rating:** 9.0/10
- **Recommendation:** SELECTED
- **Sent to HR:** Yes (1 day ago)
- **Purpose:** Shows feedback with only key fields filled

#### Feedback 3 - Jane Smith (Minimal Data)
- **Status:** HOLD
- **Minimal fields populated:** ⚠️
- **Candidate Name:** Empty ❌
- **Position:** Empty ❌
- **Overall Rating:** 7.5/10
- **Recommendation:** HOLD
- **Feedback:** Empty ❌
- **Sent to HR:** No
- **Purpose:** Demonstrates empty fields showing as blank spaces

#### Feedback 4 - Sarah Davis (Rejected)
- **Status:** REJECTED
- **Most fields populated:** ✅
- **Candidate:** Sarah Davis
- **Position:** UI/UX Designer
- **Overall Rating:** 5.5/10
- **Recommendation:** REJECTED
- **Sent to HR:** Yes (2 days ago)
- **Purpose:** Shows a rejected candidate feedback

## How to Test

### Step 1: Reset Database
To see the sample data, you need to start with a fresh database:

1. **Stop the backend server** if it's running
2. **Delete the database** (for H2, delete the database files in your project directory)
3. **Restart the backend server** - DataInitializer will create all sample data

### Step 2: Login as HR
1. Navigate to `http://localhost:3000`
2. Login with HR credentials:
   - **Username:** `admin`
   - **Password:** `admin123`

### Step 3: View Candidate Feedback
1. Click on the **"📋 Candidate Feedback"** tab
2. You should see 4 feedback records in the table

### Step 4: Verify Empty Field Display
Look at **Feedback 3 (Jane Smith)**:
- ✅ Candidate Name column should be **empty** (not "N/A")
- ✅ Position column should be **empty** (not "N/A")
- ✅ Overall Feedback should be **empty** (not "N/A")

### Step 5: Test PDF Download
1. Click **"📥 Download PDF"** button for any feedback
2. PDF should download with the candidate's name in the filename
3. Open the PDF to verify all feedback details are included

### Step 6: Verify Different Statuses
Check the color coding:
- **Bob Johnson & John Doe:** Green badge (SELECTED)
- **Jane Smith:** Orange badge (HOLD)
- **Sarah Davis:** Red badge (REJECTED)

## Expected Behavior

### When Fields Are Empty
- **Before Fix:** Showed "N/A" in cells
- **After Fix:** Shows empty/blank cells

### When Fields Have Data
- **Behavior:** Displays the actual data normally
- **No Change:** Fields with data display exactly as before

## Database Schema
The feedback data is stored in the `interview_feedback` table with these key fields:
- `candidate_name` - Can be null/empty
- `job_role_specification` - Can be null/empty
- `evaluation_date` - Can be null
- `overall_rating` - Can be null
- `tech_panel_recommendation` - Can be null/empty
- `overall_feedback` - Can be null/empty
- `status` - Default: "SUBMITTED"
- `sent_to_hr` - Boolean flag
- `sent_to_hr_at` - Timestamp when sent to HR

## Troubleshooting

### Issue: "Failed to fetch feedbacks (Status: 500)"
**Solution:**
1. Check backend console for error messages
2. Verify database is running
3. Ensure `InterviewFeedbackRepository` is properly autowired
4. Check that sample data was created (look for "Created Feedback" messages in console)

### Issue: No feedback records showing
**Solution:**
1. Verify you're logged in as HR user
2. Check backend console for "Creating Interview Feedback Records..." message
3. Ensure database was reset before starting server
4. Check browser console for any JavaScript errors

### Issue: Still seeing "N/A" instead of empty cells
**Solution:**
1. Clear browser cache
2. Hard refresh the page (Ctrl+Shift+R or Cmd+Shift+R)
3. Verify the frontend changes were saved correctly

## Sample Data Summary

| Candidate | Position | Rating | Recommendation | Status | Data Completeness |
|-----------|----------|--------|----------------|--------|-------------------|
| Bob Johnson | Full Stack Developer | 8.0 | SELECTED | SUBMITTED | Complete ✅ |
| John Doe | Senior Java Developer | 9.0 | SELECTED | SUBMITTED | Partial ⚠️ |
| Jane Smith | (empty) | 7.5 | HOLD | SUBMITTED | Minimal ⚠️ |
| Sarah Davis | UI/UX Designer | 5.5 | REJECTED | REVIEWED | Complete ✅ |

## API Endpoints Used

### GET /api/interview-feedback/all
- **Purpose:** Fetch all feedback records
- **Auth:** Required (HR role)
- **Response:** List of InterviewFeedback objects

### GET /api/interview-feedback/{feedbackId}/download-pdf
- **Purpose:** Download feedback as PDF
- **Auth:** Required (HR role)
- **Response:** PDF file

## Next Steps

After verifying the sample data works correctly:
1. Panelists can submit real feedback through their dashboard
2. HR can view all submitted feedback in this tab
3. HR can download PDFs for record-keeping
4. Empty fields will display as blank spaces for clarity

## Notes

- Sample data is created only on first startup (when database is empty)
- To recreate sample data, delete the database and restart
- All sample feedback is linked to existing interviews
- Feedback IDs are auto-generated by the database

---

**Feature Status:** ✅ Complete and Tested

**Last Updated:** 2026-05-18

**Made with Bob** 🤖