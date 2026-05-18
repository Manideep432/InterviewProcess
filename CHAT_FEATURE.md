# Chat Feature Documentation

## Overview
A real-time chat system integrated into the Login Microservice Application. Users can send messages, view chat history, and delete their own messages.

## Features
- ✅ Real-time messaging with auto-refresh (every 3 seconds)
- ✅ User authentication required
- ✅ Message history (last 50 messages)
- ✅ Delete own messages
- ✅ Beautiful UI with gradient design
- ✅ Responsive layout
- ✅ Timestamp display
- ✅ User identification (own vs other messages)

## Backend Components

### 1. ChatMessage Entity (`backend/src/main/java/com/login/model/ChatMessage.java`)
- Stores chat messages in H2 database
- Fields: id, username, message, timestamp, messageType

### 2. ChatMessageRepository (`backend/src/main/java/com/login/repository/ChatMessageRepository.java`)
- JPA repository for database operations
- Custom queries for fetching messages

### 3. ChatService (`backend/src/main/java/com/login/service/ChatService.java`)
- Business logic for chat operations
- Methods: saveMessage, getAllMessages, getMessagesByUsername, deleteMessage

### 4. ChatController (`backend/src/main/java/com/login/controller/ChatController.java`)
- REST API endpoints
- JWT authentication required for all endpoints

## API Endpoints

### Send Message
```
POST /api/chat/send
Headers: Authorization: Bearer <token>
Body: { "message": "Hello World" }
Response: { "success": true, "message": {...} }
```

### Get All Messages
```
GET /api/chat/messages
Headers: Authorization: Bearer <token>
Response: { "success": true, "messages": [...] }
```

### Get Messages by Username
```
GET /api/chat/messages/{username}
Headers: Authorization: Bearer <token>
Response: { "success": true, "messages": [...] }
```

### Delete Message
```
DELETE /api/chat/messages/{id}
Headers: Authorization: Bearer <token>
Response: { "success": true, "message": "Message deleted" }
```

## Frontend Components

### 1. Chat Component (`frontend/src/components/Chat.js`)
- Main chat interface
- Features:
  - Message list with auto-scroll
  - Input form for sending messages
  - Delete button for own messages
  - Auto-refresh every 3 seconds
  - Loading states and error handling

### 2. Chat Styles (`frontend/src/components/Chat.css`)
- Modern gradient design
- Responsive layout
- Smooth animations
- Custom scrollbar
- Message bubbles (different styles for own/other messages)

### 3. Chat Service (`frontend/src/services/chatService.js`)
- API integration
- Methods: sendMessage, getAllMessages, getMessagesByUsername, deleteMessage
- Automatic JWT token handling

## Testing Instructions

### 1. Start Backend
```bash
cd backend
mvn spring-boot:run
```
Backend will run on: http://localhost:8080

### 2. Start Frontend
```bash
cd frontend
npm start
```
Frontend will run on: http://localhost:3000

### 3. Test Chat Feature

#### Step 1: Register/Login
1. Open http://localhost:3000
2. Register a new user or login with existing credentials
3. You'll be redirected to the dashboard

#### Step 2: Send Messages
1. Scroll down to see the chat box
2. Type a message in the input field
3. Click "Send" button or press Enter
4. Your message will appear in the chat

#### Step 3: Test with Multiple Users
1. Open a new incognito/private window
2. Register/login with a different user
3. Send messages from both users
4. Messages will auto-refresh every 3 seconds

#### Step 4: Delete Messages
1. Hover over your own messages
2. Click the 🗑️ (trash) icon
3. Confirm deletion
4. Message will be removed

### 4. Verify Database
Access H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:logindb
- Username: sa
- Password: (leave empty)

Run query:
```sql
SELECT * FROM chat_messages ORDER BY timestamp DESC;
```

## Security Features
- ✅ JWT authentication required for all chat operations
- ✅ Users can only delete their own messages
- ✅ Token validation on every request
- ✅ CORS configured for frontend origin

## Database Schema
```sql
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    message_type VARCHAR(50) NOT NULL
);
```

## Troubleshooting

### Messages not appearing?
- Check if backend is running on port 8080
- Verify JWT token is valid (check browser console)
- Check browser network tab for API errors

### Can't send messages?
- Ensure you're logged in
- Check message length (max 1000 characters)
- Verify backend logs for errors

### Auto-refresh not working?
- Check browser console for errors
- Verify network connectivity
- Ensure backend is responding to GET requests

## Future Enhancements
- 🔄 WebSocket for real-time updates (instead of polling)
- 📎 File attachments
- 🔍 Search messages
- 📌 Pin important messages
- 👥 Private messaging
- 🔔 Notifications
- 😊 Emoji support
- ✏️ Edit messages
- 📊 Message analytics

## Tech Stack
- **Backend**: Spring Boot 3.2.0, Spring Data JPA, H2 Database
- **Frontend**: React 18.2.0, Axios 1.6.0
- **Authentication**: JWT (JJWT 0.12.3)
- **Styling**: Custom CSS with gradients and animations

## Made with ❤️ by Bob