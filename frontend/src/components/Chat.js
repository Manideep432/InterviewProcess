import React, { useState, useEffect, useRef } from 'react';
import chatService from '../services/chatService';
import './Chat.css';

/**
 * Chat Component - Real-time chat interface
 * 
 * @author Bob
 */
function Chat() {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const messagesEndRef = useRef(null);
    const username = localStorage.getItem('username');

    // Scroll to bottom of messages
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    // Load messages on component mount
    useEffect(() => {
        loadMessages();
        // Poll for new messages every 3 seconds
        const interval = setInterval(loadMessages, 3000);
        return () => clearInterval(interval);
    }, []);

    // Scroll to bottom when messages change
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // Load all messages
    const loadMessages = async () => {
        try {
            const data = await chatService.getAllMessages();
            // Reverse to show oldest first
            setMessages(data.reverse());
            setError('');
        } catch (err) {
            console.error('Failed to load messages:', err);
            // Don't show error on polling failures
        }
    };

    // Send a new message
    const handleSendMessage = async (e) => {
        e.preventDefault();
        
        if (!newMessage.trim()) {
            return;
        }

        setLoading(true);
        setError('');

        try {
            await chatService.sendMessage(newMessage);
            setNewMessage('');
            await loadMessages();
        } catch (err) {
            setError(err.toString());
        } finally {
            setLoading(false);
        }
    };

    // Delete a message
    const handleDeleteMessage = async (messageId) => {
        if (!window.confirm('Are you sure you want to delete this message?')) {
            return;
        }

        try {
            await chatService.deleteMessage(messageId);
            await loadMessages();
        } catch (err) {
            setError(err.toString());
        }
    };

    // Format timestamp
    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        return date.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };

    return (
        <div className="chat-container">
            <div className="chat-header">
                <h2>💬 Chat Room</h2>
                <span className="online-indicator">● Online</span>
            </div>

            {error && (
                <div className="chat-error">
                    {error}
                </div>
            )}

            <div className="chat-messages">
                {messages.length === 0 ? (
                    <div className="no-messages">
                        <p>No messages yet. Start the conversation!</p>
                    </div>
                ) : (
                    messages.map((msg) => (
                        <div 
                            key={msg.id} 
                            className={`message ${msg.username === username ? 'own-message' : 'other-message'}`}
                        >
                            <div className="message-header">
                                <span className="message-username">{msg.username}</span>
                                <span className="message-time">{formatTime(msg.timestamp)}</span>
                            </div>
                            <div className="message-content">
                                {msg.message}
                            </div>
                            {msg.username === username && (
                                <button 
                                    className="delete-btn"
                                    onClick={() => handleDeleteMessage(msg.id)}
                                    title="Delete message"
                                >
                                    🗑️
                                </button>
                            )}
                        </div>
                    ))
                )}
                <div ref={messagesEndRef} />
            </div>

            <form className="chat-input-form" onSubmit={handleSendMessage}>
                <input
                    type="text"
                    className="chat-input"
                    placeholder="Type your message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    disabled={loading}
                    maxLength={1000}
                />
                <button 
                    type="submit" 
                    className="send-btn"
                    disabled={loading || !newMessage.trim()}
                >
                    {loading ? '⏳' : '📤'} Send
                </button>
            </form>
        </div>
    );
}

export default Chat;

// Made with Bob