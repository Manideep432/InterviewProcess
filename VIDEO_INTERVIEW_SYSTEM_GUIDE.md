# 📹 Video Interview System - Complete Guide

## Overview
This guide covers the complete video interview scheduling and meeting system with calendar, WebRTC video calls, screen sharing, annotations, and real-time chat.

## 🎯 Features

### 1. **Interview Scheduling Calendar**
- Interactive calendar with date selection
- Time slot management (9 AM - 6 PM)
- Candidate and panelist selection
- Automatic conflict detection
- Duration selection (30-120 minutes)
- Notes and position details

### 2. **Video Meeting Features**
- **HD Video Calling** - WebRTC-based peer-to-peer video
- **Audio Controls** - Mute/Unmute microphone
- **Video Controls** - Turn camera on/off
- **Screen Sharing** - Share your screen with participants
- **Annotations** - Draw on shared screen with pen/eraser
- **Real-time Chat** - Text messaging during meetings
- **Recording** - Record interview sessions
- **Picture-in-Picture** - Local video preview

### 3. **Participant Roles**
- **HR** - Schedule interviews, manage meetings, end sessions
- **Panelist** - Conduct interviews, control meeting features
- **Candidate** - Join scheduled interviews

## 📦 Installation

### Backend Dependencies (Already Added)
The following dependencies are already in `backend/pom.xml`:
- Spring Boot WebSocket
- Spring Messaging
- SockJS Client
- STOMP WebSocket

### Frontend Dependencies
Install required npm packages:

```bash
cd frontend
npm install sockjs-client @stomp/stompjs
```

## 🚀 Setup Instructions

### Step 1: Database Migration
The system will automatically create the required tables:
- `interviews` - Interview scheduling data
- `video_meetings` - Video meeting sessions
- `meeting_participants` - Active participants tracking

### Step 2: Backend Configuration
No additional configuration needed. WebSocket is configured at:
- Endpoint: `ws://localhost:8081/ws`
- STOMP prefix: `/app`
- Subscribe prefix: `/topic`

### Step 3: Start Services

**Backend:**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

## 📅 Using the Calendar System

### For HR Users

1. **Access Calendar**
   - Navigate to HR Dashboard
   - Click on "Schedule Interview" tab

2. **Schedule an Interview**
   - Select a date from the calendar
   - Choose a time slot (green = available, red = booked)
   - Select candidate from dropdown
   - Select panelist from dropdown
   - Enter position title
   - Set duration (default: 60 minutes)
   - Add optional notes
   - Click "Schedule Interview"

3. **View Scheduled Interviews**
   - All scheduled interviews appear in the dashboard
   - Filter by date, status, or panelist
   - Click on interview to view details or join meeting

### Calendar Features
- **Date Navigation** - Use arrows to change months
- **Disabled Dates** - Past dates are grayed out
- **Time Slot Status** - Visual indication of availability
- **Conflict Prevention** - Can't double-book panelists

## 🎥 Using the Video Meeting System

### Joining a Meeting

1. **From Dashboard**
   - Click "Join Meeting" on scheduled interview
   - System automatically connects to video room

2. **Direct Link**
   - Use meeting room ID: `ROOM-XXXXXXXX`
   - Enter room ID in join dialog

### Video Controls

#### Basic Controls
- **🎤 Microphone** - Click to mute/unmute audio
- **📷 Camera** - Click to turn video on/off
- **🖥️ Screen Share** - Click to share your screen
- **✏️ Annotations** - Enable drawing tools
- **💬 Chat** - Open text chat panel
- **⏺️ Recording** - Start/stop recording (HR/Panelist only)
- **📞 Leave** - Exit meeting (stay active for others)
- **❌ End** - End meeting for all (HR/Panelist only)

### Screen Sharing

1. **Start Sharing**
   - Click screen share button (🖥️)
   - Select window/screen to share
   - Click "Share"

2. **Stop Sharing**
   - Click screen share button again
   - Or close the shared window

3. **Features**
   - Share entire screen or specific window
   - Cursor visibility
   - Automatic fallback to camera when stopped

### Annotation Tools

1. **Enable Annotations**
   - Click annotation button (✏️)
   - Annotation toolbar appears on left

2. **Drawing Tools**
   - **✏️ Pen** - Draw freehand
   - **🧹 Eraser** - Erase drawings
   - **Color Picker** - Choose pen color
   - **Width Slider** - Adjust pen thickness
   - **🗑️ Clear** - Clear all annotations

3. **Usage**
   - Click and drag to draw
   - Annotations visible to all participants
   - Works on both video and screen share

### Chat System

1. **Open Chat**
   - Click chat button (💬)
   - Chat panel appears on right

2. **Send Messages**
   - Type message in input box
   - Press Enter or click Send
   - Messages visible to all participants

3. **Features**
   - Real-time message delivery
   - Username display
   - Scrollable message history
   - Timestamp tracking

### Recording

1. **Start Recording** (HR/Panelist only)
   - Click recording button (⏺️)
   - Button turns red and pulses
   - All participants notified

2. **Stop Recording**
   - Click recording button again
   - Recording saved automatically

## 🔧 API Endpoints

### Meeting Management

#### Create Meeting
```http
POST /api/meetings/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "interviewId": 1,
  "hrId": 1,
  "panelistId": 2,
  "candidateId": 3,
  "scheduledStartTime": "2026-05-20T10:00:00",
  "durationMinutes": 60
}
```

#### Join Meeting
```http
POST /api/meetings/join
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomId": "ROOM-ABC12345",
  "userId": 1
}
```

#### Get Meeting by Room ID
```http
GET /api/meetings/room/{roomId}
Authorization: Bearer {token}
```

