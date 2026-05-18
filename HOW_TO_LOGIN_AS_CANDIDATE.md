# How to Login as CANDIDATE

## Step-by-Step Guide

### Step 1: Start the Application

**Start Backend (Port 8081):**
```bash
cd backend
mvn spring-boot:run
```
Wait for: `Started LoginApplication in X seconds`

**Start Frontend (Port 3000):**
```bash
cd frontend
npm start
```
Browser will open at: `http://localhost:3000`

---

### Step 2: Register as CANDIDATE

1. **Click "Register" link** on the login page
2. **Fill in the registration form:**
   - **Username**: `candidate1` (or any username you prefer)
   - **Email**: `your-email@gmail.com` (use a real email to receive OTP)
   - **Password**: `SecurePass123!` (must meet password requirements)
   - **Role**: Select **"Candidate"** from dropdown (this is the default)
3. **Click "Register" button**
4. You'll be automatically logged in and redirected to the dashboard

---

### Step 3: Login as CANDIDATE (After Registration)

#### Option A: Login with Email OTP (Recommended)

1. **Go to Login Page**: `http://localhost:3000`
2. **Enter Credentials:**
   - Username: `candidate1`
   - Password: `SecurePass123!`
3. **Click "Request OTP" button**
4. **Check your email** for 6-digit OTP code
5. **Enter the OTP** in the verification field
6. **Click "Verify & Login" button**
7. **Success!** You'll see the CANDIDATE dashboard with:
   - ℹ️ INFO tab
   - 💬 Feedback tab
   - (No Security tab - only HR sees this)

---

## What You'll See as CANDIDATE

### Dashboard View:
```
┌─────────────────────────────────────────┐
│  🎉 Welcome to Dashboard!               │
│  You have successfully logged in as     │
│  CANDIDATE                              │
├─────────────────────────────────────────┤
│  [ℹ️ INFO] [💬 Feedback]               │
├─────────────────────────────────────────┤
│  👤 Username: candidate1                │
│  📧 Email: your-email@gmail.com         │
│  👔 Role: CANDIDATE                     │
│                                         │
│  [🚪 Logout]                            │
└─────────────────────────────────────────┘
```

### Available Tabs:
- **INFO Tab**: Shows your user information
- **Feedback Tab**: Submit feedback (textarea + submit button)
- **NO Security Tab**: This is only visible to HR users

---

## Quick Test Accounts

### Create Test CANDIDATE Account:
```bash
# Registration details
Username: testcandidate
Email: your-email@gmail.com
Password: TestPass123!
Role: Candidate (select from dropdown)
```

### Create Test HR Account (for comparison):
```bash
# Registration details
Username: testhr
Email: your-email@gmail.com
Password: TestPass123!
Role: HR (select from dropdown)
```

---

## Troubleshooting

### Problem: "Invalid role" error
**Solution**: Make sure you selected "Candidate" from the dropdown during registration

### Problem: Not receiving OTP email
**Solution**: 
- Check spam/junk folder
- Verify email is correct
- Backend must be running on port 8081
- Check backend logs for email sending errors

### Problem: Still seeing Security tab as CANDIDATE
**Solution**:
- Clear browser localStorage: `localStorage.clear()`
- Logout and login again
- Check that role is "CANDIDATE" in localStorage:
  ```javascript
  // In browser console:
  JSON.parse(localStorage.getItem('user'))
  // Should show: { username: "...", email: "...", role: "CANDIDATE" }
  ```

### Problem: Backend not running
**Solution**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

---

## Password Requirements

Your password must contain:
- ✅ At least 8 characters
- ✅ At least one uppercase letter (A-Z)
- ✅ At least one lowercase letter (a-z)
- ✅ At least one digit (0-9)
- ✅ At least one special character (!@#$%^&*)

**Valid Examples:**
- `SecurePass123!`
- `Candidate@2024`
- `MyPass#456`

---

## API Endpoints Used

### Registration:
```
POST http://localhost:8081/api/auth/register
Body: {
  "username": "candidate1",
  "email": "your-email@gmail.com",
  "password": "SecurePass123!",
  "role": "CANDIDATE"
}
```

### Login (Request OTP):
```
POST http://localhost:8081/api/auth/login/request-otp
Body: {
  "username": "candidate1",
  "password": "SecurePass123!"
}
```

### Login (Verify OTP):
```
POST http://localhost:8081/api/auth/login/verify-otp
Body: {
  "username": "candidate1",
  "password": "SecurePass123!",
  "otp": "123456"
}
```

---

## Testing Role-Based Access

### Test 1: Login as CANDIDATE
1. Register with role "Candidate"
2. Login
3. **Expected**: See only INFO and Feedback tabs
4. **Expected**: No Security tab visible

### Test 2: Login as HR
1. Register with role "HR"
2. Login
3. **Expected**: See INFO, Feedback, AND Security tabs
4. **Expected**: Security tab shows MFA setup

### Test 3: Switch Between Roles
1. Logout from CANDIDATE account
2. Login with HR account
3. **Expected**: Security tab appears
4. Logout from HR account
5. Login with CANDIDATE account
6. **Expected**: Security tab disappears

---

## Video Walkthrough (Text Version)

```
1. Open browser → http://localhost:3000
2. Click "Register" link
3. Fill form:
   - Username: candidate1
   - Email: your-email@gmail.com
   - Password: SecurePass123!
   - Role: Candidate ← SELECT THIS
4. Click "Register"
5. Auto-login → Dashboard appears
6. See tabs: [INFO] [Feedback] (no Security)
7. Click INFO → See your details
8. Click Feedback → See feedback form
9. Click Logout
10. Login again with same credentials
11. Request OTP → Check email
12. Enter OTP → Verify
13. Dashboard appears with same 2 tabs
```

---

## Common Questions

**Q: Can I change my role after registration?**
A: No, role is set during registration and cannot be changed.

**Q: What's the difference between CANDIDATE and HR?**
A: 
- CANDIDATE: See INFO and Feedback tabs only
- HR: See INFO, Feedback, AND Security tabs (MFA setup)

**Q: Do I need to use OTP every time?**
A: Yes, for security. Each login requires a new OTP sent to your email.

**Q: Can I have multiple CANDIDATE accounts?**
A: Yes, register with different usernames and emails.

**Q: What if I forget my password?**
A: Use the "Forgot Password?" link on login page to reset via email OTP.

---

**Made with Bob** 🤖