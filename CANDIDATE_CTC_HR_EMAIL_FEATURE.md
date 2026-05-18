# Candidate CTC and HR Email Feature

## Overview
This feature allows candidates to enter their Current CTC and HR email address when filling out their information. When the candidate clicks the Save button, their complete details are automatically sent to the specified HR email address as a PDF attachment.

## Features Implemented

### 1. New Fields Added
- **Current CTC**: Allows candidates to enter their current Cost to Company (in LPA - Lakhs Per Annum)
- **HR Mail ID**: Email address of the HR to whom the candidate details should be sent

### 2. PDF Generation
- Automatically generates a professional PDF document with all candidate details
- Includes candidate information such as:
  - Candidate ID
  - Name
  - Email
  - Phone
  - Location
  - Current CTC
  - Position
  - Experience
  - Skills
  - Status
  - Employment Type
  - And more...

### 3. Email Notification
- Sends the PDF document as an email attachment to the HR email address
- Email includes a professional message informing HR about the new candidate registration
- HR can view the candidate details in the PDF without logging into the system

### 4. HR Dashboard Integration
- Candidate details automatically appear in the HR Dashboard
- HR can view all candidate information when they log in with their credentials

## Technical Implementation

### Backend Changes

#### 1. Database Model (`Candidate.java`)
Added two new fields:
```java
@Column(name = "current_ctc", precision = 10, scale = 2)
private BigDecimal currentCtc;

@Column(name = "hr_mail_id", length = 100)
private String hrMailId;
```

#### 2. DTO (`CandidateInfoRequest.java`)
Added fields to accept the new data:
```java
private String currentCtc;
private String hrMailId;
```

#### 3. PDF Generation Service (`PdfGenerationService.java`)
New service created to generate professional PDF documents:
- Uses iText7 library for PDF generation
- Creates a formatted table with candidate details
- Includes header, timestamp, and footer
- Professional styling with colors and formatting

#### 4. Email Service (`EmailService.java`)
Enhanced to send emails with PDF attachments:
```java
public void sendCandidateDetailsToHR(String hrEmail, String candidateName, byte[] pdfContent)
```

#### 5. Candidate Service (`CandidateService.java`)
Updated to:
- Accept new fields (currentCtc, hrMailId)
- Generate PDF after saving candidate
- Send email to HR with PDF attachment
- Handle errors gracefully

#### 6. Controller (`CandidateController.java`)
Updated endpoint to accept new parameters:
```java
@PostMapping("/save-info")
public ResponseEntity<?> saveCandidateInfo(
    @RequestParam("currentCtc") String currentCtc,
    @RequestParam("hrMailId") String hrMailId,
    // ... other parameters
)
```

### Frontend Changes

#### 1. Component (`CandidateInfo.js`)
Added two new input fields:
- Current CTC input field
- HR Mail ID input field
- Updated form submission to include new fields
- Enhanced success message to indicate email was sent

#### 2. Styling (`CandidateInfo.css`)
- Existing CSS automatically styles the new fields
- Responsive design maintained
- Consistent with the modern light blue theme

## Dependencies Added

### Maven Dependency (pom.xml)
```xml
<!-- iText PDF for PDF Generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

## How to Use

### For Candidates:

1. **Login** as a candidate
2. Navigate to **Candidate Info** tab
3. Fill in all required information:
   - Candidate Name
   - Mail ID
   - Phone Number
   - Location
   - **Current CTC** (e.g., 5.5 for 5.5 LPA)
   - **HR Mail ID** (e.g., hr@company.com)
   - Upload Photo (JPG/PNG)
   - Upload CV (PDF)
   - Upload Government ID (JPG)
4. Click **Save** button
5. Success message will confirm that details were sent to HR via email

### For HR:

1. **Check Email**: HR will receive an email with subject "New Candidate Registration - [Candidate Name]"
2. **Open PDF Attachment**: The email contains a PDF with complete candidate details
3. **Review Details**: All candidate information is formatted in a professional table
4. **Login to Dashboard**: HR can also log in to the HR Dashboard to view candidate details

## Email Configuration

Ensure your `application.properties` has email configuration:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Testing the Feature

### 1. Start Backend
```bash
cd backend
mvn spring-boot:run
```

### 2. Start Frontend
```bash
cd frontend
npm start
```

### 3. Test Flow
1. Register/Login as a candidate
2. Fill candidate information form with all fields including Current CTC and HR Mail ID
3. Click Save
4. Check the HR email inbox for the PDF attachment
5. Login as HR and verify candidate appears in HR Dashboard

## Error Handling

- If email sending fails, the candidate data is still saved
- User receives appropriate error messages
- PDF generation errors are logged
- Invalid CTC format is validated

## Security Considerations

- Email addresses are validated
- PDF generation is done server-side
- File uploads are validated and stored securely
- Authentication required for all operations

## Future Enhancements

Potential improvements:
- Email template customization
- Multiple HR email recipients
- PDF template customization
- Email delivery status tracking
- Notification preferences

## Troubleshooting

### PDF Not Generated
- Check iText7 dependency is properly added
- Verify PdfGenerationService is autowired correctly
- Check server logs for errors

### Email Not Sent
- Verify email configuration in application.properties
- Check if Gmail "Less secure app access" is enabled (or use App Password)
- Verify HR email address is valid
- Check spam/junk folder

### Fields Not Appearing
- Clear browser cache
- Restart frontend development server
- Check browser console for errors

## Files Modified

### Backend:
- `backend/pom.xml` - Added iText7 dependency
- `backend/src/main/java/com/login/model/Candidate.java` - Added fields
- `backend/src/main/java/com/login/dto/CandidateInfoRequest.java` - Added fields
- `backend/src/main/java/com/login/service/PdfGenerationService.java` - New file
- `backend/src/main/java/com/login/service/EmailService.java` - Enhanced
- `backend/src/main/java/com/login/service/CandidateService.java` - Updated
- `backend/src/main/java/com/login/controller/CandidateController.java` - Updated

### Frontend:
- `frontend/src/components/CandidateInfo.js` - Added fields and logic
- `frontend/src/components/CandidateInfo.css` - Already styled appropriately

## Support

For issues or questions:
1. Check server logs for detailed error messages
2. Verify all dependencies are installed
3. Ensure email configuration is correct
4. Test with a valid HR email address

---

**Made with Bob** ✨

Last Updated: May 14, 2026