#### Get HR Meetings
```http
GET /api/meetings/hr/{hrId}
Authorization: Bearer {token}
```

#### Leave Meeting
```http
POST /api/meetings/leave
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomId": "ROOM-ABC12345",
  "userId": 1
}
```

#### End Meeting
```http
POST /api/meetings/end
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomId": "ROOM-ABC12345",
  "userId": 1
}
```

#### Toggle Recording
```http
POST /api/meetings/recording
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomId": "ROOM-ABC12345",
  "userId": 1,
  "enable": true
}
```

### WebSocket Topics

#### Subscribe to Meeting Events
```javascript
// Offers
stompClient.subscribe('/topic/meeting/{roomId}/offer', handleOffer);

// Answers
stompClient.subscribe('/topic/meeting/{roomId}/answer', handleAnswer);

// ICE Candidates
stompClient.subscribe('/topic/meeting/{roomId}/ice-candidate', handleIceCandidate);

// Participants
stompClient.subscribe('/topic/meeting/{roomId}/participants', handleParticipants);

// Screen Share
stompClient.subscribe('/topic/meeting/{roomId}/screen-share', handleScreenShare);

// Audio/Video
stompClient.subscribe('/topic/meeting/{roomId}/audio', handleAudio);
stompClient.subscribe('/topic/meeting/{roomId}/video', handleVideo);

// Annotations
stompClient.subscribe('/topic/meeting/{roomId}/annotation', handleAnnotation);

// Chat
stompClient.subscribe('/topic/meeting/{roomId}/chat', handleChat);

// Control
stompClient.subscribe('/topic/meeting/{roomId}/control', handleControl);
```

## 🎨 Component Integration

### Add Calendar to HR Dashboard

```javascript
import InterviewCalendar from './InterviewCalendar';

// In your HR Dashboard component
<InterviewCalendar 
  user={user} 
  onSchedule={(meeting) => {
    console.log('Interview scheduled:', meeting);
    // Refresh dashboard or show success message
  }}
/>
```

### Add Video Meeting Component

```javascript
import VideoMeeting from './VideoMeeting';

// When joining a meeting
const [inMeeting, setInMeeting] = useState(false);
const [roomId, setRoomId] = useState(null);

{inMeeting && (
  <VideoMeeting
    roomId={roomId}
    user={user}
    onLeave={() => {
      setInMeeting(false);
      setRoomId(null);
    }}
  />
)}
```

## 🔒 Security Features

1. **Authentication Required**
   - All API endpoints require JWT token
   - WebSocket connections authenticated

2. **Authorization Checks**
   - Only authorized users can join meetings
   - HR/Panelist-only controls enforced
   - Role-based access control

3. **Meeting Access**
   - Room ID required to join
   - Participant verification
   - Automatic cleanup on disconnect

## 🐛 Troubleshooting

### Camera/Microphone Not Working
```
Error: Failed to access camera/microphone
Solution: 
1. Check browser permissions
2. Ensure HTTPS or localhost
3. No other app using camera
4. Try different browser
```

### WebSocket Connection Failed
```
Error: WebSocket connection failed
Solution:
1. Check backend is running on port 8081
2. Verify CORS settings
3. Check firewall settings
4. Try clearing browser cache
```

### Screen Share Not Working
```
Error: Screen sharing failed
Solution:
1. Use Chrome/Edge (best support)
2. Grant screen share permission
3. Check browser version (latest recommended)
```

### Peer Connection Failed
```
Error: ICE connection failed
Solution:
1. Check network connectivity
2. Verify STUN server access
3. Check firewall/NAT settings
4. Try different network
```

## 📱 Browser Compatibility

### Recommended Browsers
- ✅ Chrome 90+ (Best)
- ✅ Edge 90+
- ✅ Firefox 88+
- ⚠️ Safari 14+ (Limited features)

### Required Features
- WebRTC support
- MediaDevices API
- Screen Capture API
- WebSocket support
- Canvas API

## 🎯 Best Practices

### For HR
1. Schedule interviews at least 24 hours in advance
2. Verify panelist availability before scheduling
3. Send meeting links to all participants
4. Test video setup before important interviews
5. Keep recordings organized

### For Panelists
1. Join 5 minutes early to test setup
2. Use good lighting and quiet environment
3. Test camera and microphone before interview
4. Use headphones to prevent echo
5. Keep annotations professional

### For Candidates
1. Test your setup beforehand
2. Ensure stable internet connection
3. Use professional background
4. Dress appropriately
5. Have resume/documents ready

## 📊 Performance Tips

1. **Network**
   - Minimum 2 Mbps upload/download
   - Wired connection preferred
   - Close bandwidth-heavy apps

2. **Hardware**
   - Modern CPU (i5/Ryzen 5 or better)
   - 8GB RAM minimum
   - HD webcam recommended

3. **Browser**
   - Keep browser updated
   - Close unnecessary tabs
   - Disable heavy extensions

## 🔄 Updates and Maintenance

### Regular Tasks
- Clear old meeting records (monthly)
- Review recording storage (weekly)
- Update browser versions
- Monitor server resources
- Backup meeting data

## 📞 Support

For issues or questions:
1. Check this documentation
2. Review troubleshooting section
3. Check browser console for errors
4. Contact system administrator

---

## 🎉 Quick Start Checklist

- [ ] Backend running on port 8081
- [ ] Frontend running on port 3000
- [ ] npm packages installed (sockjs-client, @stomp/stompjs)
- [ ] Database tables created
- [ ] Camera/microphone permissions granted
- [ ] Test meeting scheduled
- [ ] Video call tested successfully

---

**Made with ❤️ by Bob**

*Last Updated: May 15, 2026*