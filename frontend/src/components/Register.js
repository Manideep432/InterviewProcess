import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import './Register.css';

/**
 * Register Component - User registration form with blue theme
 */
const Register = ({ onRegisterSuccess, onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'CANDIDATE' // Default role
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [passwordStrength, setPasswordStrength] = useState({ score: 0, label: '', color: '' });
  const [passwordSuggestions, setPasswordSuggestions] = useState([]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  // Calculate password strength in real-time
  useEffect(() => {
    if (formData.password) {
      const strength = calculatePasswordStrength(formData.password);
      setPasswordStrength(strength);
      
      // Generate suggestions if password is weak
      if (strength.score < 3) {
        setPasswordSuggestions(generatePasswordSuggestions(formData.password));
      } else {
        setPasswordSuggestions([]);
      }
    } else {
      setPasswordStrength({ score: 0, label: '', color: '' });
      setPasswordSuggestions([]);
    }
  }, [formData.password]);

  const calculatePasswordStrength = (password) => {
    let score = 0;
    const issues = [];

    // Length check
    if (password.length >= 15) score++;
    else issues.push('At least 15 characters');

    // Character diversity
    const hasLowercase = /[a-z]/.test(password);
    const hasUppercase = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecial = /[^a-zA-Z0-9]/.test(password);
    const characterTypes = [hasLowercase, hasUppercase, hasNumber, hasSpecial].filter(Boolean).length;

    if (characterTypes >= 2) score++;
    else issues.push('Mix of character types');

    if (characterTypes >= 3) score++;
    if (characterTypes === 4) score++;

    // Check for weak patterns
    const weakPatterns = [
      { pattern: /password/i, name: 'word "password"' },
      { pattern: /12345/, name: 'sequential numbers' },
      { pattern: /qwerty/i, name: 'keyboard pattern' },
      { pattern: /(.)\1{3,}/, name: 'repeated characters' }
    ];

    let hasWeakPattern = false;
    for (const { pattern, name } of weakPatterns) {
      if (pattern.test(password)) {
        hasWeakPattern = true;
        issues.push(`Avoid ${name}`);
        break;
      }
    }

    if (!hasWeakPattern) score++;

    // Determine label and color
    let label, color;
    if (score === 0) {
      label = 'Very Weak';
      color = '#d32f2f';
    } else if (score === 1) {
      label = 'Weak';
      color = '#f57c00';
    } else if (score === 2) {
      label = 'Fair';
      color = '#fbc02d';
    } else if (score === 3) {
      label = 'Good';
      color = '#689f38';
    } else if (score === 4) {
      label = 'Strong';
      color = '#388e3c';
    } else {
      label = 'Very Strong';
      color = '#1b5e20';
    }

    return { score, label, color, issues };
  };

  const generatePasswordSuggestions = (currentPassword) => {
    const suggestions = [];
    const strength = calculatePasswordStrength(currentPassword);

    if (strength.issues && strength.issues.length > 0) {
      suggestions.push(`❌ ${strength.issues.join(', ')}`);
    }

    // Provide example passwords
    suggestions.push('✅ Try: MySecurePass2024!');
    suggestions.push('✅ Try: HelloWorld@2024#');
    suggestions.push('✅ Try: StrongLogin99$Pass');

    return suggestions;
  };

  const validateForm = () => {
    if (!formData.username.trim()) {
      setError('Username is required');
      return false;
    }
    if (formData.username.length < 3) {
      setError('Username must be at least 3 characters');
      return false;
    }
    if (!formData.email.trim()) {
      setError('Email is required');
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return false;
    }
    if (!formData.password) {
      setError('Password is required');
      return false;
    }
    if (formData.password.length < 15) {
      setError('Password must be at least 15 characters');
      return false;
    }
    
    // Check character type diversity (at least 2 types)
    const hasLowercase = /[a-z]/.test(formData.password);
    const hasUppercase = /[A-Z]/.test(formData.password);
    const hasNumber = /[0-9]/.test(formData.password);
    const hasSpecial = /[^a-zA-Z0-9]/.test(formData.password);
    const characterTypes = [hasLowercase, hasUppercase, hasNumber, hasSpecial].filter(Boolean).length;
    
    if (characterTypes < 2) {
      setError('Password must contain at least 2 different character types (lowercase, uppercase, numbers, special characters)');
      return false;
    }
    
    // Check for weak patterns
    const weakPatterns = [
      /password/i,
      /12345/,
      /qwerty/i,
      /abc/i,
      /(.)\1{3,}/, // repeated characters
      /(012|123|234|345|456|567|678|789|890)/ // sequential numbers
    ];
    
    for (const pattern of weakPatterns) {
      if (pattern.test(formData.password)) {
        setError('Password is too weak. Avoid common patterns like "password12345", repeated characters, or sequential numbers');
        return false;
      }
    }
    
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return false;
    }
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
      const response = await authService.register(
        formData.username,
        formData.email,
        formData.password,
        formData.role
      );
      console.log('Registration successful:', response);
      setSuccess(true);
      // Don't call onRegisterSuccess immediately, let user see the success message
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <div className="register-header">
          <h2>📝 Register</h2>
          <p>Create your account to get started</p>
        </div>

        {success ? (
          <div className="success-container">
            <div className="success-message">
              <div className="success-icon">✅</div>
              <h3>Registration Successful!</h3>
              <p>Your account has been created successfully.</p>
              <p className="login-prompt">
                Please login with your username and password to continue.
              </p>
              <button
                className="login-redirect-button"
                onClick={onSwitchToLogin}
              >
                🔐 Go to Login Page
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="register-form">
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
              placeholder="Choose a username"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="role">Role</label>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              disabled={loading}
              className="role-select"
            >
              <option value="CANDIDATE">🎓 Candidate</option>
              <option value="HR">💼 HR</option>
              <option value="PANELIST">👨‍💼 Panelist</option>
            </select>
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
                placeholder="Create a password"
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
            
            {/* Password Strength Meter */}
            {formData.password && (
              <div className="password-strength-container">
                <div className="password-strength-bar">
                  <div
                    className="password-strength-fill"
                    style={{
                      width: `${(passwordStrength.score / 5) * 100}%`,
                      backgroundColor: passwordStrength.color
                    }}
                  ></div>
                </div>
                <div className="password-strength-label" style={{ color: passwordStrength.color }}>
                  {passwordStrength.label}
                </div>
              </div>
            )}
            
            {/* Password Suggestions */}
            {passwordSuggestions.length > 0 && (
              <div className="password-suggestions">
                {passwordSuggestions.map((suggestion, index) => (
                  <div key={index} className="password-suggestion">
                    {suggestion}
                  </div>
                ))}
              </div>
            )}
            
            <small className="password-hint">
              Password must be at least 15 characters with 2+ character types (lowercase, uppercase, numbers, special characters)
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <div className="password-input-wrapper">
              <input
                type={showConfirmPassword ? "text" : "password"}
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm your password"
                disabled={loading}
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                disabled={loading}
                aria-label={showConfirmPassword ? "Hide password" : "Show password"}
              >
                {showConfirmPassword ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

            <button
              type="submit"
              className="register-button"
              disabled={loading}
            >
              {loading ? '⏳ Registering...' : '✅ Register'}
            </button>
          </form>
        )}

        {!success && (
          <div className="register-footer">
            <p>
              Already have an account?{' '}
              <span className="link" onClick={onSwitchToLogin}>
                Login here
              </span>
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Register;

// Made with Bob
