# HR Dashboard - Complete Startup Guide

## 🚀 Quick Start (Follow in Order)

### Step 1: Start Backend Server

Open a **NEW terminal** and run:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Wait for this message:**
```
Started LoginApplication in X.XXX seconds
```

Backend will run on: `http://localhost:8081`

---

### Step 2: Verify Backend is Running

Open browser and check:
- H2 Console: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

Check if data exists:
```sql
SELECT * FROM users WHERE role = 'HR';
SELECT * FROM candidates;
SELECT * FROM panelists;
```

---

### Step 3: Start Frontend

Open **ANOTHER NEW terminal** and run:

```bash
cd frontend
npm install
npm start
```

Frontend will run on: `http://localhost:3000`

---

### Step 4: Login as HR

1. Go to: http://localhost:3000
2. Login with HR credentials:
   - **Username:** `admin` or `hr1`
   - **Password:** `Admin@123` or `Hr@123456`

3. You should see the HR Dashboard with Home tab active

---

## 🔍 What You Should See on Home Tab

### 1. Statistics Cards (Top Section)
- 👥 **Total Candidates** - Shows count of all candidates
- 👨‍💼 **Panelists Assigned by You** - Shows your assigned panelists
- 📅 **Total Interviews Scheduled** - Shows interview count
- 🌐 **Total Panelists** - Shows all panelists in system

### 2. Status Breakdown
- Applied, Screening, Interview, Selected, Rejected counts

### 3. All Candidates Details
- Individual cards for each candidate showing:
  - Name, Email, Phone
  - Position, Experience, Skills
  - Current CTC, Location
  - Assigned Panelist

### 4. All Panelists Details
- Individual cards for each panelist showing:
  - Username, Email, Phone
  - Specialization, Experience
  - Expertise, Company, Designation, Location

### 5. Comprehensive Data Table
- Complete mapping of candidates to panelists
- JD Details, Interview dates, Status, CTC info

---

## ❌ Troubleshooting

### Problem: Empty Home Tab (No Data Showing)

**Solution 1: Check Backend is Running**
```bash
# In backend terminal, you should see:
# "Started LoginApplication"
# If not, restart backend
```

**Solution 2: Check Browser Console**
1. Press F12 to open DevTools
2. Go to Console tab
3. Look for errors (red text)
4. Common errors:
   - "Failed to fetch" → Backend not running
   - "401 Unauthorized" → Login again
   - "404 Not Found" → Wrong API endpoint

**Solution 3: Verify API Call**
1. Open DevTools → Network tab
2. Refresh page
3. Look for call to: `/api/hr/{id}/dashboard`
4. Click on it and check:
   - Status: Should be 200
   - Response: Should have data

**Solution 4: Check Database Has Data**
```sql
-- In H2 Console, run:
SELECT COUNT(*) FROM candidates;
SELECT COUNT(*) FROM panelists;

-- If counts are 0, restart backend to trigger DataInitializer
```

**Solution 5: Clear Cache and Reload**
```
Ctrl + Shift + Delete → Clear cache
Then refresh page (F5)
```

---

## 🔧 Manual Data Creation (If No Data)

If database is empty, create sample data:

### Create HR User (if not exists)
```sql
INSERT INTO users (id, username, email, password, role, enabled, mfa_enabled) 
VALUES (1, 'admin', 'admin@company.com', '$2a$10$encrypted_password', 'HR', true, false);
```

### Create Sample Candidate
```sql
INSERT INTO candidates (id, name, email, phone, position, experience_years, skills, current_ctc, location, status, hr_id, created_at) 
VALUES (1, 'John Doe', 'john@example.com', '1234567890', 'Java Developer', 5, 'Java, Spring Boot', 800000, 'Bangalore', 'APPLIED', 1, CURRENT_TIMESTAMP);
```

### Create Sample Panelist
```sql
-- First create user
INSERT INTO users (id, username, email, password, role, enabled, mfa_enabled) 
VALUES (2, 'panelist1', 'panelist1@company.com', '$2a$10$encrypted_password', 'PANELIST', true, false);

-- Then create panelist profile
INSERT INTO panelists (id, user_id, specialization, experience_years, expertise, active, assigned_hr_id, created_at) 
VALUES (1, 2, 'Java', 8, 'Spring Boot, Microservices', true, 1, CURRENT_TIMESTAMP);
```

---

## 📊 Expected Data Flow

1. **Frontend loads** → Calls `fetchDashboardData()`
2. **API Request** → `GET /api/hr/{hrId}/dashboard`
3. **Backend processes** → `HRService.getHRDashboard()`
4. **Returns data**:
   ```json
   {
     "success": true,
     "dashboard": {
       "totalCandidates": 10,
       "totalPanelists": 5,
       "candidates": [...],
       "panelists": [...],
       "dashboardRecords": [...]
     }
   }
   ```
5. **Frontend displays** → Statistics, Cards, Table

---

## 🎯 Quick Test

Run this in browser console (F12):
```javascript
// Check if data is loaded
console.log('Dashboard Data:', window.dashboardData);

// Manual API test
fetch('http://localhost:8081/api/hr/1/dashboard', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(r => r.json())
.then(d => console.log('API Response:', d));
```

---

## 📞 Still Not Working?

1. **Restart Everything:**
   - Stop backend (Ctrl+C)
   - Stop frontend (Ctrl+C)
   - Start backend again
   - Start frontend again
   - Clear browser cache
   - Login again

2. **Check Logs:**
   - Backend terminal: Look for errors
   - Frontend terminal: Look for compilation errors
   - Browser console: Look for runtime errors

3. **Verify Ports:**
   - Backend: http://localhost:8081
   - Frontend: http://localhost:3000
   - No other apps using these ports

---

## ✅ Success Checklist

- [ ] Backend running on port 8081
- [ ] Frontend running on port 3000
- [ ] Logged in as HR user
- [ ] Home tab is active
- [ ] Can see statistics cards
- [ ] Can see candidate cards
- [ ] Can see panelist cards
- [ ] Can see data table

---

**Last Updated:** 2026-05-18
**Author:** Bob