import React, { useState, useEffect, useRef } from 'react';
import authService from '../services/authService';
import './LoginChatBot.css';

/**
 * Login ChatBot Component - AI assistant with voice for conversational login
 * 
 * @author Bob
 */
function LoginChatBot({ onLoginSuccess }) {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState('');
    const [conversationState, setConversationState] = useState('initial');
    const [username, setUsername] = useState('');
    const [isListening, setIsListening] = useState(false);
    const [isSpeaking, setIsSpeaking] = useState(false);
    const messagesEndRef = useRef(null);
    const recognitionRef = useRef(null);
    const synthRef = useRef(window.speechSynthesis);

    // Initialize speech recognition
    useEffect(() => {
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            recognitionRef.current = new SpeechRecognition();
            recognitionRef.current.continuous = false;
            recognitionRef.current.interimResults = false;
            recognitionRef.current.lang = 'en-US';

            recognitionRef.current.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                setInputValue(transcript);
                setIsListening(false);
                // Auto-submit after voice input
                setTimeout(() => {
                    handleVoiceSubmit(transcript);
                }, 500);
            };

            recognitionRef.current.onerror = (event) => {
                console.error('Speech recognition error:', event.error);
                setIsListening(false);
                speakText("Sorry, I couldn't hear you clearly. Please try again.");
            };

            recognitionRef.current.onend = () => {
                setIsListening(false);
            };
        }
    }, []);

    // Scroll to bottom when messages change
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    // Get time-based greeting
    const getTimeBasedGreeting = () => {
        const hour = new Date().getHours();
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    };

    // Add initial greeting when chatbot opens
    useEffect(() => {
        if (isOpen && messages.length === 0) {
            const greeting = getTimeBasedGreeting();
            addBotMessage(`👋 Hello! ${greeting}! How can I help you today?`);
            setConversationState('initial');
        }
    }, [isOpen]);

    // Text-to-Speech function
    const speakText = (text) => {
        if ('speechSynthesis' in window) {
            synthRef.current.cancel();
            
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.rate = 0.9;
            utterance.pitch = 1;
            utterance.volume = 1;
            utterance.lang = 'en-US';
            
            utterance.onstart = () => setIsSpeaking(true);
            utterance.onend = () => setIsSpeaking(false);
            utterance.onerror = () => setIsSpeaking(false);
            
            synthRef.current.speak(utterance);
        }
    };

    const addBotMessage = (text, speak = true) => {
        setMessages(prev => [...prev, { type: 'bot', text, timestamp: new Date() }]);
        if (speak) {
            speakText(text);
        }
    };

    const addUserMessage = (text) => {
        setMessages(prev => [...prev, { type: 'user', text, timestamp: new Date() }]);
    };

    // Handle voice input submission
    const handleVoiceSubmit = async (transcript) => {
        if (!transcript.trim()) return;
        addUserMessage(transcript);
        await processUserInput(transcript);
    };

    const toggleChatBot = () => {
        setIsOpen(!isOpen);
    };

    const startListening = () => {
        if (recognitionRef.current && !isListening) {
            setIsListening(true);
            recognitionRef.current.start();
        }
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();
        
        if (!inputValue.trim()) return;

        const userInput = inputValue.trim();
        addUserMessage(userInput);
        setInputValue('');

        await processUserInput(userInput);
    };

    const processUserInput = async (input) => {
        const lowerInput = input.toLowerCase();

        switch (conversationState) {
            case 'initial':
                // Check if user wants to login OR directly provides username
                if (lowerInput.includes('login') || lowerInput.includes('sign in') || lowerInput.includes('log in')) {
                    addBotMessage("Sure, I can assist you with that!");
                    setTimeout(() => {
                        addBotMessage("Please tell me your username.");
                    }, 10000);
                    setConversationState('asking_username');
                } else {
                    // Assume user is providing username directly
                    setUsername(input);
                    setConversationState('checking_user');
                    addBotMessage("Give me a minute, I'm checking...");
                    
                    try {
                        const userExists = await checkUserExists(input);
                        
                        if (userExists) {
                            addBotMessage(`✅ Welcome back, ${input}!`);
                            setTimeout(() => {
                                addBotMessage("If you want to login, please tell me your password.");
                            }, 10000);
                            setConversationState('asking_password');
                        } else {
                            addBotMessage(`❌ Sorry, I couldn't find user "${input}" in our system.`);
                            setTimeout(() => {
                                addBotMessage("Please tell me your correct username.");
                            }, 10000);
                            setConversationState('asking_username');
                        }
                    } catch (error) {
                        addBotMessage("I couldn't verify the username. Please try again.");
                        setTimeout(() => {
                            addBotMessage("Please tell me your username.");
                        }, 10000);
                        setConversationState('asking_username');
                    }
                }
                break;

            case 'asking_username':
                setUsername(input);
                setConversationState('checking_user');
                addBotMessage("Give me a minute, I'm checking...");
                
                try {
                    const userExists = await checkUserExists(input);
                    
                    if (userExists) {
                        addBotMessage(`✅ Welcome back, ${input}!`);
                        setTimeout(() => {
                            addBotMessage("If you want to login, please tell me your password.");
                        }, 1000);
                        setConversationState('asking_password');
                    } else {
                        addBotMessage(`❌ Sorry, I couldn't find user "${input}" in our system.`);
                        setTimeout(() => {
                            addBotMessage("Please tell me your correct username.");
                        }, 1000);
                        setConversationState('asking_username');
                    }
                } catch (error) {
                    addBotMessage("I couldn't verify the username. Please try again.");
                    setTimeout(() => {
                        addBotMessage("Please tell me your username.");
                    }, 1000);
                    setConversationState('asking_username');
                }
                break;

            case 'asking_password':
                setConversationState('logging_in');
                addBotMessage("🔐 Let me verify your credentials...");
                
                try {
                    const response = await authService.login(username, input);
                    addBotMessage(`✅ Hello ${username}! Login successful!`);
                    addBotMessage("Redirecting to your dashboard...");
                    
                    setTimeout(() => {
                        onLoginSuccess(response);
                        setIsOpen(false);
                        resetChatBot();
                    }, 2000);
                } catch (error) {
                    addBotMessage("❌ The password you entered is incorrect.");
                    setTimeout(() => {
                        addBotMessage("Please tell me the correct password.");
                    }, 10000);
                    setConversationState('asking_password');
                }
                break;

            case 'checking_user':
            case 'logging_in':
                // Ignore input while processing
                break;

            default:
                addBotMessage("Let me help you login.");
                setTimeout(() => {
                    addBotMessage("Do you want to login? (yes/no)");
                }, 1000);
                setConversationState('initial');
        }
    };

    const checkUserExists = async (username) => {
        try {
            await authService.login(username, '__dummy_password_check__');
            return true; // If no error, user exists (unlikely with dummy password)
        } catch (error) {
            // Check error message to determine if user exists
            const errorMsg = error.toString().toLowerCase();
            console.log('User check error:', errorMsg); // Debug log
            
            // If error mentions "user not found" or "username", user doesn't exist
            if (errorMsg.includes('user not found') ||
                errorMsg.includes('username not found') ||
                errorMsg.includes('no user') ||
                errorMsg.includes('invalid username')) {
                return false; // User doesn't exist
            }
            
            // Any other error (like "bad credentials", "wrong password") means user exists
            return true; // User exists but password was wrong (expected)
        }
    };

    const resetChatBot = () => {
        setMessages([]);
        setConversationState('initial');
        setUsername('');
    };

    const formatTime = (timestamp) => {
        return timestamp.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };

    return (
        <>
            {/* Chat Icon Button */}
            <button 
                className={`chatbot-icon ${isOpen ? 'active' : ''}`}
                onClick={toggleChatBot}
                title="Login Assistant"
            >
                {isOpen ? '✕' : '💬'}
            </button>

            {/* ChatBot Window */}
            {isOpen && (
                <div className="chatbot-window">
                    <div className="chatbot-header">
                        <div className="chatbot-title">
                            <span className="bot-avatar">🤖</span>
                            <div>
                                <h3>Login Assistant</h3>
                                <span className="bot-status">● Online</span>
                            </div>
                        </div>
                        <button className="chatbot-close" onClick={toggleChatBot}>✕</button>
                    </div>

                    <div className="chatbot-messages">
                        {messages.map((msg, index) => (
                            <div key={index} className={`chatbot-message ${msg.type}`}>
                                <div className="message-bubble">
                                    {msg.text}
                                </div>
                                <span className="message-time">{formatTime(msg.timestamp)}</span>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>

                    <form className="chatbot-input-form" onSubmit={handleSendMessage}>
                        <button
                            type="button"
                            className={`voice-btn ${isListening ? 'listening' : ''} ${isSpeaking ? 'speaking' : ''}`}
                            onClick={startListening}
                            disabled={isListening || conversationState === 'checking_user' || conversationState === 'logging_in'}
                            title={isListening ? 'Listening...' : isSpeaking ? 'Bot is speaking...' : 'Click to speak'}
                        >
                            {isListening ? '🎤' : isSpeaking ? '🔊' : '🎙️'}
                        </button>
                        
                        <input
                            type={conversationState === 'asking_password' ? 'password' : 'text'}
                            className="chatbot-input"
                            placeholder={
                                isListening ? 'Listening...' :
                                isSpeaking ? 'Bot is speaking...' :
                                conversationState === 'asking_password' 
                                    ? 'Type or speak password...' 
                                    : 'Type or speak your message...'
                            }
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                            disabled={conversationState === 'checking_user' || conversationState === 'logging_in' || isListening}
                        />
                        
                        <button 
                            type="submit" 
                            className="chatbot-send-btn"
                            disabled={!inputValue.trim() || conversationState === 'checking_user' || conversationState === 'logging_in' || isListening}
                        >
                            📤
                        </button>
                    </form>
                </div>
            )}
        </>
    );
}

export default LoginChatBot;

// Made with Bob