# Reset Database to Load Sample Feedback Data

## 🎯 Problem
HR Dashboard Candidate Feedback tab shows "No feedback submitted yet" even though sample data exists in DataInitializer.

## 🔍 Root Cause
The sample data in `DataInitializer.java` only runs when the database is empty (`userRepository.count() == 0`). If the database already has users, the sample feedback data won't be created.

## ✅ Solution: Reset Database

### Option 1: Delete H2 Database File (Recommended)

#### For Windows (PowerShell):
```powershell
# 1. Stop the backend server (Ctrl+C in the terminal)

# 2. Navigate to project root
cd c:/Users/SiripalliManideep/Desktop/LoginMicroserviceApp

# 3. Delete the H2 database file
Remove-Item -Path "backend/data/logindb.mv.db" -Force -ErrorAction SilentlyContinue
Remove-Item -Path "backend/data/logindb.trace.db" -Force -ErrorAction SilentlyContinue

# 4. Restart the backend
cd backend
mvn spring-boot:run
```

#### For Windows (Command Prompt):
```cmd
REM 1. Stop the backend server (Ctrl+C)

REM 2. Navigate to project root
cd c:\Users\SiripalliManideep\Desktop\LoginMicroserviceApp

REM 3. Delete the H2 database file
del backend\data\logindb.mv.db
del backend\data\logindb.trace.db

REM 4. Restart the backend
cd backend
mvn spring-boot:run
```

#### For Linux/Mac:
```bash
# 1. Stop the backend server (Ctrl+C)

# 2. Navigate to project root
cd ~/Desktop/LoginMicroserviceApp

# 3. Delete the H2 database file
rm -f backend/data/logindb.mv.db
rm -f backend/data/logindb.trace.db

# 4. Restart the backend
cd backend
mvn spring-boot:run
```

### Option 2: Use H2 Console to Delete Data

1. **Access H2 Console**:
   - Start backend server
   - Open browser: http://localhost:8081/h2-console
   - JDBC URL: `jdbc:h2:file:./data/logindb`
   - Username: `sa`
   - Password: (leave empty)
   - Click "Connect"

2. **Delete All Data**:
   ```sql
   -- Delete in correct order (respecting foreign keys)
   DELETE FROM interview_feedback;
   DELETE FROM interview;
   DELETE FROM candidate;
   DELETE FROM panelist;
   DELETE FROM hr_profile;
   DELETE FROM password_history;
   DELETE FROM email_otp;
   DELETE FROM users;
   ```

3. **Restart Backend**:
   - Stop the server (Ctrl+C)
   - Start again: `mvn spring-boot:run`
   - Sample data will be recreated

### Option 3: Change Database Name (Keep Old Data)

If you want to keep existing data and create a new database:

1. **Edit application.properties**:
   ```properties
   # Change database name
   spring.datasource.url=jdbc:h2:file:./data/logindb_new
   ```

2. **Restart Backend**:
   - New database will be created
   - Sample data will be loaded

## 📊 Verify Sample Data Loaded

After restarting the backend, check the console output:

```
=================================================
Creating comprehensive test users for all roles...
=================================================

📋 Creating HR Users...
✅ Created HR user: admin / admin123 (admin@example.com)
✅ Created HR user: hr_manager / hr123 (hr.manager@example.com)
✅ Created HR user: recruiter1 / recruiter123 (recruiter1@example.com)

👨‍💼 Creating Panelist Users...
✅ Created PANELIST user: panelist1 / panelist123 (panelist1@example.com)
✅ Created PANELIST user: panelist2 / panelist123 (panelist2@example.com)
✅ Created PANELIST user: tech_expert / expert123 (tech.expert@example.com)
✅ Created PANELIST user: senior_dev / senior123 (senior.dev@example.com)

👤 Creating Candidate Users...
✅ Created CANDIDATE user: john_doe / john123 (john.doe@example.com)
✅ Created CANDIDATE user: jane_smith / jane123 (jane.smith@example.com)
✅ Created CANDIDATE user: bob_johnson / bob123 (bob.johnson@example.com)

📋 Creating Interview Feedback Records...
✅ Created Feedback for Bob Johnson (SELECTED)
✅ Created Feedback for John Doe (SELECTED - Partial Data)
✅ Created Feedback for Jane Smith (HOLD - Minimal Data)
✅ Created Feedback for Sarah Davis (REJECTED)

=================================================
✅ ALL TEST DATA CREATED SUCCESSFULLY!
=================================================

📊 SUMMARY:
  - HR Users: 3
  - Panelist Users: 4
  - Candidate Users: 6
  - Panelist Records: 4
  - Candidate Records: 6 (All Logged In with Complete Data)
  - HR Profiles: 3
  - Interview Schedules: 6
  - Interview Feedbacks: 4 (with varying data completeness)
```

