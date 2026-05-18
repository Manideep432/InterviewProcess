# Network Access Guide - Share Your Application

## Current Situation:
- **localhost:3000** - Only works on your computer ❌
- **localhost:8080** - Only works on your computer ❌

## Solution: Make It Accessible on Your Network

### Option 1: Local Network Access (Same WiFi)

This allows anyone on the same WiFi network to access your application.

#### Step 1: Find Your IP Address

**On Windows:**
1. Open Command Prompt (cmd)
2. Type: `ipconfig`
3. Look for "IPv4 Address" under your active network
4. Example: `192.168.1.100`

**On Mac/Linux:**
1. Open Terminal
2. Type: `ifconfig` or `ip addr`
3. Look for your IP address
4. Example: `192.168.1.100`

#### Step 2: Update Backend Configuration

**File**: `backend/src/main/resources/application.properties`

Add this line:
```properties
server.address=0.0.0.0
```

This allows the backend to accept connections from any IP on your network.

#### Step 3: Update Frontend API URL

**File**: `frontend/src/services/authService.js`

Change:
```javascript
const API_URL = 'http://localhost:8080/api/auth';
```

To (replace with YOUR IP):
```javascript
const API_URL = 'http://192.168.1.100:8080/api/auth';
```

**File**: `frontend/src/services/chatService.js`

Change:
```javascript
const API_URL = 'http://localhost:8080/api/chat';
```

To (replace with YOUR IP):
```javascript
const API_URL = 'http://192.168.1.100:8080/api/chat';
```

#### Step 4: Update CORS Configuration

**File**: `backend/src/main/java/com/login/security/SecurityConfig.java`

Find the CORS configuration and update it to allow your IP:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",
        "http://192.168.1.100:3000"  // Add your IP
    ));
    // ... rest of the configuration
}
```

#### Step 5: Update Frontend Package.json Proxy

**File**: `frontend/package.json`

Change:
```json
"proxy": "http://localhost:8080"
```

To (replace with YOUR IP):
```json
"proxy": "http://192.168.1.100:8080"
```

#### Step 6: Start Both Applications

1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start Frontend** (in new terminal):
   ```bash
   cd frontend
   npm start
   ```

#### Step 7: Share the URL

Give this URL to others on your network:
```
http://192.168.1.100:3000
```

Replace `192.168.1.100` with YOUR actual IP address.

### Option 2: Using ngrok (Internet Access)

This creates a public URL that works from anywhere on the internet.

#### Step 1: Install ngrok

1. Go to: https://ngrok.com/
2. Sign up for free account
3. Download ngrok
4. Install it

#### Step 2: Start Backend

```bash
cd backend
mvn spring-boot:run
```

#### Step 3: Create Tunnel for Backend

Open new terminal:
```bash
ngrok http 8080
```

You'll see:
```
Forwarding: https://abc123.ngrok.io -> http://localhost:8080
```

Copy the `https://abc123.ngrok.io` URL.

#### Step 4: Update Frontend API URLs

**File**: `frontend/src/services/authService.js`
```javascript
const API_URL = 'https://abc123.ngrok.io/api/auth';
```

**File**: `frontend/src/services/chatService.js`
```javascript
const API_URL = 'https://abc123.ngrok.io/api/chat';
```

#### Step 5: Update Backend CORS

**File**: `backend/src/main/java/com/login/security/SecurityConfig.java`

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "https://abc123.ngrok.io"  // Add ngrok URL
));
```

#### Step 6: Start Frontend

```bash
cd frontend
npm start
```

#### Step 7: Create Tunnel for Frontend

Open another terminal:
```bash
ngrok http 3000
```

You'll see:
```
Forwarding: https://xyz789.ngrok.io -> http://localhost:3000
```

#### Step 8: Share the Frontend URL

Give this URL to anyone:
```
https://xyz789.ngrok.io
```

They can access it from anywhere in the world!

### Option 3: Deploy to Cloud (Production)

For permanent hosting:

**Frontend Options:**
- Vercel (free): https://vercel.com
- Netlify (free): https://netlify.com
- GitHub Pages (free)

**Backend Options:**
- Heroku (free tier): https://heroku.com
- Railway (free tier): https://railway.app
- Render (free tier): https://render.com

## Quick Comparison:

| Method | Accessibility | Setup | Cost | Best For |
|--------|--------------|-------|------|----------|
| **Local Network** | Same WiFi only | Easy | Free | Testing with friends nearby |
| **ngrok** | Internet | Medium | Free | Quick demos, temporary sharing |
| **Cloud Deploy** | Internet | Hard | Free/Paid | Production, permanent hosting |

## Troubleshooting:

### Firewall Issues:
If others can't connect:
1. Check Windows Firewall
2. Allow ports 3000 and 8080
3. Or temporarily disable firewall for testing

### Network Issues:
- Make sure you're on the same WiFi network
- Check if your router allows device-to-device communication
- Some public WiFi networks block this

### CORS Errors:
- Make sure you updated all CORS configurations
- Restart both backend and frontend after changes

## Security Note:

⚠️ **Important**: When sharing your application:
- Don't share on public networks without proper security
- Use HTTPS in production
- Add authentication
- Don't expose sensitive data
- ngrok free tier URLs are temporary (change every restart)

## Made with ❤️ by Bob