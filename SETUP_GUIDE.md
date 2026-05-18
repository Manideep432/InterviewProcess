# 🚀 Setup Guide - Login Microservice Application

## Quick Start Guide

Follow these steps to run the application on your machine.

---

## 📋 Prerequisites

Make sure you have the following installed:

- ✅ **Java JDK 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- ✅ **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- ✅ **Node.js 16+ and npm** - [Download](https://nodejs.org/)
- ✅ **Git** (optional) - [Download](https://git-scm.com/)

### Verify Installation

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Node.js version
node -v

# Check npm version
npm -v
```

---

## 🔧 Backend Setup (Spring Boot)

### Step 1: Navigate to Backend Directory

```bash
cd C:\Users\SiripalliManideep\Desktop\LoginMicroserviceApp\backend
```

### Step 2: Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests
- Create a JAR file

### Step 3: Run the Backend Server

```bash
mvn spring-boot:run
```

**OR** run the JAR file directly:

```bash
java -jar target/login-service-0.0.1-SNAPSHOT.jar
```

### ✅ Backend is Ready!

You should see:
```
🚀 Login Microservice Started Successfully!
📍 Server running on: http://localhost:8080
📊 H2 Console: http://localhost:8080/h2-console
```

**Keep this terminal open!**

---

## 🎨 Frontend Setup (React)

### Step 1: Open a NEW Terminal

Open a new PowerShell or Command Prompt window.

### Step 2: Navigate to Frontend Directory

```bash
cd C:\Users\SiripalliManideep\Desktop\LoginMicroserviceApp\frontend
```

### Step 3: Install Dependencies

```bash
npm install
```

This will install:
- React
- Axios
- All other dependencies

### Step 4: Start the React Development Server

```bash
npm start
```

### ✅ Frontend is Ready!

Your browser should automatically open to:
```
http://localhost:3000
```

If not, manually open your browser and go to `http://localhost:3000`

---

## 🎯 Testing the Application

### 1. Register a New User

1. Click on "Register here" link
2. Fill in the form:
   - Username: `testuser`
   - Email: `test@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
3. Click "Register"
4. You should be redirected to the Dashboard

### 2. Logout and Login

1. Click "Logout" button
2. Enter your credentials:
   - Username: `testuser`
   - Password: `password123`
3. Click "Login"
4. You should see the Dashboard again

### 3. Test API Endpoints (Optional)

You can test the API using tools like Postman or curl:

**Health Check:**
```bash
curl http://localhost:8080/api/auth/health
```

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"pass123\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"password\":\"pass123\"}"
```

---

## 🗄️ Database Access (H2 Console)

To view the database:

1. Go to: `http://localhost:8080/h2-console`
2. Use these settings:
   - **JDBC URL:** `jdbc:h2:mem:logindb`
   - **Username:** `sa`
   - **Password:** (leave empty)
3. Click "Connect"
4. You can now see the `USERS` table and all data

---

## 🛑 Stopping the Application

### Stop Backend:
- Press `Ctrl + C` in the backend terminal

### Stop Frontend:
- Press `Ctrl + C` in the frontend terminal

---

## 🐛 Troubleshooting

### Port Already in Use

**Backend (Port 8080):**
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Frontend (Port 3000):**
```bash
# Windows - Find and kill process
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### Maven Build Fails

```bash
# Clean and rebuild
mvn clean
mvn install -U
```

### npm Install Fails

```bash
# Clear cache and reinstall
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### CORS Errors

Make sure:
1. Backend is running on `http://localhost:8080`
2. Frontend is running on `http://localhost:3000`
3. Both servers are running simultaneously

---

## 📁 Project Structure

```
LoginMicroserviceApp/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/login/
│   │       ├── controller/     # REST Controllers
│   │       ├── model/          # Entity Classes
│   │       ├── repository/     # Data Access Layer
│   │       ├── service/        # Business Logic
│   │       ├── security/       # Security & JWT
│   │       └── dto/            # Data Transfer Objects
│   └── pom.xml                 # Maven Dependencies
│
└── frontend/                   # React Frontend
    ├── src/
    │   ├── components/         # React Components
    │   ├── services/           # API Services
    │   ├── App.js              # Main App Component
    │   └── index.js            # Entry Point
    └── package.json            # npm Dependencies
```

---

## 🎨 Features

✅ User Registration with validation  
✅ User Login with JWT authentication  
✅ Password encryption (BCrypt)  
✅ Token-based authentication  
✅ Modern blue-themed UI  
✅ Form validation  
✅ Error handling  
✅ Responsive design  
✅ Dashboard after login  

---

## 📞 Need Help?

If you encounter any issues:

1. Check that both backend and frontend are running
2. Verify all prerequisites are installed
3. Check the console for error messages
4. Make sure ports 8080 and 3000 are available

---

**Happy Coding! 🚀**

Created by Bob