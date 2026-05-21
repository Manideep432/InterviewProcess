# 🚀 Restart and Test JRS & Candidate Type Feature

## ⚠️ IMPORTANT: You Must Restart the Backend!

The JRS and Candidate Type fields will **NOT work** until you restart the backend application.

---

## 📋 Step-by-Step Instructions

### Step 1: Stop the Backend
Press `Ctrl+C` in the terminal where the backend is running.

### Step 2: Restart the Backend
```bash
cd backend
mvn spring-boot:run
```

**Wait for this message:**
```
Started LoginApplication in X.XXX seconds
```

### Step 3: Verify Database Columns Created
Look for these log messages during startup:
```
Hibernate: alter table candidates add column jrs varchar(100)
Hibernate: alter table candidates add column candidate_type varchar(50)
```

### Step 4: Test the Feature

#### Option A: Test with Existing Dummy Data
If you want to see the dummy data with JRS and Candidate Type:

1. **Stop the backend**
2. **Delete the database:**
   ```bash
   # Windows
   del backend\data\logindb.mv.db
   
   # Linux/Mac
   rm backend/data/logindb.mv.db
   ```
3. **Restart the backend** - Fresh data will be created with JRS and Candidate Type values

#### Option B: Test with Current Data
1. Just restart the backend (existing candidates will have NULL values)
2. Create a new candidate or edit existing ones to add JRS and Candidate Type

---

## ✅ Testing Checklist

### 1. Login to HR Dashboard
- URL: `http://localhost:3000`
- Username: `admin`
- Password: `admin123`

### 2. Navigate to "Manage Candidates" Tab
- ✅ Verify JRS column appears after Position
- ✅ Verify Candidate Type column appears after JRS
- ✅ Check if existing candidates show JRS and Candidate Type (if you reset database)

### 3. Test Add New Candidate
- Click "➕ Add New Candidate"
- Fill in all fields:
  - Name: Test Candidate
  - Email: test.candidate@example.com
  - Phone: +91-9999999999
  - Position: Software Engineer
  - **JRS: JR-2024-TEST** ⭐
  - **Candidate Type: EXTERNAL** ⭐
  - Experience: 3
  - Location: Bangalore
  - Username: testcandidate
  - Password: test12345
- Click "Create Candidate"
- ✅ Verify candidate appears in table with JRS and Candidate Type

### 4. Test Edit Candidate
- Click "✏️ Edit" on any candidate
- ✅ Verify JRS field is present and editable
- ✅ Verify Candidate Type dropdown is present
- Change values:
  - JRS: JR-2024-UPDATED
  - Candidate Type: REFERRAL
- Click "Update Candidate"
- ✅ Verify changes are saved and displayed in table

---

## 🔍 What Was Fixed

### Backend Changes:
1. ✅ **CandidateService.java** - Added JRS and Candidate Type to `updateCandidate()` method
2. ✅ **HRService.java** - Added JRS and Candidate Type to `createCandidateWithLogin()` method

### These fixes ensure:
- New candidates save JRS and Candidate Type values
- Edited candidates update JRS and Candidate Type values
- Data persists to database correctly

---

## 📊 Expected Results

### After Restarting Backend:

**If you reset the database, you should see:**

| Candidate Name    | JRS          | Candidate Type |
|-------------------|--------------|----------------|
| John Doe          | JR-2024-001  | EXTERNAL       |
| Jane Smith        | JR-2024-002  | REFERRAL       |
| Bob Johnson       | JR-2024-003  | EXTERNAL       |
| Alice Williams    | JR-2024-004  | AGENCY         |
| Michael Brown     | JR-2024-005  | INTERNAL       |
| Sarah Davis       | JR-2024-006  | REFERRAL       |

**If you don't reset the database:**
- Existing candidates will show "N/A" for JRS and Candidate Type
- You can edit them to add values
- New candidates will save JRS and Candidate Type correctly

---

## 🐛 Troubleshooting

### Issue: JRS and Candidate Type still not saving

**Solution:**
1. Make sure you **restarted the backend** after the code changes
2. Check backend console for errors
3. Verify database columns were created (check logs)
4. Clear browser cache and reload frontend

### Issue: Columns not appearing in table

**Solution:**
1. Hard refresh browser: `Ctrl+Shift+R` (Windows) or `Cmd+Shift+R` (Mac)
2. Clear browser cache
3. Restart frontend: `Ctrl+C` then `npm start`

### Issue: Database columns not created

**Solution:**
1. Check `application.properties` has: `spring.jpa.hibernate.ddl-auto=update`
2. Delete database and restart backend to force recreation
3. Check backend console for Hibernate errors

---

## 🎯 Quick Test Commands

```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend (if needed)
cd frontend
npm start

# Access Application
http://localhost:3000

# Login
Username: admin
Password: admin123
```

---

## ✨ Success Indicators

You'll know it's working when:
- ✅ Backend starts without errors
- ✅ JRS and Candidate Type columns appear in Manage Candidates table
- ✅ Add New Candidate form includes JRS and Candidate Type fields
- ✅ Edit Candidate form includes JRS and Candidate Type fields
- ✅ New candidates save with JRS and Candidate Type values
- ✅ Edited candidates update JRS and Candidate Type values
- ✅ Table displays the values correctly (not "N/A" for new/edited candidates)

---

## 📞 Still Having Issues?

If the feature still doesn't work after restarting:

1. **Check backend console** for any errors
2. **Check browser console** (F12) for frontend errors
3. **Verify all files were saved** before restarting
4. **Try deleting the database** and restarting for fresh data
5. **Check network tab** in browser to see if API calls are successful

---

**Made with ❤️ by Bob**

**Remember: RESTART THE BACKEND! 🔄**