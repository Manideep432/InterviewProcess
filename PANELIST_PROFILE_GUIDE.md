# 📋 Panelist Profile Feature - Complete Guide

## Overview
The Panelist Profile feature displays comprehensive information about a panelist in the system. This profile is accessible from the Panelist Dashboard and shows all relevant details about the panelist's professional information.

---

## 🎯 Profile Information Displayed

### 1. **Basic User Information**
- **👤 Username**: The panelist's login username
- **📧 Email**: The panelist's email address (from User entity)

### 2. **Professional Details**
- **🎯 Specialization**: The panelist's area of specialization (e.g., "Java Developer", "Frontend Expert", "Cloud Architect")
- **💼 Experience**: Years of professional experience
- **🔧 Expertise**: Detailed description of technical skills and expertise areas

### 3. **System Information**
- **✅ Status**: Active/Inactive status indicator
  - Green badge for Active panelists
  - Red badge for Inactive panelists
- **🆔 Panelist ID**: Unique identifier in the system

### 4. **Assignment Information** (Backend)
- **Assigned HR**: The HR who created/assigned this panelist profile
- **Created At**: Timestamp when the profile was created
- **Updated At**: Last modification timestamp

---

## 🏗️ Technical Architecture

### Backend Components

#### 1. **Panelist Model** (`backend/src/main/java/com/login/model/Panelist.java`)
```java
@Entity
@Table(name = "panelists")
public class Panelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // User account with PANELIST role
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_hr_id", nullable = false)
    private User assignedHr; // HR who assigned this panelist
    
    @NotBlank(message = "Specialization is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String specialization;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(length = 500)
    private String expertise;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

#### 2. **API Endpoints** (`backend/src/main/java/com/login/controller/PanelistController.java`)

**Get Panelist by User ID:**
```
GET /api/panelists/user/{userId}
Authorization: Bearer <JWT_TOKEN>

