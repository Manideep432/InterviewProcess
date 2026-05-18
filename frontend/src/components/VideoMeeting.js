import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import './VideoMeeting.css';

/**
 * Video Meeting Component - Full-featured video call with WebRTC
 * Features: Video/Audio, Screen Share, Mute/Unmute, Annotations, Chat
 * @author Bob
 */
const VideoMeeting = ({ roomId, user, onLeave }) => {
  // Video/Audio refs
  const localVideoRef = useRef(null);
  const remoteVideoRef = useRef(null);
  const screenShareRef = useRef(null);
  const canvasRef = useRef(null);
  
  // WebRTC refs
  const peerConnectionRef = useRef(null);
  const localStreamRef = useRef(null);
  const screenStreamRef = useRef(null);
  const stompClientRef = useRef(null);
  
  // State
  const [isAudioMuted, setIsAudioMuted] = useState(false);
  const [isVideoOff, setIsVideoOff] = useState(false);
  const [isScreenSharing, setIsScreenSharing] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [showChat, setShowChat] = useState(false);
  const [showAnnotation, setShowAnnotation] = useState(false);
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');
  const [participants, setParticipants] = useState([]);
  const [meeting, setMeeting] = useState(null);
  const [error, setError] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  
  // Annotation state
  const [isDrawing, setIsDrawing] = useState(false);
  const [drawColor, setDrawColor] = useState('#FF0000');
  const [drawWidth, setDrawWidth] = useState(3);
  const [annotationTool, setAnnotationTool] = useState('pen'); // pen, eraser, clear
  
  // WebRTC configuration
  const rtcConfig = {
    iceServers: [
      { urls: 'stun:stun.l.google.com:19302' },
      { urls: 'stun:stun1.l.google.com:19302' }
    ]
  };

  useEffect(() => {
    initializeMeeting();
    return () => {
      cleanup();
    };
  }, [roomId]);

  const initializeMeeting = async () => {
    try {
      // Fetch meeting details
      await fetchMeetingDetails();
      
      // Initialize media
      await initializeMedia();
      
      // Connect to signaling server
      connectToSignalingServer();
      
      // Join meeting
      await joinMeeting();
    } catch (err) {
      setError('Failed to initialize meeting: ' + err.message);
      console.error('Initialization error:', err);
    }
  };

  const fetchMeetingDetails = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/room/${roomId}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      const data = await response.json();
      if (data.success) {
        setMeeting(data.meeting);
      }
    } catch (err) {
      console.error('Error fetching meeting:', err);
    }
  };

  const initializeMedia = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: 1280, height: 720 },
        audio: true
      });
      
      localStreamRef.current = stream;
      if (localVideoRef.current) {
        localVideoRef.current.srcObject = stream;
      }
      
      // Initialize peer connection
      initializePeerConnection();
    } catch (err) {
      setError('Failed to access camera/microphone: ' + err.message);
      throw err;
    }
  };

  const initializePeerConnection = () => {
    const peerConnection = new RTCPeerConnection(rtcConfig);
    peerConnectionRef.current = peerConnection;

    // Add local stream tracks
    if (localStreamRef.current) {
      localStreamRef.current.getTracks().forEach(track => {
        peerConnection.addTrack(track, localStreamRef.current);
      });
    }

    // Handle incoming tracks
    peerConnection.ontrack = (event) => {
      if (remoteVideoRef.current) {
        remoteVideoRef.current.srcObject = event.streams[0];
      }
    };

    // Handle ICE candidates
    peerConnection.onicecandidate = (event) => {
      if (event.candidate && stompClientRef.current) {
        stompClientRef.current.send(
          `/app/meeting/${roomId}/ice-candidate`,
          {},
          JSON.stringify({
            candidate: event.candidate,
            userId: user.id
          })
        );
      }
    };

    // Handle connection state
    peerConnection.onconnectionstatechange = () => {
      console.log('Connection state:', peerConnection.connectionState);
      setIsConnected(peerConnection.connectionState === 'connected');
    };
  };

  const connectToSignalingServer = () => {
    const socket = new SockJS(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/ws`);
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, () => {
      stompClientRef.current = stompClient;
      
      // Subscribe to meeting topics
      stompClient.subscribe(`/topic/meeting/${roomId}/offer`, handleOffer);
      stompClient.subscribe(`/topic/meeting/${roomId}/answer`, handleAnswer);
      stompClient.subscribe(`/topic/meeting/${roomId}/ice-candidate`, handleIceCandidate);
      stompClient.subscribe(`/topic/meeting/${roomId}/participants`, handleParticipantUpdate);
      stompClient.subscribe(`/topic/meeting/${roomId}/screen-share`, handleScreenShare);
      stompClient.subscribe(`/topic/meeting/${roomId}/audio`, handleAudioToggle);
      stompClient.subscribe(`/topic/meeting/${roomId}/video`, handleVideoToggle);
      stompClient.subscribe(`/topic/meeting/${roomId}/annotation`, handleAnnotation);
      stompClient.subscribe(`/topic/meeting/${roomId}/chat`, handleChatMessage);
      stompClient.subscribe(`/topic/meeting/${roomId}/control`, handleMeetingControl);
    });
  };

  const joinMeeting = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/join`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ roomId, userId: user.id })
        }
      );
      
      const data = await response.json();
      if (data.success) {
        // Notify others
        if (stompClientRef.current) {
          stompClientRef.current.send(
            `/app/meeting/${roomId}/join`,
            {},
            JSON.stringify({ username: user.username, userId: user.id })
          );
        }
        
        // Create offer for existing participants
        createOffer();
      }
    } catch (err) {
      setError('Failed to join meeting: ' + err.message);
    }
  };

  const createOffer = async () => {
    try {
      const peerConnection = peerConnectionRef.current;
      const offer = await peerConnection.createOffer();
      await peerConnection.setLocalDescription(offer);
      
      if (stompClientRef.current) {
        stompClientRef.current.send(
          `/app/meeting/${roomId}/offer`,
          {},
          JSON.stringify({ offer, userId: user.id })
        );
      }
    } catch (err) {
      console.error('Error creating offer:', err);
    }
  };

  const handleOffer = async (message) => {
    try {
      const data = JSON.parse(message.body);
      if (data.senderId === user.id) return;
      
      const peerConnection = peerConnectionRef.current;
      await peerConnection.setRemoteDescription(new RTCSessionDescription(data.offer));
      
      const answer = await peerConnection.createAnswer();
      await peerConnection.setLocalDescription(answer);
      
      if (stompClientRef.current) {
        stompClientRef.current.send(
          `/app/meeting/${roomId}/answer`,
          {},
          JSON.stringify({ answer, userId: user.id })
        );
      }
    } catch (err) {
      console.error('Error handling offer:', err);
    }
  };

  const handleAnswer = async (message) => {
    try {
      const data = JSON.parse(message.body);
      if (data.senderId === user.id) return;
      
      const peerConnection = peerConnectionRef.current;
      await peerConnection.setRemoteDescription(new RTCSessionDescription(data.answer));
    } catch (err) {
      console.error('Error handling answer:', err);
    }
  };

  const handleIceCandidate = async (message) => {
    try {
      const data = JSON.parse(message.body);
      if (data.senderId === user.id) return;
      
      const peerConnection = peerConnectionRef.current;
      await peerConnection.addIceCandidate(new RTCIceCandidate(data.candidate));
    } catch (err) {
      console.error('Error handling ICE candidate:', err);
    }
  };

  const handleParticipantUpdate = (message) => {
    const data = JSON.parse(message.body);
    console.log('Participant update:', data);
    // Update participants list
  };

  const handleScreenShare = (message) => {
    const data = JSON.parse(message.body);
    console.log('Screen share event:', data);
  };

  const handleAudioToggle = (message) => {
    const data = JSON.parse(message.body);
    console.log('Audio toggle:', data);
  };

  const handleVideoToggle = (message) => {
    const data = JSON.parse(message.body);
    console.log('Video toggle:', data);
  };

  const handleAnnotation = (message) => {
    const data = JSON.parse(message.body);
    if (data.userId === user.id) return;
    
    // Draw annotation from other user
    drawAnnotationFromData(data);
  };

  const handleChatMessage = (message) => {
    const data = JSON.parse(message.body);
    setChatMessages(prev => [...prev, data]);
  };

  const handleMeetingControl = (message) => {
    const data = JSON.parse(message.body);
    console.log('Meeting control:', data);
  };

  // Media controls
  const toggleAudio = () => {
    if (localStreamRef.current) {
      const audioTrack = localStreamRef.current.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        setIsAudioMuted(!audioTrack.enabled);
        
        // Notify others
        if (stompClientRef.current) {
          stompClientRef.current.send(
            `/app/meeting/${roomId}/audio/toggle`,
            {},
            JSON.stringify({ muted: !audioTrack.enabled, userId: user.id })
          );
        }
      }
    }
  };

  const toggleVideo = () => {
    if (localStreamRef.current) {
      const videoTrack = localStreamRef.current.getVideoTracks()[0];
      if (videoTrack) {
        videoTrack.enabled = !videoTrack.enabled;
        setIsVideoOff(!videoTrack.enabled);
        
        // Notify others
        if (stompClientRef.current) {
          stompClientRef.current.send(
            `/app/meeting/${roomId}/video/toggle`,
            {},
            JSON.stringify({ disabled: !videoTrack.enabled, userId: user.id })
          );
        }
      }
    }
  };

  const startScreenShare = async () => {
    try {
      const screenStream = await navigator.mediaDevices.getDisplayMedia({
        video: { cursor: 'always' },
        audio: false
      });
      
      screenStreamRef.current = screenStream;
      if (screenShareRef.current) {
        screenShareRef.current.srcObject = screenStream;
      }
      
      // Replace video track in peer connection
      const peerConnection = peerConnectionRef.current;
      const videoTrack = screenStream.getVideoTracks()[0];
      const sender = peerConnection.getSenders().find(s => s.track?.kind === 'video');
      if (sender) {
        sender.replaceTrack(videoTrack);
      }
      
      setIsScreenSharing(true);
      
      // Notify others
      if (stompClientRef.current) {
        stompClientRef.current.send(
          `/app/meeting/${roomId}/screen-share/start`,
          {},
          JSON.stringify({ userId: user.id })
        );
      }
      
      // Handle screen share stop
      videoTrack.onended = () => {
        stopScreenShare();
      };
    } catch (err) {
      console.error('Error starting screen share:', err);
      setError('Failed to start screen sharing');
    }
  };

  const stopScreenShare = () => {
    if (screenStreamRef.current) {
      screenStreamRef.current.getTracks().forEach(track => track.stop());
      screenStreamRef.current = null;
    }
    
    // Restore camera video
    if (localStreamRef.current && peerConnectionRef.current) {
      const videoTrack = localStreamRef.current.getVideoTracks()[0];
      const sender = peerConnectionRef.current.getSenders().find(s => s.track?.kind === 'video');
      if (sender && videoTrack) {
        sender.replaceTrack(videoTrack);
      }
    }
    
    setIsScreenSharing(false);
    
    // Notify others
    if (stompClientRef.current) {
      stompClientRef.current.send(
        `/app/meeting/${roomId}/screen-share/stop`,
        {},
        JSON.stringify({ userId: user.id })
      );
    }
  };

  const toggleRecording = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/recording`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ roomId, userId: user.id, enable: !isRecording })
        }
      );
      
      const data = await response.json();
      if (data.success) {
        setIsRecording(!isRecording);
      }
    } catch (err) {
      console.error('Error toggling recording:', err);
    }
  };

  // Annotation functions
  const startDrawing = (e) => {
    if (!showAnnotation) return;
    setIsDrawing(true);
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    ctx.beginPath();
    ctx.moveTo(x, y);
  };

  const draw = (e) => {
    if (!isDrawing || !showAnnotation) return;
    
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    ctx.strokeStyle = annotationTool === 'eraser' ? '#FFFFFF' : drawColor;
    ctx.lineWidth = annotationTool === 'eraser' ? 20 : drawWidth;
    ctx.lineCap = 'round';
    ctx.lineTo(x, y);
    ctx.stroke();
    
    // Send annotation to others
    if (stompClientRef.current) {
      stompClientRef.current.send(
        `/app/meeting/${roomId}/annotation`,
        {},
        JSON.stringify({
          type: 'draw',
          x, y,
          color: ctx.strokeStyle,
          width: ctx.lineWidth,
          tool: annotationTool
        })
      );
    }
  };

  const stopDrawing = () => {
    setIsDrawing(false);
  };

  const clearAnnotations = () => {
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Notify others
    if (stompClientRef.current) {
      stompClientRef.current.send(
        `/app/meeting/${roomId}/annotation`,
        {},
        JSON.stringify({ type: 'clear' })
      );
    }
  };

  const drawAnnotationFromData = (data) => {
    if (data.type === 'clear') {
      const canvas = canvasRef.current;
      const ctx = canvas.getContext('2d');
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      return;
    }
    
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    ctx.strokeStyle = data.color;
    ctx.lineWidth = data.width;
    ctx.lineCap = 'round';
    ctx.lineTo(data.x, data.y);
    ctx.stroke();
  };

  // Chat functions
  const sendChatMessage = () => {
    if (!chatInput.trim() || !stompClientRef.current) return;
    
    stompClientRef.current.send(
      `/app/meeting/${roomId}/chat`,
      {},
      JSON.stringify({
        message: chatInput,
        username: user.username,
        userId: user.id
      })
    );
    
    setChatInput('');
  };

  const leaveMeeting = async () => {
    try {
      const token = localStorage.getItem('token');
      await fetch(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/leave`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ roomId, userId: user.id })
        }
      );
      
      // Notify others
      if (stompClientRef.current) {
        stompClientRef.current.send(
          `/app/meeting/${roomId}/leave`,
          {},
          JSON.stringify({ userId: user.id })
        );
      }
      
      cleanup();
      if (onLeave) onLeave();
    } catch (err) {
      console.error('Error leaving meeting:', err);
    }
  };

  const endMeeting = async () => {
    try {
      const token = localStorage.getItem('token');
      await fetch(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/end`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ roomId, userId: user.id })
        }
      );
      
      cleanup();
      if (onLeave) onLeave();
    } catch (err) {
      console.error('Error ending meeting:', err);
    }
  };

  const cleanup = () => {
    // Stop all tracks
    if (localStreamRef.current) {
      localStreamRef.current.getTracks().forEach(track => track.stop());
    }
    if (screenStreamRef.current) {
      screenStreamRef.current.getTracks().forEach(track => track.stop());
    }
    
    // Close peer connection
    if (peerConnectionRef.current) {
      peerConnectionRef.current.close();
    }
    
    // Disconnect signaling
    if (stompClientRef.current) {
      stompClientRef.current.disconnect();
    }
  };

  return (
    <div className="video-meeting-container">
      {error && <div className="error-banner">{error}</div>}
      
      <div className="meeting-header">
        <div className="meeting-info">
          <h2>🎥 Video Interview</h2>
          <span className="room-id">Room: {roomId}</span>
          <span className={`connection-status ${isConnected ? 'connected' : 'connecting'}`}>
            {isConnected ? '● Connected' : '○ Connecting...'}
          </span>
        </div>
      </div>

      <div className="video-grid">
        {/* Remote Video (Main) */}
        <div className="video-main">
          <video ref={remoteVideoRef} autoPlay playsInline className="remote-video" />
          {isScreenSharing && (
            <video ref={screenShareRef} autoPlay playsInline className="screen-share-video" />
          )}
          
          {/* Annotation Canvas */}
          {showAnnotation && (
            <canvas
              ref={canvasRef}
              className="annotation-canvas"
              width={1280}
              height={720}
              onMouseDown={startDrawing}
              onMouseMove={draw}
              onMouseUp={stopDrawing}
              onMouseLeave={stopDrawing}
            />
          )}
        </div>

        {/* Local Video (Picture-in-Picture) */}
        <div className="video-local">
          <video ref={localVideoRef} autoPlay playsInline muted className="local-video" />
          <span className="local-label">You</span>
        </div>
      </div>

      {/* Controls */}
      <div className="meeting-controls">
        <button 
          className={`control-btn ${isAudioMuted ? 'muted' : ''}`}
          onClick={toggleAudio}
          title={isAudioMuted ? 'Unmute' : 'Mute'}
        >
          {isAudioMuted ? '🔇' : '🎤'}
        </button>

        <button 
          className={`control-btn ${isVideoOff ? 'off' : ''}`}
          onClick={toggleVideo}
          title={isVideoOff ? 'Turn On Video' : 'Turn Off Video'}
        >
          {isVideoOff ? '📹' : '📷'}
        </button>

        <button 
          className={`control-btn ${isScreenSharing ? 'active' : ''}`}
          onClick={isScreenSharing ? stopScreenShare : startScreenShare}
          title={isScreenSharing ? 'Stop Sharing' : 'Share Screen'}
        >
          🖥️
        </button>

        <button 
          className={`control-btn ${showAnnotation ? 'active' : ''}`}
          onClick={() => setShowAnnotation(!showAnnotation)}
          title="Annotation Tools"
        >
          ✏️
        </button>

        <button 
          className={`control-btn ${showChat ? 'active' : ''}`}
          onClick={() => setShowChat(!showChat)}
          title="Chat"
        >
          💬
        </button>

        <button 
          className={`control-btn ${isRecording ? 'recording' : ''}`}
          onClick={toggleRecording}
          title={isRecording ? 'Stop Recording' : 'Start Recording'}
        >
          {isRecording ? '⏹️' : '⏺️'}
        </button>

        <button 
          className="control-btn leave-btn"
          onClick={leaveMeeting}
          title="Leave Meeting"
        >
          📞
        </button>

        <button 
          className="control-btn end-btn"
          onClick={endMeeting}
          title="End Meeting"
        >
          ❌
        </button>
      </div>

      {/* Annotation Tools */}
      {showAnnotation && (
        <div className="annotation-tools">
          <button 
            className={`tool-btn ${annotationTool === 'pen' ? 'active' : ''}`}
            onClick={() => setAnnotationTool('pen')}
          >
            ✏️ Pen
          </button>
          <button 
            className={`tool-btn ${annotationTool === 'eraser' ? 'active' : ''}`}
            onClick={() => setAnnotationTool('eraser')}
          >
            🧹 Eraser
          </button>
          <input 
            type="color" 
            value={drawColor} 
            onChange={(e) => setDrawColor(e.target.value)}
            className="color-picker"
          />
          <input 
            type="range" 
            min="1" 
            max="10" 
            value={drawWidth}
            onChange={(e) => setDrawWidth(parseInt(e.target.value))}
            className="width-slider"
          />
          <button className="tool-btn" onClick={clearAnnotations}>
            🗑️ Clear
          </button>
        </div>
      )}

      {/* Chat Panel */}
      {showChat && (
        <div className="chat-panel">
          <div className="chat-header">
            <h3>💬 Chat</h3>
            <button onClick={() => setShowChat(false)}>✕</button>
          </div>
          <div className="chat-messages">
            {chatMessages.map((msg, index) => (
              <div key={index} className="chat-message">
                <strong>{msg.username}:</strong> {msg.message}
              </div>
            ))}
          </div>
          <div className="chat-input-container">
            <input
              type="text"
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && sendChatMessage()}
              placeholder="Type a message..."
              className="chat-input"
            />
            <button onClick={sendChatMessage} className="send-btn">Send</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default VideoMeeting;

// Made with Bob