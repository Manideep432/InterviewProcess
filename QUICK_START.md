# Quick Start Guide - Testing the AI ChatBot

## Step 1: Register a Test User

### Option A: Using the Registration Form
1. Open http://localhost:3000
2. Click "Register here" link
3. Fill in the form:
   - Username: `Manideep`
   - Email: `manideep@example.com`
   - Password: `password123`
4. Click "Register"
5. You'll be logged in automatically

### Option B: Using H2 Console (Direct Database)
1. Open http://localhost:8080/h2-console
2. Login with:
   - JDBC URL: `jdbc:h2:mem:logindb`
   - Username: `sa`
   - Password: (leave empty)
3. Run this SQL:
```sql
INSERT INTO users (username, email, password) 
VALUES ('Manideep', 'manideep@example.com', '$2a$10$encrypted_password_here');
```

## Step 2: Test the AI ChatBot

1. **Logout** if you're logged in (click Logout button)
2. You'll be back on the login page
3. **Click the chat icon** (💬) in the bottom-right corner
4. **Start conversation**:
   ```
   You: "I want to login"
   Bot: "Great! I'll help you login. What's your username?"
   You: "Manideep"
   Bot: "✅ User 'Manideep' found! Please provide your password."
   Bot: "🎤 You can click the microphone icon to speak your password"
   ```
5. **Enter password**:
   - Type: `password123`
   - OR Click 🎙️ and speak: "password one two three"
6. **Click Send** or press Enter
7. Bot will say: "✅ Login successful! Redirecting to dashboard..."
8. You'll be automatically redirected to the dashboard

## Step 3: Test Voice Recognition

1. Follow steps 1-4 above
2. When bot asks for password, **click the microphone icon** (🎙️)
3. Icon turns red (🎤) and pulses - **now listening**
4. **Speak clearly**: "password one two three"
5. Password appears in the input field
6. **Click Send**
7. Login successful!

## Common Test Scenarios

### Scenario 1: User Not Found
```
You: "help me login"
Bot: "What's your username?"
You: "NonExistentUser"
Bot: "❌ User 'NonExistentUser' not found. Please check the username or register."
Bot: "Would you like to try a different username? (yes/no)"
You: "yes"
Bot: "Okay, what's your username?"
```

### Scenario 2: Wrong Password
```
You: "I want to login"
Bot: "What's your username?"
You: "Manideep"
Bot: "✅ User found! Please provide your password."
You: "wrongpassword"
Bot: "❌ Login failed. The password is incorrect."
Bot: "Would you like to try again? (yes/no)"
You: "yes"
Bot: "Okay, please provide your password again."
```

### Scenario 3: Successful Login
```
You: "login please"
Bot: "What's your username?"
You: "Manideep"
Bot: "✅ User found! Please provide your password."
You: "password123"
Bot: "🔐 Attempting to login..."
Bot: "✅ Login successful! Redirecting to dashboard..."
[Automatically redirects to dashboard]
```

## Troubleshooting

### "User not found" error?
- Make sure you registered the user first
- Check spelling of username (case-sensitive)
- Verify user exists in database (H2 console)

### Voice recognition not working?
- Use Chrome, Edge, or Safari (not Firefox)
- Allow microphone permissions
- Speak clearly and not too fast
- Check browser console for errors

### Chatbot not responding?
- Ensure backend is running on port 8080
- Check browser console for errors
- Verify network tab shows API calls
- Try refreshing the page

## Quick Test Commands

Try these natural language commands:
- "I want to login"
- "help me login"
- "I need to sign in"
- "Can you help me access my account?"
- "login please"

## Expected Results

✅ **Working Correctly:**
- Chat icon appears in bottom-right
- Clicking icon opens chat window
- Bot responds to messages
- User verification works
- Password input (text or voice) works
- Successful login redirects to dashboard
- Failed login shows error and retry option

❌ **Not Working:**
- No chat icon visible
- Chat window doesn't open
- Bot doesn't respond
- Voice recognition fails
- Login doesn't work

## Next Steps

After successful login:
1. You'll see the Dashboard with your user info
2. Scroll down to see the **Chat Room** feature
3. Send messages to test the chat functionality
4. Open another browser (incognito) to test multi-user chat

## Made with ❤️ by Bob