import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import ForgotPassword from './components/ForgotPassword';
import Dashboard from './components/Dashboard';
import authService from './services/authService';
import './App.css';

/**
 * Main App Component
 */
function App() {
  const [currentView, setCurrentView] = useState('login');
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check if user is already logged in
    const currentUser = authService.getCurrentUser();
    if (currentUser) {
      setUser(currentUser);
      setIsAuthenticated(true);
    }
  }, []);

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
    return <Dashboard user={user} onLogout={handleLogout} />;
  }

  return (
    <div className="App">
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
  );
}

export default App;

// Made with Bob
