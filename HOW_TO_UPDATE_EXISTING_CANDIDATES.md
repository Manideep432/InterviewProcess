# 🔧 How to Update Existing Candidates with JRS and Candidate Type Data

## 📋 Problem
Existing candidates in the database show "N/A" for JRS and Candidate Type because these fields were added after the candidates were created.

## ✅ Solution
Update the existing candidates in the database using the H2 Console.

---

## 🚀 Method 1: Using H2 Database Console (RECOMMENDED)

### Step 1: Make Sure Backend is Running
```bash
cd backend
mvn spring-boot:run
```

### Step 2: Access H2 Console
Open your browser and go to:
```
http://localhost:8081/h2-console
```

### Step 3: Login to H2 Console
Use these connection details:

- **JDBC URL:** `jdbc:h2:file:./data/logindb`
- **User Name:** `sa`
- **Password:** (leave empty)

Click **"Connect"**

### Step 4: Run the Update Script

Copy and paste this SQL script into the H2 Console:

```sql
-- Update existing candidates with JRS and Candidate Type

UPDATE candidates 
SET jrs = 'JR-2024-001', candidate_type = 'EXTERNAL' 
WHERE email = 'john.doe@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-002', candidate_type = 'REFERRAL' 
WHERE email = 'jane.smith@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-003', candidate_type = 'EXTERNAL' 
WHERE email = 'bob.johnson@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-004', candidate_type = 'AGENCY' 
WHERE email = 'alice.williams@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-005', candidate_type = 'INTERNAL' 
WHERE email = 'michael.brown@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-006', candidate_type = 'REFERRAL' 
WHERE email = 'sarah.davis@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-007', candidate_type = 'EXTERNAL' 
WHERE email = 'manideep@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-008', candidate_type = 'EXTERNAL' 
WHERE email = 'manideep1@example.com';

UPDATE candidates 
SET jrs = 'JR-2024-009', candidate_type = 'EXTERNAL' 
WHERE email = 'test@example.com';
```

Click **"Run"** button

### Step 5: Verify the Updates

Run this query to see the updated data:

```sql
SELECT id, name, email, jrs, candidate_type, position, status 
FROM candidates 
ORDER BY id;
```

You should see all candidates now have JRS and Candidate Type values!

### Step 6: Refresh HR Dashboard

1. Go to your application: `http://localhost:3000`
2. Login as HR: `admin` / `admin123`
3. Go to "Manage Candidates" tab
4. **Refresh the page** (F5 or Ctrl+R)
5. ✅ You should now see JRS and Candidate Type data instead of "N/A"!

---

## 🚀 Method 2: Using SQL File (Alternative)

If you prefer to use the SQL file directly:

### Step 1: Locate the SQL File
The file `UPDATE_EXISTING_CANDIDATES_WITH_JRS.sql` is in your project root directory.

### Step 2: Access H2 Console
```
http://localhost:8081/h2-console
```

### Step 3: Copy SQL Content
Open `UPDATE_EXISTING_CANDIDATES_WITH_JRS.sql` and copy all the content.

### Step 4: Paste and Run
Paste the content into H2 Console and click "Run".

---

## 🚀 Method 3: Quick Update for All Candidates

If you want to update ALL candidates at once with default values:

```sql
-- Update all candidates that don't have JRS
UPDATE candidates 
SET jrs = CONCAT('JR-2024-', LPAD(CAST(id AS VARCHAR), 3, '0'))
WHERE jrs IS NULL;

-- Update all candidates that don't have candidate_type
UPDATE candidates 
SET candidate_type = 'EXTERNAL'
WHERE candidate_type IS NULL;

-- Verify
SELECT id, name, email, jrs, candidate_type FROM candidates;
```

This will:
- Set JRS to `JR-2024-001`, `JR-2024-002`, etc. based on candidate ID
- Set all Candidate Types to `EXTERNAL` by default

---

## 📊 Expected Results After Update

| Candidate Name    | JRS          | Candidate Type | Position              |
|-------------------|--------------|----------------|-----------------------|
| John Doe          | JR-2024-001  | EXTERNAL       | Senior Java Developer |
| Jane Smith        | JR-2024-002  | REFERRAL       | React Developer       |
| Bob Johnson       | JR-2024-003  | EXTERNAL       | Full Stack Developer  |
| Alice Williams    | JR-2024-004  | AGENCY         | DevOps Engineer       |
| Michael Brown     | JR-2024-005  | INTERNAL       | Python Developer      |
| Sarah Davis       | JR-2024-006  | REFERRAL       | UI/UX Designer        |
| Manideep          | JR-2024-007  | EXTERNAL       | (Position)            |
| Manideep1         | JR-2024-008  | EXTERNAL       | (Position)            |
| testuser          | JR-2024-009  | EXTERNAL       | (Position)            |

---

## 🔍 Troubleshooting

### Issue: Can't access H2 Console

**Check application.properties:**
```properties
# Should have these settings
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Restart backend if you changed these settings.**

### Issue: "Table not found" error

**Solution:**
1. Make sure backend is running
2. Check the JDBC URL is correct: `jdbc:h2:file:./data/logindb`
3. Try without the `./` prefix: `jdbc:h2:file:data/logindb`

### Issue: Still showing "N/A" after update

**Solution:**
1. Verify the SQL ran successfully (check for success message)
2. Run the SELECT query to confirm data is in database
3. **Hard refresh the browser:** `Ctrl+Shift+R` (Windows) or `Cmd+Shift+R` (Mac)
4. Clear browser cache
5. Logout and login again

### Issue: Some candidates not updated

**Solution:**
Check the email addresses in your database:
```sql
SELECT id, name, email FROM candidates;
```

Then update the SQL script with the correct email addresses.

---

## ✅ Verification Steps

1. ✅ Run SQL updates in H2 Console
2. ✅ Verify with SELECT query
3. ✅ Refresh HR Dashboard page
4. ✅ Check Manage Candidates table
5. ✅ Confirm JRS and Candidate Type columns show data (not "N/A")
6. ✅ Test creating a new candidate (should save JRS and Candidate Type)
7. ✅ Test editing a candidate (should update JRS and Candidate Type)

---

## 🎯 Quick Commands Summary

```bash
# 1. Start backend
cd backend
mvn spring-boot:run

# 2. Access H2 Console
# Open browser: http://localhost:8081/h2-console

# 3. Login with:
# JDBC URL: jdbc:h2:file:./data/logindb
# User: sa
# Password: (empty)

# 4. Run the UPDATE SQL script

# 5. Refresh HR Dashboard
# Open: http://localhost:3000
# Login: admin / admin123
# Go to Manage Candidates
# Press F5 to refresh
```

---

## 📝 Notes

- **No need to restart backend** after running SQL updates
- **No need to restart frontend** after running SQL updates
- **Just refresh the browser page** to see the updated data
- The SQL script is safe to run multiple times (it will just update the same records)
- You can customize the JRS and Candidate Type values as needed

---

## 🎉 Success!

After following these steps, your Manage Candidates table should display:
- ✅ JRS values (e.g., JR-2024-001, JR-2024-002, etc.)
- ✅ Candidate Type values (EXTERNAL, INTERNAL, REFERRAL, AGENCY)
- ✅ No more "N/A" for existing candidates!

---

**Made with ❤️ by Bob**