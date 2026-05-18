# Running Two ngrok Tunnels Simultaneously

## Step-by-Step Guide

### Step 1: Stop Any Existing ngrok Tunnels

**On Windows:**
```bash
taskkill /F /IM ngrok.exe
```

**On Mac/Linux:**
```bash
killall ngrok
```

### Step 2: Open Two Separate Terminal Windows

You need **TWO different terminal windows** (PowerShell, CMD, or Git Bash)

---

## Terminal 1: Backend Tunnel (Port 8080)

### Run this command:
```bash
ngrok http 8080
```

### You'll see output like:
```
Forwarding    https://abc123-backend.ngrok-free.app -> http://localhost:8080
```

### Copy the HTTPS URL (e.g., `https://abc123-backend.ngrok-free.app`)

**⚠️ KEEP THIS TERMINAL OPEN!**

---

## Terminal 2: Frontend Tunnel (Port 3000)

### Open a NEW terminal window and run:
```bash
ngrok http 3000
```

### You'll see output like:
```
Forwarding    https://xyz789-frontend.ngrok-free.app -> http://localhost:3000
```

### Copy the HTTPS URL (e.g., `https://xyz789-frontend.ngrok-free.app`)

**⚠️ KEEP THIS TERMINAL OPEN TOO!**

---

## Step 3: Update Frontend Configuration

### Update `frontend/src/services/authService.js`:

Replace the API_URL with your **backend ngrok URL**:

```javascript
const API_URL = 'https://abc123-backend.ngrok-free.app/api/auth';
```

### Update `frontend/src/services/chatService.js`:

Replace the API_URL with your **backend ngrok URL**:

```javascript
const API_URL = 'https://abc123-backend.ngrok-free.app/api/chat';
```

---

## Step 4: Update Backend CORS Configuration

### Update `backend/src/main/java/com/login/security/SecurityConfig.java`:

Add your **frontend ngrok URL** to allowed origins:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",
        "https://xyz789-frontend.ngrok-free.app"  // Add your frontend ngrok URL
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## Step 5: Restart Your Applications

### 1. Stop Backend (if running):
- Press `Ctrl+C` in backend terminal

### 2. Rebuild and Start Backend:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. Stop Frontend (if running):
- Press `Ctrl+C` in frontend terminal

### 4. Restart Frontend:
```bash
cd frontend
npm start
```

---

## Step 6: Share URLs

### Share the **Frontend URL** with others:
```
https://xyz789-frontend.ngrok-free.app
```

They can access your app from anywhere in the world!

---

## Important Notes

### ✅ Both Terminals Must Stay Open
- Terminal 1: ngrok for backend (port 8080)
- Terminal 2: ngrok for frontend (port 3000)
- If you close either terminal, that tunnel stops working

### ✅ URLs Change Every Time
- Each time you restart ngrok, you get NEW URLs
- You must update the configuration files with new URLs
- For permanent URLs, upgrade to ngrok paid plan

### ✅ Free ngrok Limitations
- URLs expire when you close the terminal
- Limited to 40 connections/minute
- Shows ngrok warning page on first visit (users must click "Visit Site")

---

## Alternative: Use ngrok Config File (Advanced)

### Create `ngrok.yml` file:

```yaml
version: "2"
authtoken: YOUR_NGROK_AUTH_TOKEN
tunnels:
  backend:
    proto: http
    addr: 8080
  frontend:
    proto: http
    addr: 3000
```

### Run both tunnels with one command:
```bash
ngrok start --all
```

This starts both tunnels in one terminal window!

---

## Quick Reference

| Service | Port | ngrok Command | Example URL |
|---------|------|---------------|-------------|
| Backend | 8080 | `ngrok http 8080` | https://abc123.ngrok-free.app |
| Frontend | 3000 | `ngrok http 3000` | https://xyz789.ngrok-free.app |

---

## Troubleshooting

### Error: "endpoint already online"
**Solution:** Kill all ngrok processes first:
```bash
taskkill /F /IM ngrok.exe
```

### Error: "tunnel not found"
**Solution:** Make sure both terminals are still open

### Error: "CORS error"
**Solution:** Update SecurityConfig.java with correct frontend ngrok URL

### Frontend can't connect to backend
**Solution:** Check that authService.js and chatService.js have correct backend ngrok URL

---

## Summary

1. ✅ Open 2 terminals
2. ✅ Run `ngrok http 8080` in Terminal 1
3. ✅ Run `ngrok http 3000` in Terminal 2
4. ✅ Update frontend config with backend URL
5. ✅ Update backend CORS with frontend URL
6. ✅ Restart both applications
7. ✅ Share frontend URL with others

**Both tunnels must stay running!**