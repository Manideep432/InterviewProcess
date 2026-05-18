import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth';

/**
 * Authentication Service - Handles API calls for authentication
 */
class AuthService {
  /**
   * Register a new user
   */
  async register(username, email, password, role) {
    try {
      const response = await axios.post(`${API_URL}/register`, {
        username,
        email,
        password,
        role
      });
      
      // Don't auto-login after registration
      // User must login separately with OTP verification
      return response.data;
    } catch (error) {
      throw error.response?.data?.error || 'Registration failed';
    }
  }

  /**
   * Login user
   * Note: If MFA is enabled, this will throw 'MFA_REQUIRED' error
   */
  async login(username, password) {
    try {
      const response = await axios.post(`${API_URL}/login`, {
        username,
        password
      });
      
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify({
          id: response.data.id,
          username: response.data.username,
          email: response.data.email,
          role: response.data.role
        }));
      }
      
      return response.data;
    } catch (error) {
      // Check if error is MFA_REQUIRED
      const errorMessage = error.response?.data?.error || 'Login failed';
      throw errorMessage;
    }
  }

  /**
   * Logout user
   */
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  /**
   * Get current user
   */
  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  }

  /**
   * Get auth token
   */
  getToken() {
    return localStorage.getItem('token');
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn() {
    return !!this.getToken();
  }

  /**
   * Request OTP for login
   */
  async requestOtp(username, password) {
    try {
      const response = await axios.post(`${API_URL}/login/request-otp`, {
        username,
        password
      });
      return response.data;
    } catch (error) {
      throw error.response?.data?.error || 'Failed to send OTP';
    }
  }

  /**
   * Verify OTP and complete login
   */
  async verifyOtp(username, password, otp) {
    try {
      const response = await axios.post(`${API_URL}/login/verify-otp`, {
        username,
        password,
        otp
      });
      
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify({
          id: response.data.id,
          username: response.data.username,
          email: response.data.email,
          role: response.data.role
        }));
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data?.error || 'OTP verification failed';
    }
  }
}

export default new AuthService();

// Made with Bob
