import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth/mfa';

/**
 * MFA Service - API calls for Multi-Factor Authentication
 * 
 * @author Bob
 */
class MfaService {
    /**
     * Setup MFA - Get QR code and secret
     */
    async setupMfa() {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.post(
                `${API_URL}/setup`,
                {},
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to setup MFA';
        }
    }

    /**
     * Enable MFA with verification code
     */
    async enableMfa(secret, code) {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.post(
                `${API_URL}/enable`,
                { secret, code },
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to enable MFA';
        }
    }

    /**
     * Disable MFA
     */
    async disableMfa(code) {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.post(
                `${API_URL}/disable`,
                { code },
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to disable MFA';
        }
    }

    /**
     * Check MFA status
     */
    async getMfaStatus() {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await axios.get(
                `${API_URL}/status`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'Failed to get MFA status';
        }
    }

    /**
     * Login with MFA code
     */
    async loginWithMfa(username, password, mfaCode) {
        try {
            const response = await axios.post(
                'http://localhost:8081/api/auth/login/mfa',
                {
                    username,
                    password,
                    mfaCode: parseInt(mfaCode)
                },
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (response.data.token) {
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('user', JSON.stringify({
                    username: response.data.username,
                    email: response.data.email
                }));
            }

            return response.data;
        } catch (error) {
            throw error.response?.data?.error || 'MFA login failed';
        }
    }
}

export default new MfaService();

// Made with Bob