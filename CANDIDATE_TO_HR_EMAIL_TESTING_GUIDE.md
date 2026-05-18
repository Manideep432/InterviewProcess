# Candidate to HR Email Testing Guide

## Overview
When a candidate saves their information, the system automatically:
1. Saves the candidate data to the database
2. Generates a professional PDF with all candidate details
3. Sends the PDF as an email attachment to the HR email address provided by the candidate
4. Makes the candidate visible in the HR Dashboard

## Current Implementation Status ✅

### Backend Features:
- ✅ PDF Generation Service (using iText7)
- ✅ Email Service with attachment support
- ✅ Candidate Service with auto-email functionality
- ✅ Controller endpoint `/api/candidates/save-info`
- ✅ Email configuration in application.properties

### Frontend Features:
- ✅ Current CTC input field
- ✅ HR Mail ID input field
- ✅ Success message indicating email was sent
- ✅ Form validation and file upload support

## How It Works

### Step-by-Step Flow:

1. **Candidate Fills Form**
   - Candidate Name
   - Mail ID
   - Phone Number
   - Location
   - **Current CTC** (e.g., 5.5 for 5.5 LPA)
   - **HR Mail ID** (e.g., hr@company.com)
   - Photo (JPG/PNG)
   - CV (PDF)
   - Government ID (JPG)

2. **Candidate Clicks Save**
   - Frontend sends data to backend via POST request
   - Backend saves candidate to database
   - Backend generates PDF with all candidate details
   - Backend sends email to HR with PDF attachment

3. **HR Receives Email**
   - Subject: "New Candidate Registration - [Candidate Name]"
   - Body: Professional message about new candidate
   - Attachment: PDF with complete candidate details

4. **HR Can View in Dashboard**
   - HR logs in to their dashboard
   - Candidate appears in the candidates list
   - HR can view all details and assign panelist

## Testing Instructions

### Prerequisites:
1. Backend server running on port 8081
2. Frontend server running on port 3000
3. Email configuration verified in `application.properties`

### Test Scenario 1: Complete Flow Test

**Step 1: Start Servers**
```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm start
```

**Step 2: Login as Candidate**
1. Open browser: http://localhost:3000
2. Login with candidate credentials
   - Username: candidate
   - Password: candidate123
3. Enter OTP received via email

**Step 3: Fill Candidate Information**
1. Click on "Candidate Info" tab
2. Fill all fields:
   - Candidate Name: John Doe
   - Mail ID: john.doe@example.com
   - Phone Number: 9876543210
   - Location: Bangalore
   - **Current CTC: 5.5** (in LPA)
   - **HR Mail ID: hr@company.com** (or your test email)
3. Upload files:
   - Photo (JPG/PNG)
   - CV (PDF)
   - Government ID (JPG)

**Step 4: Save and Verify**
1. Click "Save" button
2. Wait for success message:
   - "✓ Saved successfully! Your details have been sent to HR via email as PDF and will appear in their dashboard."
3. Check backend console for logs:
   ```
   Candidate details email sent successfully to HR: hr@company.com
   ```

**Step 5: Check HR Email**
1. Open the HR email inbox (hr@company.com)
2. Look for email with subject: "New Candidate Registration - John Doe"
3. Verify email contains:
   - Professional message
   - PDF attachment named: "Candidate_Details_John_Doe.pdf"
4. Open PDF and verify all details are present

**Step 6: Verify in HR Dashboard**
1. Logout from candidate account
2. Login as HR:
   - Username: hr
   - Password: hr123
3. Navigate to HR Dashboard
4. Verify "John Doe" appears in candidates list
5. Click to view full details

### Test Scenario 2: Email Failure Handling

