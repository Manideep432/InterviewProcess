# JRS and Candidate Type Feature - Complete Implementation Guide

## 📋 Overview

This guide documents the complete implementation of **JRS (Job Requisition System)** and **Candidate Type** fields in the HR Dashboard's Manage Candidates section.

---

## 🎯 Feature Summary

### New Fields Added:
1. **JRS** - Job Requisition System ID (Text field)
2. **Candidate Type** - Classification of candidate source (Dropdown)

### Column Order in Manage Candidates Table:
1. Name
2. Email
3. Phone
4. Position
5. **JRS** ⭐ NEW
6. **Candidate Type** ⭐ NEW
7. Experience
8. Location
9. Interview
10. Status
11. Actions

---

## 🗄️ Database Changes

### Candidate Table - New Columns:
```sql
jrs VARCHAR(100)           -- Job Requisition System ID
candidate_type VARCHAR(50) -- EXTERNAL, INTERNAL, REFERRAL, AGENCY
```

**Note:** These columns will be automatically created when you restart the backend application.

---

## 🔧 Backend Implementation

### 1. Candidate.java (Model)
**Location:** `backend/src/main/java/com/login/model/Candidate.java`

**Added Fields:**
```java
@Column(length = 100)
private String jrs; // Job Requisition System or Job Reference System

@Column(name = "candidate_type", length = 50)
private String candidateType; // INTERNAL, EXTERNAL, REFERRAL, etc.
```

**Getters and Setters:** ✅ Added

---

### 2. CandidateDTO.java (Data Transfer Object)
**Location:** `backend/src/main/java/com/login/dto/CandidateDTO.java`

**Added Fields:**
```java
private String jrs;
private String candidateType;
```

**Constructor Updated:** ✅ Maps from Candidate entity
**Getters and Setters:** ✅ Added

---

### 3. DataInitializer.java (Sample Data)
**Location:** `backend/src/main/java/com/login/config/DataInitializer.java`

**Dummy Data Added for All 6 Candidates:**

| Candidate Name    | JRS          | Candidate Type | Position              |
|-------------------|--------------|----------------|-----------------------|
| John Doe          | JR-2024-001  | EXTERNAL       | Senior Java Developer |
| Jane Smith        | JR-2024-002  | REFERRAL       | React Developer       |
| Bob Johnson       | JR-2024-003  | EXTERNAL       | Full Stack Developer  |
| Alice Williams    | JR-2024-004  | AGENCY         | DevOps Engineer       |
| Michael Brown     | JR-2024-005  | INTERNAL       | Python Developer      |
| Sarah Davis       | JR-2024-006  | REFERRAL       | UI/UX Designer        |

---

## 🎨 Frontend Implementation

### 1. HRDashboard.js - State Management
**Location:** `frontend/src/components/HRDashboard.js`

**Updated newCandidate State:**
```javascript
const [newCandidate, setNewCandidate] = useState({
  name: '',
  email: '',
  phone: '',
  position: '',
  jrs: '',                    // ⭐ NEW
  candidateType: 'EXTERNAL',  // ⭐ NEW (default value)
  experienceYears: '',
  // ... other fields
});
```

---

### 2. Manage Candidates Table
**Updated Table Structure:**

**Header:**
```jsx
<thead>
  <tr>
    <th>Name</th>
    <th>Email</th>
    <th>Phone</th>
    <th>Position</th>
    <th>JRS</th>              {/* ⭐ NEW */}
    <th>Candidate Type</th>   {/* ⭐ NEW */}
    <th>Experience</th>
    <th>Location</th>
    <th>Interview</th>
    <th>Status</th>
    <th>Actions</th>
  </tr>
</thead>
```

**Body:**
```jsx
<td>{candidate.jrs || 'N/A'}</td>
<td>{candidate.candidateType || 'N/A'}</td>
```

---

### 3. Add New Candidate Form
**New Input Fields Added:**

**JRS Field:**
```jsx
<div className="form-group">
  <label htmlFor="jrs">JRS</label>
  <input
    type="text"
    id="jrs"
    name="jrs"
    value={newCandidate.jrs}
    onChange={handleInputChange}
    placeholder="Job Requisition System ID"
  />
</div>
```

**Candidate Type Field:**
```jsx
<div className="form-group">
  <label htmlFor="candidateType">Candidate Type</label>
  <select
    id="candidateType"
    name="candidateType"
    value={newCandidate.candidateType}
    onChange={handleInputChange}
  >
    <option value="EXTERNAL">External</option>
    <option value="INTERNAL">Internal</option>
    <option value="REFERRAL">Referral</option>
    <option value="AGENCY">Agency</option>
  </select>
</div>
```

---

### 4. Edit Candidate Form
**Same fields added with "edit-" prefix for IDs:**
- `edit-jrs`
- `edit-candidateType`

---

## 📊 Candidate Type Options

| Value      | Description                                    | Use Case                          |
|------------|------------------------------------------------|-----------------------------------|
| EXTERNAL   | Candidates from outside the organization       | Job portals, direct applications  |
| INTERNAL   | Current employees applying for new positions   | Internal job postings             |
| REFERRAL   | Candidates referred by existing employees      | Employee referral program         |
| AGENCY     | Candidates sourced through recruitment agencies| Third-party recruitment           |

