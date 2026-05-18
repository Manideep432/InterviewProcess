import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import CandidateInfo from './CandidateInfo';
import PanelistDashboard from './PanelistDashboard';
import HRDashboard from './HRDashboard';
import './Dashboard.css';

/**
 * Dashboard Component - Displayed after successful login
 * Shows different content based on user role (HR, PANELIST, CANDIDATE)
 */
const Dashboard = ({ user, onLogout }) => {
  const userRole = user.role || 'CANDIDATE';

  // Declare all hooks at the top level (before any conditional returns)
  const [activeTab, setActiveTab] = useState('info');

  // If user is CANDIDATE, show the new CandidateInfo component
  if (userRole === 'CANDIDATE') {
    return <CandidateInfo user={user} onLogout={onLogout} />;
  }

  // If user is PANELIST, show the new PanelistDashboard component
  if (userRole === 'PANELIST') {
    return <PanelistDashboard user={user} onLogout={onLogout} />;
  }

  // If user is HR, show the new HRDashboard component
  if (userRole === 'HR') {
    return <HRDashboard user={user} onLogout={onLogout} />;
  }

  // For other roles, continue with the existing dashboard

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  const getRoleIcon = () => {
    switch (userRole) {
      case 'HR':
        return '💼';
      case 'PANELIST':
        return '👨‍💼';
      case 'CANDIDATE':
        return '🎓';
      default:
        return '👤';
    }
  };

  const getRoleDescription = () => {
    switch (userRole) {
      case 'HR':
        return 'You can manage panelists and candidates';
      case 'PANELIST':
        return 'You can view assigned candidates and HR information';
      case 'CANDIDATE':
        return 'You can view your application status';
      default:
        return 'Welcome to your dashboard';
    }
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-card">
        <div className="dashboard-header">
          <h2>{getRoleIcon()} Welcome to Dashboard!</h2>
          <p>You have successfully logged in as <strong>{userRole}</strong></p>
          <p className="role-description">{getRoleDescription()}</p>
        </div>

        <div className="dashboard-tabs">
          <button
            className={`tab-button ${activeTab === 'info' ? 'active' : ''}`}
            onClick={() => setActiveTab('info')}
          >
            👤 User Info
          </button>
          
          {userRole === 'HR' && (
            <>
              <button
                className={`tab-button ${activeTab === 'panelists' ? 'active' : ''}`}
                onClick={() => setActiveTab('panelists')}
              >
                👨‍💼 Panelists
              </button>
              <button
                className={`tab-button ${activeTab === 'candidates' ? 'active' : ''}`}
                onClick={() => setActiveTab('candidates')}
              >
                🎓 Candidates
              </button>
            </>
          )}
          
          {userRole === 'PANELIST' && (
            <>
              <button
                className={`tab-button ${activeTab === 'assigned' ? 'active' : ''}`}
                onClick={() => setActiveTab('assigned')}
              >
                📋 Assigned Candidates
              </button>
              <button
                className={`tab-button ${activeTab === 'hrinfo' ? 'active' : ''}`}
                onClick={() => setActiveTab('hrinfo')}
              >
                💼 HR Information
              </button>
            </>
          )}
          
          <button
            className={`tab-button ${activeTab === 'feedback' ? 'active' : ''}`}
            onClick={() => setActiveTab('feedback')}
          >
            💬 Feedback
          </button>
        </div>

        {activeTab === 'info' && (
          <>
            <div className="user-info">
              <div className="info-item">
                <span className="info-label">👤 Username:</span>
                <span className="info-value">{user.username}</span>
              </div>
              <div className="info-item">
                <span className="info-label">📧 Email:</span>
                <span className="info-value">{user.email}</span>
              </div>
              <div className="info-item">
                <span className="info-label">👔 Role:</span>
                <span className="info-value">{userRole}</span>
              </div>
              <div className="info-item">
                <span className="info-label">🆔 User ID:</span>
                <span className="info-value">{user.id}</span>
              </div>
            </div>

            <div className="dashboard-actions">
              <button className="logout-button" onClick={handleLogout}>
                🚪 Logout
              </button>
            </div>

            <div className="dashboard-footer">
              <p>✅ Authentication successful with JWT token</p>
              <p className="tech-stack">
                <strong>Tech Stack:</strong> React + Spring Boot + JWT
              </p>
            </div>
          </>
        )}

        {activeTab === 'panelists' && userRole === 'HR' && (
          <div className="content-section">
            <h3>👨‍💼 Manage Panelists</h3>
            <p>View and manage panelists assigned to you.</p>
            <div className="placeholder-content">
              <p>📋 Panelist management interface will be displayed here.</p>
              <p>Features:</p>
              <ul>
                <li>View all assigned panelists</li>
                <li>Add new panelists</li>
                <li>Update panelist information</li>
                <li>Activate/Deactivate panelists</li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'candidates' && userRole === 'HR' && (
          <div className="content-section">
            <h3>🎓 Manage Candidates</h3>
            <p>View and manage candidates in your pipeline.</p>
            <div className="placeholder-content">
              <p>📋 Candidate management interface will be displayed here.</p>
              <p>Features:</p>
              <ul>
                <li>View all candidates</li>
                <li>Add new candidates</li>
                <li>Assign panelists to candidates</li>
                <li>Update candidate status</li>
                <li>Filter by status (Applied, Screening, Interview, etc.)</li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'assigned' && userRole === 'PANELIST' && (
          <div className="content-section">
            <h3>📋 Assigned Candidates</h3>
            <p>View candidates assigned to you for evaluation.</p>
            <div className="placeholder-content">
              <p>📋 Assigned candidates will be displayed here.</p>
              <p>You can:</p>
              <ul>
                <li>View candidate details</li>
                <li>Update candidate status</li>
                <li>Add evaluation notes</li>
                <li>Schedule interviews</li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'hrinfo' && userRole === 'PANELIST' && (
          <div className="content-section">
            <h3>💼 HR Information</h3>
            <p>Information about the HR who assigned you.</p>
            <div className="placeholder-content">
              <p>📋 HR details will be displayed here.</p>
              <p>Information includes:</p>
              <ul>
                <li>HR Name</li>
                <li>Contact Information</li>
                <li>Assignment Date</li>
                <li>Your Specialization</li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'feedback' && (
          <div className="feedback-section">
            <h3>📝 Feedback</h3>
            <p>Share your thoughts and suggestions with us.</p>
            <textarea
              className="feedback-textarea"
              placeholder="Enter your feedback here..."
              rows="6"
            ></textarea>
            <button className="submit-button">Submit Feedback</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;

// Made with Bob
