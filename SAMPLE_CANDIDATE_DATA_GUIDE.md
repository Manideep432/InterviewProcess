# Sample Candidate Data Guide

## Overview
This guide explains the sample candidate data that has been created in the system to demonstrate the "Candidate Details" feature in the HR Dashboard.

## Sample Data Created

### 6 Sample Candidates (All Logged In)

All candidates have been created with:
- ✅ Complete profile information
- ✅ Logged in status (`isLoggedIn = true`)
- ✅ HR email set to `admin@example.com`
- ✅ Recent login timestamps
- ✅ Full CTC details (Current, Old, New)
- ✅ Job descriptions and other required fields

### Candidate Details

#### 1. John Doe
- **Email**: john.doe@example.com
- **Phone**: +91-9876543210
- **Position**: Senior Java Developer
- **Experience**: 6 years
- **Skills**: Java, Spring Boot, Microservices, Docker, Kubernetes
- **Status**: INTERVIEW
- **Current CTC**: ₹12,00,000
- **Old CTC**: ₹12,00,000
- **New CTC**: ₹18,00,000
- **Location**: Bangalore, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: panelist1
- **JD**: Looking for experienced Java developer with strong Spring Boot and microservices background
- **Last Login**: 15 minutes ago

#### 2. Jane Smith
- **Email**: jane.smith@example.com
- **Phone**: +91-9876543211
- **Position**: React Developer
- **Experience**: 3 years
- **Skills**: React, Redux, JavaScript, TypeScript, CSS, HTML5
- **Status**: SCREENING
- **Current CTC**: ₹8,00,000
- **Old CTC**: ₹8,00,000
- **New CTC**: ₹12,00,000
- **Location**: Hyderabad, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: panelist2
- **JD**: Frontend developer with expertise in React and modern JavaScript frameworks
- **Last Login**: 5 minutes ago

#### 3. Bob Johnson
- **Email**: bob.johnson@example.com
- **Phone**: +91-9876543212
- **Position**: Full Stack Developer
- **Experience**: 5 years
- **Skills**: Java, Spring, React, PostgreSQL, MongoDB, REST APIs
- **Status**: SELECTED
- **Current CTC**: ₹15,00,000
- **Old CTC**: ₹15,00,000
- **New CTC**: ₹22,00,000
- **Location**: Pune, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: panelist1
- **JD**: Full stack developer proficient in both backend (Java/Spring) and frontend (React)
- **Last Login**: 30 minutes ago

#### 4. Alice Williams
- **Email**: alice.williams@example.com
- **Phone**: +91-9876543213
- **Position**: DevOps Engineer
- **Experience**: 4 years
- **Skills**: AWS, Docker, Kubernetes, Jenkins, Terraform, CI/CD
- **Status**: APPLIED
- **Current CTC**: ₹10,00,000
- **Old CTC**: ₹10,00,000
- **New CTC**: ₹15,00,000
- **Location**: Mumbai, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: None
- **JD**: DevOps engineer with strong cloud infrastructure experience
- **Last Login**: 10 minutes ago

#### 5. Michael Brown
- **Email**: michael.brown@example.com
- **Phone**: +91-9876543214
- **Position**: Python Developer
- **Experience**: 3 years
- **Skills**: Python, Django, Flask, PostgreSQL, REST APIs, Machine Learning
- **Status**: SCREENING
- **Current CTC**: ₹9,00,000
- **Old CTC**: ₹9,00,000
- **New CTC**: ₹14,00,000
- **Location**: Chennai, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: senior_dev
- **JD**: Python developer with web framework experience
- **Last Login**: 20 minutes ago

#### 6. Sarah Davis
- **Email**: sarah.davis@example.com
- **Phone**: +91-9876543215
- **Position**: UI/UX Designer
- **Experience**: 4 years
- **Skills**: Figma, Adobe XD, Sketch, User Research, Prototyping
- **Status**: INTERVIEW
- **Current CTC**: ₹7,00,000
- **Old CTC**: ₹7,00,000
- **New CTC**: ₹11,00,000
- **Location**: Bangalore, India
- **Employment Type**: FULL_TIME
- **Assigned Panelist**: panelist2
- **JD**: Creative UI/UX designer with strong portfolio
- **Last Login**: 8 minutes ago

