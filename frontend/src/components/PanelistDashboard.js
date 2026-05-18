import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import PanelistProfile from './PanelistProfile';
import TechnicalAssessmentForm from './TechnicalAssessmentForm';
import './PanelistDashboard.css';

// Get API URL from environment variable or use default
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

/**
 * PanelistDashboard Component - Dashboard for Panelist role
 * Tabs: HOME, Panelist Profile, New Interview, Number of Interviews
 */
const PanelistDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('home');
  const [showFullProfile, setShowFullProfile] = useState(false);
  const [panelistData, setPanelistData] = useState(null);
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [newInterview, setNewInterview] = useState({
    candidateName: '',
    candidateEmail: '',
    interviewDate: '',
    interviewTimeFrom: '',
    interviewTimeTo: '',
    position: '',
    notes: ''
  });
  const [feedbackModal, setFeedbackModal] = useState(null);
  const [feedbackText, setFeedbackText] = useState('');
  const [feedbackDecision, setFeedbackDecision] = useState('SELECTED');
  const [submittingFeedback, setSubmittingFeedback] = useState(false);
  const [showAssessmentForm, setShowAssessmentForm] = useState(false);
  const [selectedInterviewForAssessment, setSelectedInterviewForAssessment] = useState(null);

  useEffect(() => {
    loadPanelistData();
    loadInterviews();
  }, []);

  const loadPanelistData = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/api/panelists/user/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${authService.getToken()}`
        }
      });
      
      const data = await response.json();
      if (data.success) {
        setPanelistData(data.panelist);
      }
    } catch (err) {
      console.error('Error loading panelist data:', err);
      // Error message removed as per user request
    } finally {
      setLoading(false);
    }
  };

  const loadInterviews = async () => {
    try {
      const response = await fetch(`${API_URL}/api/interviews/panelist/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${authService.getToken()}`
        }
      });
      
      const data = await response.json();
      if (data.success) {
        setInterviews(data.interviews || []);
      }
    } catch (err) {
      console.error('Error loading interviews:', err);
    }
  };

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  const handleNewInterviewChange = (e) => {
    setNewInterview({
      ...newInterview,
      [e.target.name]: e.target.value
    });
  };

  const handleScheduleInterview = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch(`${API_URL}/api/interviews/schedule`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authService.getToken()}`
        },
        body: JSON.stringify({
          ...newInterview,
          panelistId: user.id
        })
      });

      const data = await response.json();
      if (data.success) {
        alert('Interview scheduled successfully!');
        setNewInterview({
          candidateName: '',
          candidateEmail: '',
          interviewDate: '',
          interviewTimeFrom: '',
          interviewTimeTo: '',
          position: '',
          notes: ''
        });
        loadInterviews();
        setActiveTab('interviews');
      } else {
        setError(data.message || 'Failed to schedule interview');
      }
    } catch (err) {
      setError('Error scheduling interview: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteInterview = async (interviewId) => {
    if (!window.confirm('Mark this interview as completed?')) {
      return;
    }

    try {
      const response = await fetch(`${API_URL}/api/interviews/${interviewId}/complete`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${authService.getToken()}`,
          'Content-Type': 'application/json'
        }
      });

      const data = await response.json();
      if (data.success) {
        alert('Interview marked as completed!');
        loadInterviews();
      } else {
        setError(data.message || 'Failed to complete interview');
      }
    } catch (err) {
      setError('Error completing interview: ' + err.message);
    }
  };

  const handleOpenFeedbackModal = (interview) => {
    setFeedbackModal(interview);
    setFeedbackText(interview.feedback || '');
    setFeedbackDecision(interview.candidateStatus === 'REJECTED' ? 'REJECTED' : 'SELECTED');
  };

  const handleCloseFeedbackModal = () => {
    setFeedbackModal(null);
    setFeedbackText('');
    setFeedbackDecision('SELECTED');
  };

  const handleSubmitFeedback = async (e) => {
    e.preventDefault();
    setSubmittingFeedback(true);

    try {
      const response = await fetch(`${API_URL}/api/interviews/${feedbackModal.id}/feedback`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${authService.getToken()}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          feedback: feedbackText,
          decision: feedbackDecision
        })
      });

      const data = await response.json();
      if (data.success) {
        alert(`Feedback submitted successfully! Candidate marked as ${feedbackDecision}.`);
        loadInterviews();
        handleCloseFeedbackModal();
      } else {
        setError(data.message || 'Failed to submit feedback');
      }
    } catch (err) {
      setError('Error submitting feedback: ' + err.message);
    } finally {
      setSubmittingFeedback(false);
    }
  };

  const handleOpenAssessmentForm = (interview) => {
    setSelectedInterviewForAssessment(interview);
    setShowAssessmentForm(true);
  };

  const handleCloseAssessmentForm = () => {
    setShowAssessmentForm(false);
    setSelectedInterviewForAssessment(null);
  };

  const handleAssessmentSubmitSuccess = () => {
    loadInterviews();
  };

  const isInterviewCompleted = (interview) => {
    if (interview.status === 'COMPLETED') return true;
    
    const now = new Date();
    const interviewDate = new Date(interview.interviewDate);
    const [hours, minutes] = interview.interviewTimeTo.split(':');
    interviewDate.setHours(parseInt(hours), parseInt(minutes), 0, 0);
    
    return now > interviewDate;
  };

  const getInterviewStats = () => {
    const total = interviews.length;
    const upcoming = interviews.filter(i => i.status === 'SCHEDULED').length;
    const completed = interviews.filter(i => i.status === 'COMPLETED').length;
    const cancelled = interviews.filter(i => i.status === 'CANCELLED').length;
    return { total, upcoming, completed, cancelled };
  };

  const stats = getInterviewStats();

  // Show full profile view if requested
  if (showFullProfile) {
    return <PanelistProfile user={user} onBack={() => setShowFullProfile(false)} />;
  }

  return (
    <div className="panelist-dashboard-container">
      <div className="panelist-dashboard-card">
        <div className="panelist-dashboard-header">
          <div className="header-content">
            <div>
              <h2>👨‍💼 Panelist Dashboard</h2>
              <p className="role-badge">Role: {user.role}</p>
            </div>
            <div>
              <p style={{ margin: '0 0 10px 0', color: '#666', fontSize: '16px' }}>
                Welcome, <strong>{user.username}</strong>
              </p>
              <button className="header-logout-button" onClick={handleLogout}>
                🚪 Logout
              </button>
            </div>
          </div>
        </div>

        <div className="panelist-tabs">
          <button
            className={`panelist-tab-button ${activeTab === 'home' ? 'active' : ''}`}
            onClick={() => setActiveTab('home')}
          >
            🏠 HOME
          </button>
          <button
            className="panelist-tab-button"
            onClick={() => setShowFullProfile(true)}
          >
            👤 View Full Profile
          </button>
          <button
            className={`panelist-tab-button ${activeTab === 'newInterview' ? 'active' : ''}`}
            onClick={() => setActiveTab('newInterview')}
          >
            ➕ New Interview
          </button>
          <button
            className={`panelist-tab-button ${activeTab === 'interviews' ? 'active' : ''}`}
            onClick={() => setActiveTab('interviews')}
          >
            📊 Number of Interviews ({stats.total})
          </button>
        </div>

        {/* Error Display */}
        {error && (
          <div className="error-banner" style={{
            backgroundColor: '#fee',
            color: '#c33',
            padding: '15px',
            margin: '10px 0',
            borderRadius: '8px',
            border: '1px solid #fcc',
            textAlign: 'center',
            fontWeight: 'bold'
          }}>
            ❌ {error}
          </div>
        )}

        {/* HOME Tab */}
        {activeTab === 'home' && (
          <div className="tab-content">
            <h3>📈 Dashboard Overview</h3>
            <div className="stats-grid">
              <div className="stat-card total">
                <div className="stat-icon">📊</div>
                <div className="stat-info">
                  <h4>Total Interviews</h4>
                  <p className="stat-number">{stats.total}</p>
                </div>
              </div>
              <div className="stat-card upcoming">
                <div className="stat-icon">📅</div>
                <div className="stat-info">
                  <h4>Upcoming</h4>
                  <p className="stat-number">{stats.upcoming}</p>
                </div>
              </div>
              <div className="stat-card completed">
                <div className="stat-icon">✅</div>
                <div className="stat-info">
                  <h4>Completed</h4>
                  <p className="stat-number">{stats.completed}</p>
                </div>
              </div>
              <div className="stat-card cancelled">
                <div className="stat-icon">❌</div>
                <div className="stat-info">
                  <h4>Cancelled</h4>
                  <p className="stat-number">{stats.cancelled}</p>
                </div>
              </div>
            </div>

            <div className="quick-actions">
              <h4>⚡ Quick Actions</h4>
              <button
                className="action-button schedule"
                onClick={() => setActiveTab('newInterview')}
              >
                ➕ Schedule New Interview
              </button>
              <button
                className="action-button view"
                onClick={() => setActiveTab('interviews')}
              >
                📋 View All Interviews
              </button>
            </div>
          </div>
        )}

        {/* New Interview Tab */}
        {activeTab === 'newInterview' && (
          <div className="tab-content">
            <h3>➕ Schedule New Interview</h3>
            {error && <div className="error-message">❌ {error}</div>}
            <form onSubmit={handleScheduleInterview} className="interview-form">
              <div className="form-group">
                <label>Candidate Name *</label>
                <input
                  type="text"
                  name="candidateName"
                  value={newInterview.candidateName}
                  onChange={handleNewInterviewChange}
                  required
                  placeholder="Enter candidate name"
                />
              </div>
              <div className="form-group">
                <label>Candidate Email *</label>
                <input
                  type="email"
                  name="candidateEmail"
                  value={newInterview.candidateEmail}
                  onChange={handleNewInterviewChange}
                  required
                  placeholder="candidate@example.com"
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Interview Date *</label>
                  <input
                    type="date"
                    name="interviewDate"
                    value={newInterview.interviewDate}
                    onChange={handleNewInterviewChange}
                    required
                    min={new Date().toISOString().split('T')[0]}
                  />
                </div>
                <div className="form-group">
                  <label>Interview Time From *</label>
                  <input
                    type="time"
                    name="interviewTimeFrom"
                    value={newInterview.interviewTimeFrom}
                    onChange={handleNewInterviewChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Interview Time To *</label>
                  <input
                    type="time"
                    name="interviewTimeTo"
                    value={newInterview.interviewTimeTo}
                    onChange={handleNewInterviewChange}
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <label>Position *</label>
                <input
                  type="text"
                  name="position"
                  value={newInterview.position}
                  onChange={handleNewInterviewChange}
                  required
                  placeholder="e.g., Senior Software Engineer"
                />
              </div>
              <div className="form-group">
                <label>Notes</label>
                <textarea
                  name="notes"
                  value={newInterview.notes}
                  onChange={handleNewInterviewChange}
                  placeholder="Additional notes about the interview..."
                  rows="4"
                />
              </div>
              <button type="submit" className="submit-button" disabled={loading}>
                {loading ? '⏳ Scheduling...' : '📅 Schedule Interview'}
              </button>
            </form>
          </div>
        )}

        {/* Number of Interviews Tab */}
        {activeTab === 'interviews' && (
          <div className="tab-content">
            <h3>📊 Interview List ({stats.total} Total)</h3>
            <div className="interview-filters">
              <span className="filter-label">Filter:</span>
              <button className="filter-btn all">All ({stats.total})</button>
              <button className="filter-btn upcoming">Upcoming ({stats.upcoming})</button>
              <button className="filter-btn completed">Completed ({stats.completed})</button>
              <button className="filter-btn cancelled">Cancelled ({stats.cancelled})</button>
            </div>
            {interviews.length === 0 ? (
              <div className="empty-state">
                <p>📭 No interviews scheduled yet</p>
                <button 
                  className="action-button schedule"
                  onClick={() => setActiveTab('newInterview')}
                >
                  ➕ Schedule Your First Interview
                </button>
              </div>
            ) : (
              <div className="interviews-list">
                {interviews.map((interview) => {
                  const isCompleted = isInterviewCompleted(interview);
                  return (
                  <div key={interview.id} className={`interview-card ${interview.status.toLowerCase()}`}>
                    <div className="interview-header">
                      <h4>{interview.candidateName}</h4>
                      <span className={`status-badge ${interview.status.toLowerCase()}`}>
                        {interview.status}
                      </span>
                    </div>
                    <div className="interview-details">
                      <p><strong>📧 Email:</strong> {interview.candidateEmail}</p>
                      <p><strong>💼 Position:</strong> {interview.position}</p>
                      <p><strong>📅 Date:</strong> {interview.interviewDate}</p>
                      <p><strong>⏰ Time:</strong> {interview.interviewTimeFrom} - {interview.interviewTimeTo}</p>
                      {interview.notes && <p><strong>📝 Notes:</strong> {interview.notes}</p>}
                      {interview.feedback && (
                        <p><strong>💬 Feedback:</strong> {interview.feedback}</p>
                      )}
                    </div>
                    <div className="interview-actions" style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                      {isCompleted && interview.status !== 'COMPLETED' && (
                        <button
                          onClick={() => handleCompleteInterview(interview.id)}
                          style={{
                            padding: '8px 16px',
                            backgroundColor: '#28a745',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '0.9em'
                          }}
                        >
                          ✅ Mark Complete
                        </button>
                      )}
                      {(interview.status === 'COMPLETED' || isCompleted) && (
                        <>
                          <button
                            onClick={() => handleOpenFeedbackModal(interview)}
                            style={{
                              padding: '8px 16px',
                              backgroundColor: '#007bff',
                              color: 'white',
                              border: 'none',
                              borderRadius: '4px',
                              cursor: 'pointer',
                              fontSize: '0.9em'
                            }}
                          >
                            💬 {interview.feedback ? 'Edit Feedback' : 'Add Feedback'}
                          </button>
                          <button
                            onClick={() => handleOpenAssessmentForm(interview)}
                            style={{
                              padding: '8px 16px',
                              backgroundColor: interview.hasTechnicalFeedback ? '#6c757d' : '#28a745',
                              color: 'white',
                              border: 'none',
                              borderRadius: '4px',
                              cursor: 'pointer',
                              fontSize: '0.9em'
                            }}
                            disabled={interview.hasTechnicalFeedback}
                            title={interview.hasTechnicalFeedback ? 'Technical Feedback already submitted' : 'Submit Technical Feedback Form'}
                          >
                            📋 {interview.hasTechnicalFeedback ? 'Feedback Submitted' : 'Technical Feedback'}
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                )})}
              </div>
            )}
          </div>
        )}

        {/* Feedback Modal */}
        {feedbackModal && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000
          }}>
            <div style={{
              backgroundColor: 'white',
              padding: '30px',
              borderRadius: '8px',
              maxWidth: '600px',
              width: '90%',
              maxHeight: '80vh',
              overflow: 'auto'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h3 style={{ margin: 0 }}>💬 Interview Feedback</h3>
                <button
                  onClick={handleCloseFeedbackModal}
                  style={{
                    background: 'none',
                    border: 'none',
                    fontSize: '24px',
                    cursor: 'pointer',
                    color: '#666'
                  }}
                >
                  ✕
                </button>
              </div>
              <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
                <p style={{ margin: '5px 0' }}><strong>Candidate:</strong> {feedbackModal.candidateName}</p>
                <p style={{ margin: '5px 0' }}><strong>Position:</strong> {feedbackModal.position}</p>
                <p style={{ margin: '5px 0' }}><strong>Date:</strong> {feedbackModal.interviewDate}</p>
                <p style={{ margin: '5px 0' }}><strong>Time:</strong> {feedbackModal.interviewTimeFrom} - {feedbackModal.interviewTimeTo}</p>
              </div>
              <form onSubmit={handleSubmitFeedback}>
                <div style={{ marginBottom: '20px' }}>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                    Interview Decision *
                  </label>
                  <select
                    value={feedbackDecision}
                    onChange={(e) => setFeedbackDecision(e.target.value)}
                    required
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #ddd',
                      borderRadius: '4px',
                      fontSize: '14px',
                      backgroundColor: '#fff'
                    }}
                  >
                    <option value="SELECTED">Selected</option>
                    <option value="REJECTED">Rejected</option>
                  </select>
                </div>
                <div style={{ marginBottom: '20px' }}>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                    Feedback *
                  </label>
                  <textarea
                    value={feedbackText}
                    onChange={(e) => setFeedbackText(e.target.value)}
                    required
                    rows="8"
                    placeholder="Enter your feedback about the candidate's performance, skills, and overall impression..."
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #ddd',
                      borderRadius: '4px',
                      fontSize: '14px',
                      fontFamily: 'inherit',
                      resize: 'vertical'
                    }}
                  />
                </div>
                <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                  <button
                    type="button"
                    onClick={handleCloseFeedbackModal}
                    style={{
                      padding: '10px 20px',
                      backgroundColor: '#6c757d',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '14px'
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={submittingFeedback}
                    style={{
                      padding: '10px 20px',
                      backgroundColor: '#007bff',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: submittingFeedback ? 'not-allowed' : 'pointer',
                      fontSize: '14px',
                      opacity: submittingFeedback ? 0.6 : 1
                    }}
                  >
                    {submittingFeedback ? '⏳ Submitting...' : '✅ Submit Feedback'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Technical Assessment Form Modal */}
        {showAssessmentForm && selectedInterviewForAssessment && (
          <TechnicalAssessmentForm
            interview={selectedInterviewForAssessment}
            onClose={handleCloseAssessmentForm}
            onSubmitSuccess={handleAssessmentSubmitSuccess}
          />
        )}
      </div>
    </div>
  );
};

export default PanelistDashboard;

// Made with Bob