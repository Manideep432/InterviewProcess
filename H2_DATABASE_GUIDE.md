# H2 Database Connection Guide

## What is H2 Database?

H2 is an in-memory database that's already configured in your Spring Boot application. It's automatically created when you start the backend and deleted when you stop it.

## Step-by-Step: How to Connect to H2 Database

### Step 1: Start the Backend Application

**Option A: Using IntelliJ IDEA**
1. Open IntelliJ IDEA
2. Navigate to: `backend/src/main/java/com/login/LoginApplication.java`
3. Right-click on the file
4. Select **"Run 'LoginApplication'"**
5. Wait for the application to start
6. Look for this in the console:
   ```
   =================================================
   Creating test users...
   =================================================
   ✅ Created user: Manideep (password: password123)
   ✅ Created user: testuser (password: test123)
   ✅ Created user: admin (password: admin123)
   =================================================
   ```

**Option B: Using Eclipse**
1. Open Eclipse
2. Right-click on `LoginApplication.java`
3. Select **"Run As" → "Java Application"**
4. Check console for user creation messages

**Option C: Using VS Code**
1. Install "Spring Boot Extension Pack" from Extensions
2. Open `LoginApplication.java`
3. Click the **"Run"** button above the `main` method
4. Check terminal for startup messages

**Option D: Using Command Line (if Maven is installed)**
```bash
cd backend
mvn spring-boot:run
```

### Step 2: Verify Backend is Running

Open your browser and go to:
```
http://localhost:8080
```

You should see a **Whitelabel Error Page** (this is normal - it means backend is running!)

### Step 3: Access H2 Console

1. Open your browser
2. Go to: **http://localhost:8080/h2-console**
3. You should see the H2 Console login page

### Step 4: Login to H2 Console

Fill in these details:

| Field | Value |
|-------|-------|
| **JDBC URL** | `jdbc:h2:mem:logindb` |
| **User Name** | `sa` |
| **Password** | (leave empty) |

Click **"Connect"** button

### Step 5: Verify Users Were Created

Once logged in, you'll see a SQL query box. Run this query:

```sql
SELECT * FROM users;
```

Click **"Run"** button

You should see 3 users:

| ID | USERNAME | EMAIL | PASSWORD |
|----|----------|-------|----------|
| 1 | Manideep | manideep@example.com | $2a$10$... |
| 2 | testuser | test@example.com | $2a$10$... |
| 3 | admin | admin@example.com | $2a$10$... |

### Step 6: Verify Chat Messages Table

Run this query:

```sql
SELECT * FROM chat_messages;
```

This table will be empty initially (no messages yet).

## Troubleshooting

### Problem 1: Can't Access http://localhost:8080/h2-console

**Solution:**
- Backend is not running
- Start the backend application (see Step 1)
- Wait 30 seconds for it to fully start
- Try again

### Problem 2: "Database not found" Error

**Solution:**
- Check JDBC URL is exactly: `jdbc:h2:mem:logindb`
- Make sure backend is running
- H2 is in-memory, so it's created when backend starts

### Problem 3: No Users in Database

**Solution:**
- Check backend console for user creation messages
- If you don't see "✅ Created user" messages:
  - Stop the backend
  - Delete `backend/target` folder
  - Restart the backend
  - Users will be created automatically

### Problem 4: Can't Start Backend

**Solution:**
- Check if port 8080 is already in use
- Close any other applications using port 8080
- Or change port in `application.properties`:
  ```properties
  server.port=8081
  ```

## H2 Console Features

### View All Tables
In the left panel, you'll see:
- **USERS** - User accounts
- **CHAT_MESSAGES** - Chat history

### Run Queries
You can run any SQL query:

**Count users:**
```sql
SELECT COUNT(*) FROM users;
```

**Find specific user:**
```sql
SELECT * FROM users WHERE username = 'Manideep';
```

**View recent chat messages:**
```sql
SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 10;
```

**Delete all chat messages:**
```sql
DELETE FROM chat_messages;
```

## Configuration Files

### application.properties
Location: `backend/src/main/resources/application.properties`

Current H2 configuration:
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:logindb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

## Testing the Complete Flow

### 1. Start Backend
- Run `LoginApplication.java`
- Check console for user creation messages

### 2. Verify Database
- Go to http://localhost:8080/h2-console
- Login with credentials above
- Run: `SELECT * FROM users;`
- Confirm 3 users exist

### 3. Start Frontend
- Run: `npm start` in frontend directory
- Opens: http://localhost:3000

### 4. Test Chatbot
- Click chat icon (💬)
- Say: "I want to login"
- Say: "Manideep"
- Bot checks database and finds user ✅
- Say: "password123"
- Bot verifies password and logs in ✅

### 5. Verify Chat Messages
- After using chatbot
- Go back to H2 Console
- Run: `SELECT * FROM chat_messages;`
- You should see your chat history

## Important Notes

⚠️ **H2 is In-Memory**
- Data is lost when backend stops
- Users are recreated on each startup
- Perfect for development/testing
- Not for production use

✅ **Automatic Setup**
- No manual database setup needed
- Tables created automatically
- Test users created automatically
- Just start the backend!

## Quick Checklist

- [ ] Backend is running (check http://localhost:8080)
- [ ] H2 Console accessible (http://localhost:8080/h2-console)
- [ ] Can login to H2 Console
- [ ] Users table has 3 users
- [ ] Frontend is running (http://localhost:3000)
- [ ] Chatbot icon visible on login page
- [ ] Chatbot can verify username from database

If all checkboxes are ✅, your chatbot is fully connected to the backend database!

## Made with ❤️ by Bob