## 🧪 Test the Fix

### Step 1: Login as HR
```
URL: http://localhost:3000
Username: admin
Password: admin123
```

### Step 2: Navigate to Candidate Feedback
1. Click on "📋 Candidate Feedback" tab
2. You should see **4 feedback records**:
   - Bob Johnson (Full Stack Developer) - SELECTED - 8.0/10
   - John Doe (Senior Java Developer) - SELECTED - 9.0/10
   - Jane Smith (React Developer) - HOLD - 7.5/10
   - Sarah Davis (UI/UX Designer) - REJECTED - 5.5/10

### Step 3: Download PDF
1. Click "📥 Download PDF" button for any feedback
2. PDF should download with complete feedback details

## 🔄 Complete Reset Script (Windows PowerShell)

Save this as `reset-database.ps1`:

```powershell
# Reset Database Script for LoginMicroserviceApp
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Resetting Database for Sample Feedback Data" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# Navigate to project root
$projectRoot = "c:/Users/SiripalliManideep/Desktop/LoginMicroserviceApp"
Set-Location $projectRoot

Write-Host "`n1. Checking for running backend process..." -ForegroundColor Yellow
$backendProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {$_.Path -like "*maven*"}
if ($backendProcess) {
    Write-Host "   Stopping backend process..." -ForegroundColor Yellow
    Stop-Process -Id $backendProcess.Id -Force
    Start-Sleep -Seconds 2
}

Write-Host "`n2. Deleting H2 database files..." -ForegroundColor Yellow
Remove-Item -Path "backend/data/logindb.mv.db" -Force -ErrorAction SilentlyContinue
Remove-Item -Path "backend/data/logindb.trace.db" -Force -ErrorAction SilentlyContinue
Write-Host "   Database files deleted!" -ForegroundColor Green

Write-Host "`n3. Starting backend server..." -ForegroundColor Yellow
Write-Host "   Please wait for sample data to be created..." -ForegroundColor Yellow
Set-Location "backend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvn spring-boot:run"

Write-Host "`n==================================================" -ForegroundColor Cyan
Write-Host "Database reset complete!" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Wait for backend to start (check console for 'Started LoginApplication')" -ForegroundColor White
Write-Host "2. Login as HR: admin / admin123" -ForegroundColor White
Write-Host "3. Go to 'Candidate Feedback' tab" -ForegroundColor White
Write-Host "4. You should see 4 feedback records" -ForegroundColor White
Write-Host "`nPress any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
```

Run with:
```powershell
powershell -ExecutionPolicy Bypass -File reset-database.ps1
```

## 🚨 Troubleshooting

### Issue: "Access Denied" when deleting database
**Solution**: Make sure backend is completely stopped before deleting files

### Issue: Database file not found
**Solution**: Check if database location is correct in `application.properties`

### Issue: Sample data still not showing
**Solution**: 
1. Check backend console for errors
2. Verify `DataInitializer.java` is being executed
3. Check if `userRepository.count() == 0` condition is met

### Issue: Frontend shows error
**Solution**:
1. Clear browser cache (Ctrl+Shift+Delete)
2. Logout and login again
3. Check browser console (F12) for errors

## ✅ Success Indicators

You'll know it worked when:
- ✅ Backend console shows "ALL TEST DATA CREATED SUCCESSFULLY!"
- ✅ Backend console shows "Interview Feedbacks: 4"
- ✅ HR Dashboard Candidate Feedback tab shows 4 records
- ✅ All candidate names are visible
- ✅ Ratings and recommendations are displayed
- ✅ PDF download works

---

**Made with ❤️ by Bob**