import axios from 'axios';

const API_URL = 'http://localhost:8081/api/chat';

/**
 * Chat Service - API calls for chat operations
 * 
 * @author Bob
 */
class ChatService {
    /**
     * Send a chat message
     */
    async sendMessage(message) {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.post(
                `${API_URL}/send`,
                { message },
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to send message';
        }
    }

    /**
     * Get all chat messages
     */
    async getAllMessages() {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.get(
                `${API_URL}/messages`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data.messages;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to fetch messages';
        }
    }

    /**
     * Get messages by username
     */
    async getMessagesByUsername(username) {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.get(
                `${API_URL}/messages/${username}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data.messages;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to fetch messages';
        }
    }

    /**
     * Delete a message
     */
    async deleteMessage(messageId) {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.delete(
                `${API_URL}/messages/${messageId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to delete message';
        }
    }
}

export default new ChatService();

// Made with Bob