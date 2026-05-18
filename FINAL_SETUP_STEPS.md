# ✅ Final Setup Steps - Fix Role Display Issue

## Current Situation
- You registered with HR or PANELIST role
- After login, it shows "CANDIDATE" role
- This is because the backend is using OLD code without the fix

## 🔧 Solution: Restart Backend with New Code

### Step 1: Stop the Backend
In the terminal where backend is running, press:
```
Ctrl + C
```

### Step 2: Delete Old Database (IMPORTANT!)
Navigate to backend folder and delete these files:
```bash
cd backend
# Windows PowerShell:
Remove-Item testdb.mv.db -ErrorAction SilentlyContinue
Remove-Item testdb.trace.db -ErrorAction SilentlyContinue

# Mac/Linux:
rm -f testdb.mv.db testdb.trace.db
```

Or manually delete these files from the `backend` folder:
- `testdb.mv.db`
- `testdb.trace.db`

### Step 3: Restart Backend
```bash
mvn spring-boot:run
```

### Step 4: Wait for Startup Messages
Look for these messages in console:
```
=================================================
Creating test users...
=================================================
✅ Created CANDIDATE user: Manideep (password: password123)
✅ Created CANDIDATE user: Manideep1 (password: password123)
✅ Created CANDIDATE user: testuser (password: test123)
✅ Created HR user: admin (password: admin123)
✅ Created PANELIST user: panelist1 (password: panelist123)
✅ Created PANELIST user: panelist2 (password: panelist123)
✅ Created Panelist record for panelist1 (assigned by admin)
✅ Created Panelist record for panelist2 (assigned by admin)
✅ Created Candidate: John Doe
✅ Created Candidate: Jane Smith (assigned to panelist2)
✅ Created Candidate: Bob Johnson (assigned to panelist1)
=================================================
Test users, panelists, and candidates created successfully!
=================================================
```

If you see "Users already exist in database. Skipping initialization." - you didn't delete the database files!

### Step 5: Test Login

#### Test 1: Login as HR
1. Go to `http://localhost:3000`
2. Login with:
   - Username: `admin`
   - Password: `admin123`
3. **Expected Result**:
   ```
   ✅ You have successfully logged in as HR
   ✅ You can manage panelists and candidates
   
   Tabs visible:
   - 👤 User Info (shows Role: HR)
   - 👨‍💼 Panelists
   - 🎓 Candidates
   - 💬 Feedback
   ```

#### Test 2: Login as Panelist
1. Logout
2. Login with:
   - Username: `panelist1`
   - Password: `panelist123`
3. **Expected Result**:
   ```
   ✅ You have successfully logged in as PANELIST
   ✅ You can view assigned candidates and HR information
   
   Tabs visible:
   - 👤 User Info (shows Role: PANELIST)
   - 📋 Assigned Candidates
   - 💼 HR Information
   - 💬 Feedback
   ```

#### Test 3: Login as Candidate
1. Logout
2. Login with:
   - Username: `Manideep`
   - Password: `password123`
3. **Expected Result**:
   ```
   ✅ You have successfully logged in as CANDIDATE
   ✅ You can view your application status
   
   Tabs visible:
   - 👤 User Info (shows Role: CANDIDATE)
   - 💬 Feedback
   ```

## ❌ If Still Showing Wrong Role

### Check 1: Backend Console
Look at the backend console. Do you see the "Creating test users..." messages?
- **YES** → Good! Database was recreated
- **NO** → Database files weren't deleted. Stop backend, delete files, restart

### Check 2: Browser Cache
Clear browser cache or use Incognito/Private mode:
1. Open new Incognito window
2. Go to `http://localhost:3000`
3. Try login again

### Check 3: Verify in H2 Console
1. Open `http://localhost:8080/h2-console`
2. Login (JDBC URL: `jdbc:h2:mem:testdb`, Username: `sa`, Password: empty)
3. Run:
   ```sql
   SELECT id, username, email, role FROM users ORDER BY id;
   ```
4. Should see:
   ```
   1 | Manideep   | manideep@example.com     | CANDIDATE
   2 | Manideep1  | manideep1@example.com    | CANDIDATE
   3 | testuser   | test@example.com         | CANDIDATE
   4 | admin      | admin@example.com        | HR
   5 | panelist1  | panelist1@example.com    | PANELIST
   6 | panelist2  | panelist2@example.com    | PANELIST
   ```

If roles are wrong in database, the database wasn't recreated!

## 🎯 Register New Users (After Backend Restart)

After backend is restarted with new code, you can register new users:

### Register as HR:
1. Go to Register page
2. Fill in details
3. Select "💼 HR" from dropdown
4. Register
5. Login → Should show HR dashboard

### Register as Panelist:
1. Go to Register page
2. Fill in details
3. Select "👨‍💼 Panelist" from dropdown
4. Register
5. Login → Should show PANELIST dashboard

## 📋 Checklist

- [ ] Backend stopped (Ctrl+C)
- [ ] Database files deleted (testdb.mv.db, testdb.trace.db)
- [ ] Backend restarted (mvn spring-boot:run)
- [ ] Saw "Creating test users..." messages
- [ ] Tested login with admin/admin123
- [ ] Saw "You have successfully logged in as HR"
- [ ] Saw HR-specific tabs (Panelists, Candidates)
- [ ] Tested login with panelist1/panelist123
- [ ] Saw "You have successfully logged in as PANELIST"
- [ ] Saw Panelist-specific tabs (Assigned Candidates, HR Information)

## ✅ Success Criteria

When everything is working:
- HR login shows: "You have successfully logged in as HR"
- Panelist login shows: "You have successfully logged in as PANELIST"
- Candidate login shows: "You have successfully logged in as CANDIDATE"
- Each role sees different tabs in dashboard
- User Info tab shows correct role

---

**Made with Bob** 🤖