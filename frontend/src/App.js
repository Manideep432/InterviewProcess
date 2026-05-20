import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import ForgotPassword from './components/ForgotPassword';
import Dashboard from './components/Dashboard';
import ErrorBoundary from './components/ErrorBoundary';
import authService from './services/authService';
import { checkBackendHealth } from './utils/apiHelper';
import './App.css';

/**
 * Main App Component
 */
function App() {
  const [currentView, setCurrentView] = useState('login');
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [backendStatus, setBackendStatus] = useState('checking'); // checking, online, offline
  const [showBackendError, setShowBackendError] = useState(false);

  useEffect(() => {
    // Check backend health on mount
    checkBackend();
    
    // Check if user is already logged in
    const currentUser = authService.getCurrentUser();
    if (currentUser) {
      setUser(currentUser);
      setIsAuthenticated(true);
    }
  }, []);

  const checkBackend = async () => {
    setBackendStatus('checking');
    const isHealthy = await checkBackendHealth();
    
    if (isHealthy) {
      setBackendStatus('online');
      setShowBackendError(false);
    } else {
      setBackendStatus('offline');
      setShowBackendError(true);
      
      // Auto-retry after 5 seconds
      setTimeout(() => {
        checkBackend();
      }, 5000);
    }
  };

  const handleRetryBackend = () => {
    setShowBackendError(false);
    checkBackend();
  };

  const handleLoginSuccess = (response) => {
    setUser({
      id: response.id,
      username: response.username,
      email: response.email,
      role: response.role
    });
    setIsAuthenticated(true);
  };

  const handleRegisterSuccess = (response) => {
    // Don't auto-login, just switch to login view
    setCurrentView('login');
  };

  const handleLogout = () => {
    setUser(null);
    setIsAuthenticated(false);
    setCurrentView('login');
  };

  const switchToRegister = () => {
    setCurrentView('register');
  };

  const switchToLogin = () => {
    setCurrentView('login');
  };

  const switchToForgotPassword = () => {
    setCurrentView('forgotPassword');
  };

  if (isAuthenticated && user) {
    return (
      <ErrorBoundary>
        <Dashboard user={user} onLogout={handleLogout} />
      </ErrorBoundary>
    );
  }

  return (
    <ErrorBoundary>
      <div className="App">
        {/* Backend Status Banner */}
        {showBackendError && backendStatus === 'offline' && (
          <div className="backend-error-banner">
            <div className="backend-error-content">
              <span className="error-icon">⚠️</span>
              <span className="error-text">
                Backend server is not responding. Please ensure it's running on http://localhost:8081
              </span>
              <button className="retry-backend-btn" onClick={handleRetryBackend}>
                🔄 Retry
              </button>
            </div>
          </div>
        )}

        {currentView === 'login' ? (
          <Login
            onLoginSuccess={handleLoginSuccess}
            onSwitchToRegister={switchToRegister}
            onSwitchToForgotPassword={switchToForgotPassword}
          />
        ) : currentView === 'register' ? (
          <Register
            onRegisterSuccess={handleRegisterSuccess}
            onSwitchToLogin={switchToLogin}
          />
        ) : (
          <ForgotPassword
            onSwitchToLogin={switchToLogin}
          />
        )}
      </div>
    </ErrorBoundary>
  );
}

export default App;

// Made with Bob