**Test with Invalid Email:**
1. Login as candidate
2. Fill form with invalid HR email: "invalid-email"
3. Click Save
4. System should still save candidate data
5. Check console for error message (email won't be sent)

**Test with Empty HR Email:**
1. Login as candidate
2. Fill form but leave HR Mail ID empty
3. Click Save
4. Candidate data is saved
5. No email is sent (expected behavior)

### Test Scenario 3: Multiple Candidates

1. Save multiple candidates with different HR emails
2. Each HR should receive only their respective candidate's PDF
3. All candidates should appear in HR Dashboard

## Troubleshooting

### Issue: Email Not Received

**Check 1: Email Configuration**
```properties
# Verify in application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=sravansiripalli@gmail.com
spring.mail.password=mgfgcqwxrobfetsd
```

**Check 2: Gmail Settings**
- Ensure "Less secure app access" is enabled OR
- Use App Password instead of regular password
- Check if 2-factor authentication is enabled

**Check 3: Backend Logs**
Look for these messages:
```
✅ Success: "Candidate details email sent successfully to HR: [email]"
❌ Error: "Failed to send candidate details email: [error message]"
```

**Check 4: Spam/Junk Folder**
- Check HR email's spam/junk folder
- Mark as "Not Spam" if found

**Check 5: Email Address Format**
- Ensure HR email is valid format: user@domain.com
- No spaces or special characters

### Issue: PDF Not Generated

**Check 1: iText7 Dependency**
Verify in `pom.xml`:
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

**Check 2: Rebuild Project**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Check 3: Backend Logs**
Look for PDF generation errors:
```
Failed to generate PDF: [error message]
```

### Issue: Form Not Submitting

**Check 1: Network Tab**
- Open browser DevTools (F12)
- Go to Network tab
- Click Save button
- Check for POST request to `/api/candidates/save-info`
- Verify response status (should be 200)

**Check 2: Console Errors**
- Check browser console for JavaScript errors
- Verify token is present in localStorage

**Check 3: CORS Issues**
Verify in `application.properties`:
```properties
cors.allowed.origins=http://localhost:3000
```

## Expected Email Content

### Email Subject:
```
New Candidate Registration - John Doe
```

### Email Body:
```
Dear HR,

A new candidate has registered in the system.

Candidate Name: John Doe

Please find the complete candidate details in the attached PDF document.

You can also view this candidate's information in your HR Dashboard.

Best regards,
Login Microservice Team

---
This is an automated email. Please do not reply.
```

### PDF Content:
```
CANDIDATE INFORMATION
Generated on: 14-05-2026 16:37:23

Field                   | Value
------------------------|---------------------------
Candidate ID            | 7
Name                    | John Doe
Email                   | john.doe@example.com
Phone                   | 9876543210
Location                | Bangalore
Current CTC             | ₹ 5.5
Position                | N/A
Experience (Years)      | N/A
Skills                  | Awaiting details
Status                  | APPLIED
Employment Type         | N/A

This is an auto-generated document.
```

## Verification Checklist

- [ ] Backend server is running
- [ ] Frontend server is running
- [ ] Email configuration is correct
- [ ] Candidate can login successfully
- [ ] Candidate Info form loads properly
- [ ] All fields are fillable
- [ ] Files can be uploaded
- [ ] Save button works
- [ ] Success message appears
- [ ] Backend logs show email sent
- [ ] HR receives email
- [ ] PDF attachment is present
- [ ] PDF opens correctly
- [ ] All candidate details in PDF
- [ ] Candidate appears in HR Dashboard
- [ ] HR can view candidate details

## Common Test Emails

For testing, you can use:
- Your own email address
- Gmail test account
- Temporary email services (for testing only)

**Example Test:**
```
Candidate Email: candidate@test.com
HR Email: your-email@gmail.com (use your actual email)
Current CTC: 5.5
```

## Success Indicators

✅ **Frontend Success:**
- Green success message appears
- Message says "sent to HR via email as PDF"

✅ **Backend Success:**
- Console log: "Candidate details email sent successfully to HR"
- No error messages in logs

✅ **Email Success:**
- Email received in HR inbox
- PDF attachment present
- PDF opens without errors
- All data visible in PDF

✅ **Database Success:**
- Candidate saved with all fields
- Current CTC stored correctly
- HR Mail ID stored correctly

## Additional Notes

### Email Sending is Asynchronous
- Email is sent after candidate is saved
- If email fails, candidate data is still saved
- This prevents data loss due to email issues

### PDF Generation
- PDF is generated in-memory (not saved to disk)
- PDF is attached directly to email
- Professional formatting with tables and colors

### Security
- Only authenticated candidates can save info
- JWT token required for API access
- File uploads are validated
- Email addresses are validated

## Support

If you encounter issues:
1. Check backend console logs
2. Check browser console
3. Verify email configuration
4. Test with a different email address
5. Check spam/junk folder
6. Restart backend server

---

**Made with Bob** ✨

Last Updated: May 14, 2026