Response:
{
  "success": true,
  "panelist": {
    "id": 4,
    "user": {
      "id": 5,
      "username": "panelist1",
      "email": "panelist1@example.com",
      "role": "PANELIST"
    },
    "assignedHr": {
      "id": 2,
      "username": "hr1",
      "email": "hr1@example.com"
    },
    "specialization": "Java Developer",
    "experienceYears": 5,
    "expertise": "Spring Boot, Microservices, REST APIs",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**Other Available Endpoints:**
- `GET /api/panelists/{id}` - Get panelist by panelist ID
- `GET /api/panelists/all` - Get all panelists
- `GET /api/panelists/hr/{hrId}` - Get panelists assigned by specific HR
- `GET /api/panelists/active` - Get all active panelists
- `PUT /api/panelists/{panelistId}` - Update panelist details
- `PUT /api/panelists/{panelistId}/toggle-status` - Toggle active status
- `DELETE /api/panelists/{panelistId}` - Delete panelist

#### 3. **Service Layer** (`backend/src/main/java/com/login/service/PanelistService.java`)
- `getPanelistByUserId(Long userId)` - Retrieves panelist profile by user ID
- `getPanelistById(Long id)` - Retrieves panelist by panelist ID
- `updatePanelist()` - Updates panelist information
- `togglePanelistStatus()` - Activates/deactivates panelist

---

### Frontend Components

#### 1. **PanelistDashboard Component** (`frontend/src/components/PanelistDashboard.js`)

**Profile Tab Implementation:**
```javascript
// Load panelist data on component mount
useEffect(() => {
  loadPanelistData();
}, []);

const loadPanelistData = async () => {
  setLoading(true);
  try {
    const response = await fetch(`${API_URL}/api/panelists/user/${user.id}`, {
      headers: {
        'Authorization': `Bearer ${authService.getToken()}`
      }
    });
    
    const data = await response.json();
    if (data.success) {
      setPanelistData(data.panelist);
    }
  } catch (err) {
    console.error('Error loading panelist data:', err);
    setError('Failed to load dashboard data.');
  } finally {
    setLoading(false);
  }
};
```

**Profile Display:**
```jsx
{activeTab === 'profile' && (
  <div className="tab-content">
    <h3>👤 Panelist Profile</h3>
    {loading ? (
      <p>Loading profile...</p>
    ) : panelistData ? (
      <div className="profile-info">
        <div className="profile-item">
          <span className="profile-label">👤 Username:</span>
          <span className="profile-value">{user.username}</span>
        </div>
        <div className="profile-item">
          <span className="profile-label">📧 Email:</span>
          <span className="profile-value">{user.email}</span>
        </div>
        <div className="profile-item">
          <span className="profile-label">🎯 Specialization:</span>
          <span className="profile-value">{panelistData.specialization || 'Not set'}</span>
        </div>
        <div className="profile-item">
          <span className="profile-label">💼 Experience:</span>
          <span className="profile-value">{panelistData.experienceYears || 0} years</span>
        </div>
        <div className="profile-item">
          <span className="profile-label">🔧 Expertise:</span>
          <span className="profile-value">{panelistData.expertise || 'Not set'}</span>
        </div>
        <div className="profile-item">
          <span className="profile-label">✅ Status:</span>
          <span className={`profile-value status ${panelistData.isActive ? 'active' : 'inactive'}`}>
            {panelistData.isActive ? 'Active' : 'Inactive'}
          </span>
        </div>
        <div className="profile-item">
          <span className="profile-label">🆔 Panelist ID:</span>
          <span className="profile-value">{panelistData.id}</span>
        </div>
      </div>
    ) : (
      <p>No profile data available</p>
    )}
  </div>
)}
```

#### 2. **Styling** (`frontend/src/components/PanelistDashboard.css`)
- Profile items displayed in a clean, organized layout
- Color-coded status badges (green for active, red for inactive)
- Responsive design for different screen sizes
- Professional appearance with proper spacing and typography

---

## 🔐 Security & Access Control

### Authentication Requirements
- User must be logged in with a valid JWT token
- User must have the `PANELIST` role
- Token is sent in the `Authorization` header: `Bearer <token>`

### Authorization
- Panelists can only view their own profile
- HR users can view and manage panelist profiles they created
- Admin users have full access to all panelist profiles

---

## 📊 Database Schema

### Panelists Table
```sql
CREATE TABLE panelists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    assigned_hr_id BIGINT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    experience_years INT,
    expertise VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_hr_id) REFERENCES users(id)
);
```

---

## 🎨 UI/UX Features

### Visual Elements
1. **Tab Navigation**: Easy switching between Home, Profile, New Interview, and Interviews
2. **Icon-based Labels**: Emojis for better visual recognition
3. **Status Indicators**: Color-coded badges for active/inactive status
4. **Loading States**: Spinner/message while data is being fetched
5. **Error Handling**: Clear error messages if data fails to load

### User Experience
- **Instant Access**: Profile loads automatically when dashboard opens
- **Real-time Data**: Always shows current profile information
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Professional Layout**: Clean, organized presentation of information

---

## 🚀 How to Access Panelist Profile

### For Panelists:
1. Login with panelist credentials
2. You'll be redirected to the Panelist Dashboard
3. Click on the **"👤 Panelist Profile"** tab
4. View your complete profile information

### For HR Users:
1. Login with HR credentials
2. Navigate to HR Dashboard
3. View panelist list
4. Click on a specific panelist to see their profile

---

## 🔧 Profile Management

### Creating a Panelist Profile
**Only HR users can create panelist profiles:**
```javascript
POST /api/panelists/create
{
  "userId": 5,
  "hrId": 2,
  "specialization": "Java Developer",
  "experienceYears": 5,
  "expertise": "Spring Boot, Microservices, REST APIs"
}
```

### Updating Profile Information
**HR can update panelist details:**
```javascript
PUT /api/panelists/{panelistId}
{
  "specialization": "Senior Java Developer",
  "experienceYears": 6,
  "expertise": "Spring Boot, Microservices, REST APIs, Cloud Architecture",
  "hrId": 2
}
```

### Toggling Active Status
**HR can activate/deactivate panelists:**
```javascript
PUT /api/panelists/{panelistId}/toggle-status?hrId=2
```

---

## 📝 Profile Fields Explained

| Field | Type | Required | Description | Example |
|-------|------|----------|-------------|---------|
| **Username** | String | Yes | Login username from User entity | "panelist1" |
| **Email** | String | Yes | Email address from User entity | "panelist1@example.com" |
| **Specialization** | String | Yes | Primary area of expertise | "Java Developer" |
| **Experience Years** | Integer | No | Years of professional experience | 5 |
| **Expertise** | String | No | Detailed skills description | "Spring Boot, Microservices" |
| **Status** | Boolean | Yes | Active/Inactive indicator | true/false |
| **Panelist ID** | Long | Yes | Unique system identifier | 4 |

---

## 🐛 Troubleshooting

### Profile Not Loading
**Issue**: Profile data doesn't appear
**Solutions**:
1. Ensure backend is running on port 8081
2. Check JWT token is valid
3. Verify user has PANELIST role
4. Check browser console for errors
5. Verify panelist record exists in database

### Missing Profile Data
**Issue**: Some fields show "Not set"
**Solutions**:
1. HR needs to update the panelist profile
2. Check if fields were provided during creation
3. Use the update endpoint to add missing information

### Status Shows Inactive
**Issue**: Profile shows inactive status
**Solutions**:
1. Contact HR to reactivate your profile
2. HR can use toggle-status endpoint
3. Check if there's a reason for deactivation

---

## 🎯 Best Practices

### For Panelists:
1. ✅ Review your profile regularly
2. ✅ Contact HR if information needs updating
3. ✅ Keep your email address current
4. ✅ Understand your specialization and expertise areas

### For HR Users:
1. ✅ Keep panelist information up-to-date
2. ✅ Provide detailed expertise descriptions
3. ✅ Update experience years annually
4. ✅ Only deactivate when necessary
5. ✅ Document reasons for status changes

### For Developers:
1. ✅ Always validate JWT tokens
2. ✅ Handle loading and error states
3. ✅ Implement proper error messages
4. ✅ Use consistent styling
5. ✅ Test with different data scenarios

---

## 📚 Related Features

- **Interview Management**: Schedule and manage interviews
- **HR Dashboard**: HR view of all panelists
- **User Authentication**: Login and JWT token management
- **Role-Based Access**: Different views for different roles

---

## 🔄 Future Enhancements

Potential improvements for the Panelist Profile feature:

1. **Profile Editing**: Allow panelists to update their own information
2. **Profile Picture**: Add photo upload capability
3. **Skills Tags**: Visual representation of technical skills
4. **Certifications**: Section for professional certifications
5. **Performance Metrics**: Interview success rates and feedback
6. **Availability Calendar**: Show panelist availability
7. **Contact Preferences**: Communication preferences
8. **Export Profile**: Download profile as PDF

---

## 📞 Support

If you encounter issues with the Panelist Profile feature:

1. Check this guide for troubleshooting steps
2. Review the backend logs for errors
3. Verify database connectivity
4. Contact your system administrator
5. Check the browser console for frontend errors

---

## 📄 Summary

The Panelist Profile feature provides a comprehensive view of panelist information including:
- ✅ Basic user details (username, email)
- ✅ Professional information (specialization, experience, expertise)
- ✅ System status (active/inactive)
- ✅ Unique identifier (panelist ID)

The feature is fully integrated with the authentication system, uses JWT tokens for security, and provides a clean, professional interface for viewing profile information.

---

**Made with ❤️ by Bob**