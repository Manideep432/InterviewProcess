# HR Add Candidate Feature - Quick Start Guide

## 🚀 Quick Setup & Testing

### Prerequisites
- Backend server running on `http://localhost:8081`
- Frontend server running on `http://localhost:3000`
- HR account created and logged in

### Step-by-Step Testing

#### 1. Start the Application
```bash
# Terminal 1 - Start Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Start Frontend
cd frontend
npm start
```

#### 2. Login as HR
1. Open browser: `http://localhost:3000`
2. Click "Login"
3. Enter HR credentials:
   - Username: `admin` (or your HR username)
   - Password: `Admin@123` (or your HR password)
   - Role: HR
4. Click "Login"

#### 3. Navigate to Add Candidate Tab
1. You should see HR Dashboard
2. Click on "➕ Add New Candidate" tab
3. Form will appear with HR email pre-filled

#### 4. Fill the Form - Example Data

**Personal Information:**
- Name: `John Doe`
- Phone: `9876543210`
- Position: `Java Developer`
- Experience: `5`
- Skills: `Java, Spring Boot, React, MySQL`

**Job Details:**
- Current CTC: `1200000`
- Employment Type: `Full Time` (dropdown)
- Location: `Bangalore`
- HR Email: `admin@example.com` (pre-filled)
- Job Description: 
  ```
  Looking for experienced Java Developer with Spring Boot expertise.
  Must have 5+ years of experience in enterprise applications.
  ```

**Login Credentials:**
- Username: `johndoe`
- Password: `JohnDoe@123`

#### 5. Submit the Form
1. Click "✅ Create Candidate" button
2. Wait for success message
3. You should see: "✅ Candidate created successfully! Username: johndoe"

#### 6. Verify Candidate Creation
1. Click on "📋 Candidate Details" tab
2. Look for the newly created candidate
3. Verify all information is correct

#### 7. Test Candidate Login
1. Click "Logout" from HR Dashboard
2. Go to Login page
3. Enter candidate credentials:
   - Username: `johndoe`
   - Password: `JohnDoe@123`
   - Role: CANDIDATE
4. Click "Login"
5. Verify candidate dashboard loads successfully

## 📋 Sample Test Data

### Test Case 1: Full-Time Developer
```
Name: Sarah Johnson
Phone: 9123456789
Position: Senior React Developer
Experience: 7
Skills: React, TypeScript, Node.js, MongoDB
Current CTC: 1800000
Employment Type: FULL_TIME
Location: Hyderabad
Username: sarahj
Password: Sarah@2024
```

### Test Case 2: Contract Position
```
Name: Mike Chen
Phone: 9234567890
Position: DevOps Engineer
Experience: 4
Skills: Docker, Kubernetes, AWS, Jenkins
Current CTC: 1500000
Employment Type: CONTRACT
Location: Pune
Username: mikechen
Password: Mike@DevOps123
```

### Test Case 3: Intern
```
Name: Priya Sharma
Phone: 9345678901
Position: Software Development Intern
Experience: 0
Skills: Python, Django, HTML, CSS
Current CTC: 300000
Employment Type: INTERN
Location: Mumbai
Username: priyas
Password: Priya@Intern2024
```

## ✅ Validation Tests

### Test 1: Missing Required Fields
1. Leave "Name" field empty
2. Try to submit
3. Expected: Browser validation error

### Test 2: Short Password
1. Enter password: `test123` (less than 8 chars)
2. Try to submit
3. Expected: "Password must be at least 8 characters long"

### Test 3: Duplicate Username
1. Create candidate with username `testuser`
2. Try creating another with same username
3. Expected: "Username already exists" error

### Test 4: Form Reset
1. Fill all fields
2. Click "🔄 Reset Form"
3. Expected: All fields cleared except HR email

## 🔍 Verification Checklist

After creating a candidate, verify:

- [ ] Success message appears with username
- [ ] Form resets automatically
- [ ] Candidate appears in "Candidate Details" tab
- [ ] Candidate can login with provided credentials
- [ ] Candidate email is `{username}@candidate.com`
- [ ] Candidate status is "APPLIED"
- [ ] All entered data is saved correctly
- [ ] Candidate is linked to the HR who created them

## 🐛 Troubleshooting

### Issue: "Failed to create candidate"
**Solution:** Check backend console for detailed error message

### Issue: Form doesn't submit
**Solution:** 
1. Check browser console for errors
2. Verify all required fields are filled
3. Ensure password is at least 8 characters

### Issue: Candidate can't login
**Solution:**
1. Verify username is correct (case-sensitive)
2. Verify password is correct
3. Select "CANDIDATE" role in login form
4. Check if user was created in database

### Issue: "Invalid HR ID"
**Solution:**
1. Logout and login again
2. Verify you're logged in as HR role
3. Check JWT token is valid

## 📊 Database Verification

### Check User Table
```sql
SELECT * FROM users WHERE role = 'CANDIDATE' ORDER BY created_at DESC;
```

### Check Candidate Table
```sql
SELECT * FROM candidates ORDER BY created_at DESC;
```

### Verify Linking
```sql
SELECT 
    c.name, 
    c.email, 
    c.position, 
    u.username,
    h.username as hr_username
FROM candidates c
JOIN users u ON c.email = u.email
JOIN users h ON c.hr_id = h.id
WHERE u.role = 'CANDIDATE';
```

## 🎯 Success Criteria

The feature is working correctly if:

1. ✅ HR can access "Add New Candidate" tab
2. ✅ Form displays all required fields
3. ✅ Form validation works (required fields, password length)
4. ✅ Candidate is created successfully
5. ✅ User account is created with CANDIDATE role
6. ✅ Password is encrypted in database
7. ✅ Candidate can login with provided credentials
8. ✅ Candidate appears in HR's candidate list
9. ✅ Form resets after successful submission
10. ✅ Error messages display for validation failures

## 📞 Support

If you encounter any issues:
1. Check browser console (F12)
2. Check backend console logs
3. Verify database connections
4. Review error messages carefully
5. Refer to main documentation: `HR_ADD_CANDIDATE_FEATURE.md`

---

**Happy Testing! 🎉**

Created by: Bob  
Date: 2026-05-16