## How to View Sample Data

### Step 1: Login as HR
1. Go to `http://localhost:3000`
2. Login with credentials:
   - **Username**: admin
   - **Password**: admin123

### Step 2: Navigate to Candidate Details Tab
1. After login, you'll see the HR Dashboard
2. Click on the **"📋 Candidate Details"** tab
3. You should see all 6 sample candidates displayed with their complete information

### Step 3: View Candidate Information
Each candidate card displays:
- Name and status badge
- Email and phone number
- Last login timestamp
- Assigned panelist (if any)
- Job description details
- Interview date/time (if scheduled)
- Joining date
- Current, Old, and New CTC
- Employment type
- Location
- Position
- Experience years
- Skills

## Features Demonstrated

### 1. Real-time Login Tracking
- All candidates show as "Currently logged in" with green indicator (🟢)
- Last login timestamps are displayed
- Auto-refresh every 10 seconds to show new logins

### 2. Complete Candidate Profiles
- Full contact information
- Detailed job descriptions
- CTC progression (Current → Old → New)
- Employment details
- Skills and experience

### 3. HR-Candidate Association
- All candidates have `hrMailId` set to `admin@example.com`
- Only candidates who specified this HR's email are shown
- Proper filtering based on HR email

### 4. Status Tracking
- Different statuses: APPLIED, SCREENING, INTERVIEW, SELECTED
- Color-coded status badges for easy identification

### 5. Panelist Assignment
- Some candidates have assigned panelists
- Shows panelist name and email
- "Not Assigned" for candidates without panelists

## Database Information

The sample data is automatically created when the application starts if the database is empty. The data is stored in the MySQL database `logindb` in the `candidates` table.

### To Reset Data
If you want to recreate the sample data:
1. Stop the backend server
2. Drop the database: `DROP DATABASE logindb;`
3. Restart the backend server
4. The DataInitializer will recreate all sample data

## Technical Implementation

### Backend Changes
- **File**: `backend/src/main/java/com/login/config/DataInitializer.java`
- **Changes**: Added 6 comprehensive candidate records with all required fields
- **Key Fields Set**:
  - `isLoggedIn = true`
  - `hrMailId = "admin@example.com"`
  - `lastLoginAt = recent timestamps`
  - Complete CTC information
  - Job descriptions
  - Location and employment type

### API Endpoint
- **Endpoint**: `GET /api/hr/{hrId}/logged-in-candidates`
- **Purpose**: Fetches only logged-in candidates who specified the HR's email
- **Response**: Array of candidate objects with complete information

### Frontend Display
- **Component**: `frontend/src/components/HRDashboard.js`
- **Tab**: "Candidate Details"
- **Features**:
  - Card-based layout for each candidate
  - Real-time updates every 10 seconds
  - Online indicator for logged-in candidates
  - Formatted CTC display in Indian Rupees
  - Responsive grid layout

## Troubleshooting

### No Candidates Showing?
1. **Check if backend is running**: Backend should be on port 8081
2. **Verify HR login**: Make sure you're logged in as `admin`
3. **Check browser console**: Look for any API errors
4. **Verify database**: Check if candidates exist in the database

### Candidates Not Marked as Logged In?
1. **Check database**: Verify `is_logged_in` field is `true`
2. **Check HR email**: Verify `hr_mail_id` matches your HR email
3. **Restart backend**: Stop and restart to reload data

## Next Steps

You can now:
1. ✅ View all logged-in candidates in the HR Dashboard
2. ✅ See complete candidate information including CTC details
3. ✅ Monitor real-time login status
4. ✅ Track candidate progress through different statuses
5. ✅ View panelist assignments

---

**Made with ❤️ by Bob**