---

## 🧪 Testing Instructions

### Step 1: Reset Database (Optional)
To see the dummy data with JRS and Candidate Type:

1. **Stop the backend** if running
2. **Delete the database file:**
   ```bash
   # For H2 Database
   rm backend/data/logindb.mv.db
   ```
3. **Restart the backend** - Fresh data will be created

### Step 2: Start Backend
```bash
cd backend
mvn spring-boot:run
```

**Expected Console Output:**
```
✅ Created Candidate: John Doe (INTERVIEW, Logged In)
✅ Created Candidate: Jane Smith (SCREENING, Logged In)
✅ Created Candidate: Bob Johnson (SELECTED, Logged In)
✅ Created Candidate: Alice Williams (APPLIED, Logged In)
✅ Created Candidate: Michael Brown (SCREENING, Logged In)
✅ Created Candidate: Sarah Davis (INTERVIEW, Logged In)
```

### Step 3: Start Frontend
```bash
cd frontend
npm start
```

### Step 4: Test the Feature

1. **Login as HR:**
   - Username: `admin`
   - Password: `admin123`

2. **Navigate to "Manage Candidates" Tab**

3. **Verify Table Display:**
   - ✅ JRS column appears after Position
   - ✅ Candidate Type column appears after JRS
   - ✅ All 6 candidates show their JRS and Candidate Type values

4. **Test Add New Candidate:**
   - Click "Add New Candidate"
   - Fill in all fields including JRS and Candidate Type
   - Submit the form
   - Verify new candidate appears in table with correct values

5. **Test Edit Candidate:**
   - Click "Edit" on any candidate
   - Verify JRS and Candidate Type fields are populated
   - Modify the values
   - Save changes
   - Verify updated values in table

---

## 📝 Sample Data Reference

### Complete Candidate Data with JRS and Candidate Type:

```
1. John Doe
   - JRS: JR-2024-001
   - Type: EXTERNAL
   - Position: Senior Java Developer
   - Status: INTERVIEW

2. Jane Smith
   - JRS: JR-2024-002
   - Type: REFERRAL
   - Position: React Developer
   - Status: SCREENING

3. Bob Johnson
   - JRS: JR-2024-003
   - Type: EXTERNAL
   - Position: Full Stack Developer
   - Status: SELECTED

4. Alice Williams
   - JRS: JR-2024-004
   - Type: AGENCY
   - Position: DevOps Engineer
   - Status: APPLIED

5. Michael Brown
   - JRS: JR-2024-005
   - Type: INTERNAL
   - Position: Python Developer
   - Status: SCREENING

6. Sarah Davis
   - JRS: JR-2024-006
   - Type: REFERRAL
   - Position: UI/UX Designer
   - Status: INTERVIEW
```

---

## 🔍 Verification Checklist

- [ ] Backend starts without errors
- [ ] Database columns created automatically
- [ ] All 6 candidates have JRS and Candidate Type values
- [ ] Manage Candidates table shows new columns in correct order
- [ ] Add New Candidate form includes JRS and Candidate Type fields
- [ ] Edit Candidate form includes JRS and Candidate Type fields
- [ ] New candidates can be created with JRS and Candidate Type
- [ ] Existing candidates can be edited to update JRS and Candidate Type
- [ ] Table displays "N/A" for empty JRS values
- [ ] Candidate Type dropdown shows all 4 options

---

## 🎯 JRS Naming Convention

**Recommended Format:** `JR-YYYY-NNN`

Examples:
- `JR-2024-001` - First job requisition of 2024
- `JR-2024-002` - Second job requisition of 2024
- `JR-2025-001` - First job requisition of 2025

**Alternative Formats:**
- `REQ-2024-001` - Requisition format
- `JOB-2024-001` - Job format
- `POS-2024-001` - Position format

---

## 🚀 Quick Start Commands

```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm start

# Access Application
http://localhost:3000

# Login Credentials
Username: admin
Password: admin123
```

---

## 📌 Important Notes

1. **Database Auto-Creation:** New columns are created automatically by JPA/Hibernate
2. **Existing Data:** Old candidates will have NULL values for JRS and Candidate Type
3. **Default Value:** Candidate Type defaults to "EXTERNAL" in the form
4. **Optional Field:** JRS is optional and can be left empty
5. **Display Logic:** Empty JRS values show as "N/A" in the table

---

## 🎉 Feature Complete!

All components have been successfully implemented:
- ✅ Backend Model & DTO updated
- ✅ Database schema enhanced
- ✅ Frontend UI updated
- ✅ Forms include new fields
- ✅ Dummy data populated
- ✅ Table displays new columns

**The JRS and Candidate Type feature is now fully functional!**

---

## 📞 Support

If you encounter any issues:
1. Check backend console for errors
2. Verify database columns were created
3. Clear browser cache and reload
4. Restart both backend and frontend
5. Check that all files were saved properly

---

**Made with ❤️ by Bob**