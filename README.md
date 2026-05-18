# Login Microservice Application

A full-stack login application with microservices architecture using Spring Boot backend and React frontend.

## 🏗️ Architecture

- **Backend**: Spring Boot microservice with JWT authentication
- **Frontend**: React application with modern UI
- **Database**: In-memory H2 database (can be configured for MySQL/PostgreSQL)

## 📁 Project Structure

```
LoginMicroserviceApp/
├── backend/                 # Spring Boot microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/login/
│   │   │   │       ├── controller/
│   │   │   │       ├── model/
│   │   │   │       ├── repository/
│   │   │   │       ├── service/
│   │   │   │       ├── security/
│   │   │   │       └── LoginApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── frontend/               # React application
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── services/
    │   ├── App.js
    │   └── index.js
    └── package.json

```

## 🚀 Features

### Backend
- ✅ User registration
- ✅ User login with JWT token
- ✅ Password encryption (BCrypt)
- ✅ Token-based authentication
- ✅ RESTful API endpoints
- ✅ CORS configuration
- ✅ Input validation
- ✅ **Multi-Factor Authentication (MFA/2FA)**
- ✅ TOTP-based authentication
- ✅ QR code generation for authenticator apps
- ✅ Password history tracking
- ✅ Password validation rules
- ✅ Real-time chat system
- ✅ AI-powered login chatbot

### Frontend
- ✅ Modern login UI
- ✅ Form validation
- ✅ API integration
- ✅ Token management
- ✅ Responsive design
- ✅ Error handling
- ✅ Real-time chat interface
- ✅ AI chatbot assistant

## 🛠️ Technologies

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Security
- JWT (JSON Web Tokens)
- H2 Database
- Maven

### Frontend
- React 18+
- Axios
- CSS3
- Modern JavaScript (ES6+)

## 📋 Prerequisites

- Java JDK 17 or higher
- Node.js 16+ and npm
- Maven 3.6+
- Git

## 🔧 Installation & Setup

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

Backend will run on: `http://localhost:8081`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

Frontend will run on: `http://localhost:3000`

## 🔌 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user (returns MFA_REQUIRED if MFA enabled) |
| POST | `/api/auth/login/mfa` | Login with MFA code |
| GET | `/api/auth/user` | Get user details (requires token) |
| POST | `/api/auth/change-password` | Change user password |
| GET | `/api/auth/health` | Health check endpoint |

### MFA Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/mfa/setup` | Generate QR code for MFA setup |
| POST | `/api/auth/mfa/enable` | Enable MFA with verification code |
| POST | `/api/auth/mfa/disable` | Disable MFA |
| GET | `/api/auth/mfa/status` | Check MFA status |

### Chat Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat/send` | Send a chat message |
| GET | `/api/chat/messages` | Get all messages for current user |
| GET | `/api/chat/messages/{username}` | Get messages by username |
| DELETE | `/api/chat/messages/{id}` | Delete a message |

### Request/Response Examples

**Register User:**
```json
POST /api/auth/register
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Login User:**
```json
POST /api/auth/login
{
  "username": "john_doe",
  "password": "SecurePass123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com"
}
```

## 🔐 Security

- Passwords are encrypted using BCrypt
- JWT tokens for stateless authentication
- Token expiration: 24 hours
- CORS enabled for frontend communication
- Input validation on all endpoints
- **Multi-Factor Authentication (MFA/2FA)** with TOTP
- Password history tracking (prevents reuse of last 5 passwords)
- Strong password validation rules
- Secure MFA secret storage

## 🎨 UI Features

- Clean and modern design
- Blue color scheme
- Responsive layout
- Form validation with error messages
- Loading states
- Success/error notifications

## 📝 Default Test Users

After running the application, you can use these test credentials:

- Username: `admin`
- Password: `admin123`

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📦 Building for Production

### Backend
```bash
cd backend
mvn clean package
java -jar target/login-service-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Created by Bob

## 📱 Multi-Factor Authentication (MFA)

### Current Status
✅ **Backend MFA is FULLY implemented** (TOTP with Google Authenticator)
✅ **Frontend MFA UI is FULLY implemented** (Complete user interface)

### How to Use MFA (User Interface)

#### Enable MFA:
1. **Login** to your account
2. Go to **Dashboard** → **Security** tab
3. Click **"🔒 Enable 2FA"** button
4. **Scan QR code** with Google Authenticator app
5. **Enter 6-digit code** from your app
6. Click **"✅ Verify & Enable"**
7. MFA is now enabled! 🎉

#### Login with MFA:
1. Enter **username** and **password**
2. Click **"Login"**
3. System detects MFA is enabled
4. Enter **6-digit code** from authenticator app
5. Click **"✅ Verify & Login"**
6. Successfully logged in! 🎉

#### Disable MFA:
1. Go to **Dashboard** → **Security** tab
2. Click **"🔓 Disable 2FA"**
3. Enter **current 6-digit code**
4. Click **"🔓 Confirm Disable"**
5. MFA is now disabled

### Compatible Authenticator Apps
- ✅ Google Authenticator (Android/iOS)
- ✅ Microsoft Authenticator (Android/iOS)
- ✅ Authy (Android/iOS/Desktop)
- ✅ 1Password (with TOTP support)
- ✅ Any RFC 6238 compliant TOTP app

### MFA Features
- TOTP-based authentication (Time-based One-Time Password)
- QR code generation for easy setup
- 6-digit codes that refresh every 30 seconds
- Secure secret storage in database
- Optional (users can choose to enable/disable)

**Note**: Complete MFA implementation with beautiful UI! See `FRONTEND_MFA_GUIDE.md` for detailed user guide and `MFA_IMPLEMENTATION_GUIDE.md` for technical documentation.

## 💬 Chat Feature

The application includes a real-time chat system:
- Send and receive messages
- View chat history
- Delete messages
- User-specific message filtering
- AI-powered login chatbot assistant

See `CHAT_FEATURE.md` and `AI_CHATBOT_GUIDE.md` for details.

## 📚 Additional Documentation

- `QUICK_START.md` - Quick start guide
- `SETUP_GUIDE.md` - Detailed setup instructions
- `MFA_IMPLEMENTATION_GUIDE.md` - Complete MFA backend documentation
- `FRONTEND_MFA_GUIDE.md` - Frontend MFA implementation guide
- `PASSWORD_VALIDATION_GUIDE.md` - Password rules and validation
- `H2_DATABASE_GUIDE.md` - Database access and management
- `CHAT_FEATURE.md` - Chat system documentation
- `AI_CHATBOT_GUIDE.md` - AI chatbot documentation
- `NETWORK_ACCESS_GUIDE.md` - Network configuration
- `NGROK_TWO_TUNNELS_GUIDE.md` - Ngrok setup for external access

## 📞 Support

For issues and questions, please create an issue in the repository.

---

**Happy Coding! 🚀**