# HR Add Candidate - Email Field with OTP Feature

## Overview
This feature adds an **Email field** to the HR Dashboard's "Add New Candidate" form. When HR creates a new candidate, the candidate will receive an **OTP (One-Time Password)** at the provided email address for secure login.

## What's New

### Frontend Changes (HRDashboard.js)

1. **New Email Field Added**
   - Location: Personal Information section, between Name and Phone fields
   - Field Type: Email input with validation
   - Required: Yes
   - Placeholder: `candidate@example.com`
   - Helper Text: "📧 OTP will be sent to this email for candidate login"

2. **Form State Updated**
   ```javascript
   const [newCandidate, setNewCandidate] = useState({
     name: '',
     email: '',        // NEW FIELD
     phone: '',
     position: '',
     // ... other fields
   });
   ```

3. **Validation Enhanced**
   - Email is now a required field
   - Form validates email presence before submission

### Backend Changes (HRService.java)

1. **Email Parameter Added**
   - The service now accepts `email` from the request
   - Email validation added with regex pattern
   - Email format validation: `^[A-Za-z0-9+_.-]+@(.+)$`

2. **OTP Functionality**
   - OTP is automatically generated and sent to the candidate's email
   - OTP is valid for 5 minutes
   - Candidate uses this OTP to complete their first login

## How It Works

### Step 1: HR Creates Candidate
1. HR logs into the dashboard
2. Navigates to "Add New Candidate" tab
3. Fills in the form including:
   - **Name** (required)
   - **Email** (required) ← NEW FIELD
   - **Phone** (required)
   - **Position** (required)
   - **Username** (required)
   - **Password** (required)
   - Other optional fields

### Step 2: Backend Processing
1. Backend receives the candidate data with email
2. Creates a User account with role "CANDIDATE"
3. Creates a Candidate profile linked to the HR
4. **Generates a 6-digit OTP**
5. **Sends OTP email to the candidate's email address**

### Step 3: Candidate Receives OTP
The candidate receives an email with:
- Subject: "Your Login OTP"
- OTP code (6 digits)
- Username for login
- Instructions to login

### Step 4: Candidate Login
1. Candidate visits the login page
2. Enters username and OTP
3. System verifies OTP
4. Candidate gains access to their dashboard

## API Changes

### Endpoint: POST `/api/hr/{hrId}/create-candidate`

**Request Body (Updated):**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",  // NEW REQUIRED FIELD
  "phone": "1234567890",
  "position": "Java Developer",
  "experienceYears": 5,
  "skills": "Java, Spring Boot, React",
  "currentCtc": 1200000,
  "hrMailId": "hr@company.com",
  "jdDetails": "Job description...",
  "employmentType": "FULL_TIME",
  "location": "Bangalore",
  "username": "johndoe",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Candidate created successfully. Login credentials sent via email.",
  "candidate": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "position": "Java Developer",
    "status": "APPLIED"
  },
  "user": {
    "id": 10,
    "username": "johndoe",
    "email": "john.doe@example.com",
    "role": "CANDIDATE"
  },
  "otpSent": true
}
```

## Testing the Feature

### Prerequisites
1. Backend server running on port 8081
2. Frontend server running on port 3000
3. Email service configured (check `application.properties`)
4. HR account created and logged in

### Test Steps

#### Test 1: Create Candidate with Email
1. Login as HR
2. Go to "Add New Candidate" tab
3. Fill in all required fields:
   ```
   Name: Test Candidate
   Email: testcandidate@example.com
   Phone: 9876543210
   Position: Software Engineer
   Username: testuser
   Password: Test@1234
   ```
4. Click "Create Candidate"
5. **Expected Result:**
   - Success message appears
   - Candidate is created
   - OTP email is sent to `testcandidate@example.com`

#### Test 2: Verify Email Validation
1. Try to submit form without email
2. **Expected Result:** Validation error "Please fill in all required fields"

3. Try to submit with invalid email format (e.g., "notanemail")
4. **Expected Result:** Browser validation error

#### Test 3: Check OTP Email
1. After creating candidate, check the email inbox for `testcandidate@example.com`
2. **Expected Email Content:**
   - Subject: "Your Login OTP"
   - Body contains 6-digit OTP
   - Body contains username
   - Instructions for login

#### Test 4: Candidate Login with OTP
1. Go to login page
2. Enter username: `testuser`
3. Enter the OTP received in email
4. Click Login
5. **Expected Result:**
   - Successful login
   - Redirected to Candidate Dashboard

### Backend Console Logs
When a candidate is created, you should see:
```
=== Creating New Candidate ===
HR ID: 1
Request: {name=Test Candidate, email=testcandidate@example.com, ...}
OTP generated and sent to: testcandidate@example.com | OTP: 123456
Candidate created successfully
==============================
```

## Email Configuration

Ensure your `application.properties` has email settings:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Security Features

1. **Email Validation**: Email format is validated on both frontend and backend
2. **OTP Expiry**: OTP expires after 5 minutes
3. **One-Time Use**: Each OTP can only be used once
4. **Rate Limiting**: Maximum 5 OTP requests per hour per email
5. **Secure Password**: Password must be at least 8 characters

## Troubleshooting

### Issue: OTP Email Not Received
**Solutions:**
1. Check email configuration in `application.properties`
2. Check spam/junk folder
3. Verify email service is running
4. Check backend console for email sending errors

### Issue: "Email already exists" Error
**Solution:** The email is already registered. Use a different email address.

### Issue: OTP Verification Failed
**Solutions:**
1. Check if OTP has expired (5 minutes)
2. Ensure OTP is entered correctly (6 digits)
3. Request a new OTP if needed

### Issue: Email Field Not Showing
**Solution:** Clear browser cache and refresh the page

## Database Schema

The Candidate table already has an `email` field:
```sql
CREATE TABLE candidates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,  -- Used for OTP
    phone VARCHAR(20),
    position VARCHAR(100),
    -- ... other fields
);
```

## Benefits

1. **Real Email Addresses**: Candidates use their actual email instead of auto-generated ones
2. **Secure Login**: OTP-based authentication for first login
3. **Better Communication**: HR can communicate with candidates via their real email
4. **Professional**: More professional than auto-generated emails like `username@candidate.com`
5. **Verification**: Email verification through OTP ensures valid email addresses

## Future Enhancements

1. Email verification link (in addition to OTP)
2. Resend OTP functionality
3. Email templates customization
4. SMS OTP as alternative
5. Email change functionality for candidates

## Related Files

### Frontend
- `frontend/src/components/HRDashboard.js` - Main form component
- `frontend/src/components/HRDashboard.css` - Styling

### Backend
- `backend/src/main/java/com/login/controller/HRController.java` - API endpoint
- `backend/src/main/java/com/login/service/HRService.java` - Business logic
- `backend/src/main/java/com/login/service/OtpService.java` - OTP generation
- `backend/src/main/java/com/login/service/EmailService.java` - Email sending
- `backend/src/main/java/com/login/model/Candidate.java` - Candidate entity

## Support

For issues or questions:
1. Check backend console logs
2. Check frontend browser console
3. Verify email service configuration
4. Review this documentation

---

**Made with ❤️ by Bob**