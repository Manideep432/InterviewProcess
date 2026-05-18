import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import mfaService from '../services/mfaService';
import LoginChatBot from './LoginChatBot';
import './Login.css';

/**
 * Login Component - User login form with blue theme and MFA support
 */
const Login = ({ onLoginSuccess, onSwitchToRegister, onSwitchToForgotPassword }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [otp, setOtp] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [maskedEmail, setMaskedEmail] = useState('');
  const [resendTimer, setResendTimer] = useState(0);
  const [canResend, setCanResend] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const validateForm = () => {
    if (!formData.username.trim()) {
      setError('Username is required');
      return false;
    }
    if (!formData.password) {
      setError('Password is required');
      return false;
    }
    // Note: Login doesn't need strict validation like registration
    // Backend will validate the password during authentication
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError('');

    try {
      // Request OTP
      const response = await authService.requestOtp(formData.username, formData.password);
      console.log('OTP sent:', response);
      setOtpSent(true);
      setMaskedEmail(response.email);
      setResendTimer(60); // Start 60 second timer
      setCanResend(false);
      setError('');
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    
    if (!otp || otp.length !== 6) {
      setError('Please enter a valid 6-digit OTP');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authService.verifyOtp(
        formData.username,
        formData.password,
        otp
      );
      console.log('Login successful:', response);
      onLoginSuccess(response);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  // Timer effect for resend OTP
  useEffect(() => {
    let interval;
    if (otpSent && resendTimer > 0) {
      interval = setInterval(() => {
        setResendTimer((prev) => {
          if (prev <= 1) {
            setCanResend(true);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [otpSent, resendTimer]);

  const handleBackToLogin = () => {
    setOtpSent(false);
    setOtp('');
    setError('');
    setMaskedEmail('');
    setResendTimer(0);
    setCanResend(false);
  };

  const handleResendOtp = async () => {
    if (!canResend || loading) return;

    setLoading(true);
    setError('');
    setCanResend(false);

    try {
      const response = await authService.requestOtp(formData.username, formData.password);
      console.log('OTP resent:', response);
      setResendTimer(60); // Reset timer to 60 seconds
      setError('');
      // Show success message briefly
      const successMsg = '✅ OTP resent successfully!';
      setError(successMsg);
      setTimeout(() => {
        if (error === successMsg) setError('');
      }, 3000);
    } catch (err) {
      setError(err);
      setCanResend(true); // Allow retry on error
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h2>{otpSent ? '📧 Email Verification' : '🔐 Login'}</h2>
          <p>
            {otpSent
              ? `Enter the 6-digit OTP sent to ${maskedEmail}`
              : 'Welcome back! Please login to your account'
            }
          </p>
        </div>

        {!otpSent ? (
          <form onSubmit={handleSubmit} className="login-form">
            {error && (
              <div className="error-message">
                ❌ {error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="Enter your username"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  disabled={loading}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={loading}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading ? '⏳ Logging in...' : '✅ Login'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleOtpSubmit} className="login-form">
            {error && (
              <div className="error-message">
                ❌ {error}
              </div>
            )}

            <div className="mfa-info">
              <p>📧 Check your email for the OTP code</p>
              <p style={{fontSize: '12px', color: '#666'}}>Code expires in 5 minutes</p>
            </div>

            <div className="form-group">
              <label htmlFor="otp">One-Time Password (OTP)</label>
              <input
                type="text"
                id="otp"
                name="otp"
                value={otp}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                  setOtp(value);
                  setError('');
                }}
                placeholder="000000"
                maxLength="6"
                disabled={loading}
                className="mfa-code-input"
                autoComplete="off"
                autoFocus
              />
              <small className="input-hint">Enter the 6-digit OTP from your email</small>
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={loading || otp.length !== 6}
            >
              {loading ? '⏳ Verifying...' : '✅ Verify & Login'}
            </button>

            <div className="otp-actions">
              <button
                type="button"
                className="resend-otp-button"
                onClick={handleResendOtp}
                disabled={!canResend || loading}
              >
                {canResend ? '🔄 Resend OTP' : `⏱️ Resend in ${resendTimer}s`}
              </button>

              <button
                type="button"
                className="back-to-login-button"
                onClick={handleBackToLogin}
                disabled={loading}
              >
                ← Back to Login
              </button>
            </div>
          </form>
        )}

        <div className="login-footer">
          <p>
            Forgot your password?{' '}
            <span className="link" onClick={onSwitchToForgotPassword}>
              Reset here
            </span>
          </p>
          <p>
            Don't have an account?{' '}
            <span className="link" onClick={onSwitchToRegister}>
              Register here
            </span>
          </p>
        </div>
      </div>

      {/* AI ChatBot Assistant */}
      <LoginChatBot onLoginSuccess={onLoginSuccess} />
    </div>
  );
};

export default Login;

// Made with Bob
