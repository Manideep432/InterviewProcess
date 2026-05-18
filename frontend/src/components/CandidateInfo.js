import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import './CandidateInfo.css';

const CandidateInfo = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('home');
  const [interviews, setInterviews] = useState([]);
  const [loadingInterviews, setLoadingInterviews] = useState(false);
  const [candidateName, setCandidateName] = useState('');
  const [feedbackList, setFeedbackList] = useState([]);
  const [loadingFeedback, setLoadingFeedback] = useState(false);
  const [candidateId, setCandidateId] = useState(null);
  const [formData, setFormData] = useState({
    candidateName: '',
    mailId: '',
    phoneNumber: '',
    location: '',
    currentCtc: '',
    hrMailId: '',
    position: '',
    experienceYears: '',
    skills: '',
    photo: null,
    cv: null,
    gvtId: null
  });
  const [photoPreview, setPhotoPreview] = useState(null);
  const [dragActive, setDragActive] = useState({});
  const [saveStatus, setSaveStatus] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e, fieldName) => {
    const file = e.target.files[0];
    if (file) {
      setFormData(prev => ({
        ...prev,
        [fieldName]: file
      }));

      // Create preview for photo
      if (fieldName === 'photo' && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onloadend = () => {
          setPhotoPreview(reader.result);
        };
        reader.readAsDataURL(file);
      }
    }
  };

  const handleDrag = (e, fieldName) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(prev => ({ ...prev, [fieldName]: true }));
    } else if (e.type === "dragleave") {
      setDragActive(prev => ({ ...prev, [fieldName]: false }));
    }
  };

  const handleDrop = (e, fieldName) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(prev => ({ ...prev, [fieldName]: false }));

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      setFormData(prev => ({
        ...prev,
        [fieldName]: file
      }));

      // Create preview for photo
      if (fieldName === 'photo' && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onloadend = () => {
          setPhotoPreview(reader.result);
        };
        reader.readAsDataURL(file);
      }
    }
  };

  const handleSave = async () => {
    // Validate required fields
    if (!formData.candidateName || !formData.mailId || !formData.phoneNumber || !formData.location) {
      alert('Please fill in all required fields: Name, Email, Phone, and Location');
      return;
    }

    // Validate skills word count (max 200 words)
    if (formData.skills) {
      const wordCount = formData.skills.trim().split(/\s+/).length;
      if (wordCount > 200) {
        alert(`Skills section exceeds 200 words limit. Current: ${wordCount} words`);
        return;
      }
    }

    setSaveStatus('saving');
    
    try {
      const formDataToSend = new FormData();
      formDataToSend.append('candidateName', formData.candidateName);
      formDataToSend.append('mailId', formData.mailId);
      formDataToSend.append('phoneNumber', formData.phoneNumber);
      formDataToSend.append('location', formData.location);
      
      // Only append if values exist
      if (formData.currentCtc) {
        formDataToSend.append('currentCtc', formData.currentCtc);
      }
      if (formData.hrMailId) {
        formDataToSend.append('hrMailId', formData.hrMailId);
      }
      if (formData.position) {
        formDataToSend.append('position', formData.position);
      }
      if (formData.experienceYears) {
        formDataToSend.append('experienceYears', formData.experienceYears);
      }
      if (formData.skills) {
        formDataToSend.append('skills', formData.skills);
      }
      
      if (formData.photo) formDataToSend.append('photo', formData.photo);
      if (formData.cv) formDataToSend.append('cv', formData.cv);
      if (formData.gvtId) formDataToSend.append('gvtId', formData.gvtId);

      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8081/api/candidates/save-info', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formDataToSend
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Success:', result);
        setSaveStatus('success');
        // Clear form after successful save
        setTimeout(() => {
          setSaveStatus('');
        }, 5000);
      } else {
        const errorData = await response.json();
        setSaveStatus('error');
        console.error('Error Response:', errorData);
        alert('Error: ' + (errorData.message || 'Failed to save candidate information'));
        setTimeout(() => setSaveStatus(''), 3000);
      }
    } catch (error) {
      console.error('Error saving candidate info:', error);
      setSaveStatus('error');
      alert('Network error: ' + error.message);
      setTimeout(() => setSaveStatus(''), 3000);
    }
  };

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  // Fetch candidate name and ID on component mount
  useEffect(() => {
    const fetchCandidateName = async () => {
      try {
        const token = localStorage.getItem('token');
        const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8081';
        const url = `${apiUrl}/api/candidates/by-email/${encodeURIComponent(user.email)}`;
        
        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.ok) {
          const data = await response.json();
          if (data.candidate && data.candidate.name) {
            setCandidateName(data.candidate.name);
            setCandidateId(data.candidate.id);
          }
        }
      } catch (error) {
        console.error('Error fetching candidate name:', error);
      }
    };

    fetchCandidateName();
  }, [user.email]);

  // Fetch interviews when "My Interviews" tab is active
  useEffect(() => {
    if (activeTab === 'myInterviews') {
      fetchInterviews();
    }
  }, [activeTab]);

  // Fetch feedback when "feedback" tab is active
  useEffect(() => {
    if (activeTab === 'feedback' && candidateId) {
      fetchFeedback();
    }
  }, [activeTab, candidateId]);

  const fetchInterviews = async () => {
    setLoadingInterviews(true);
    try {
      const token = localStorage.getItem('token');
      const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8081';
      const url = `${apiUrl}/api/interviews/candidate/email/${encodeURIComponent(user.email)}/details`;
      
      console.log('Fetching interviews from:', url);
      console.log('User email:', user.email);
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log('Response status:', response.status);
      
      if (response.ok) {
        const data = await response.json();
        console.log('Interviews data:', data);
        setInterviews(data.interviews || []);
      } else {
        const errorData = await response.json().catch(() => ({}));
        console.error('Failed to fetch interviews:', response.status, errorData);
        setInterviews([]);
      }
    } catch (error) {
      console.error('Error fetching interviews:', error);
      setInterviews([]);
    } finally {
      setLoadingInterviews(false);
    }
  };

  const fetchFeedback = async () => {
    setLoadingFeedback(true);
    try {
      const token = localStorage.getItem('token');
      const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8081';
      const url = `${apiUrl}/api/interview-feedback/candidate/${candidateId}`;
      
      console.log('Fetching feedback from:', url);
      console.log('Candidate ID:', candidateId);
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log('Feedback response status:', response.status);
      
      if (response.ok) {
        const data = await response.json();
        console.log('Feedback data:', data);
        setFeedbackList(data.feedbackList || []);
      } else {
        const errorData = await response.json().catch(() => ({}));
        console.error('Failed to fetch feedback:', response.status, errorData);
        setFeedbackList([]);
      }
    } catch (error) {
      console.error('Error fetching feedback:', error);
      setFeedbackList([]);
    } finally {
      setLoadingFeedback(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatTime = (timeString) => {
    if (!timeString) return 'N/A';
    return timeString;
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case 'SCHEDULED':
        return 'status-scheduled';
      case 'COMPLETED':
        return 'status-completed';
      case 'CANCELLED':
        return 'status-cancelled';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'RESCHEDULED':
        return 'status-rescheduled';
      default:
        return 'status-default';
    }
  };

  return (
    <div className="candidate-dashboard">
      {/* Header/Navbar */}
      <header className="candidate-header">
        <div className="header-left">
          <span className="role-badge">Role: CANDIDATE</span>
          {candidateName && (
            <span className="candidate-name-badge">👤 {candidateName}</span>
          )}
        </div>
        <nav className="header-nav">
          <button 
            className={`nav-link ${activeTab === 'home' ? 'active' : ''}`}
            onClick={() => setActiveTab('home')}
          >
            Home
          </button>
          <button 
            className={`nav-link ${activeTab === 'candidateInfo' ? 'active' : ''}`}
            onClick={() => setActiveTab('candidateInfo')}
          >
            Candidate Info
          </button>
          <button
            className={`nav-link ${activeTab === 'myInterviews' ? 'active' : ''}`}
            onClick={() => setActiveTab('myInterviews')}
          >
            My Interviews
          </button>
          <button
            className={`nav-link ${activeTab === 'feedback' ? 'active' : ''}`}
            onClick={() => setActiveTab('feedback')}
          >
            Feed Back
          </button>
        </nav>
        <div className="header-right">
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="candidate-main">
        {activeTab === 'home' && (
          <div className="home-content">
            <h1>Welcome to Candidate Dashboard</h1>
            <p>Please navigate to "Candidate Info" to fill your details.</p>
          </div>
        )}

        {activeTab === 'candidateInfo' && (
          <div className="info-card">
            <h2 className="card-title">Candidate Info</h2>
            
            <div className="form-grid">
              {/* Photo Upload */}
              <div className="form-group full-width">
                <label className="form-label">Candidate Photo (JPG/PNG)</label>
                <div 
                  className={`upload-box ${dragActive.photo ? 'drag-active' : ''}`}
                  onDragEnter={(e) => handleDrag(e, 'photo')}
                  onDragLeave={(e) => handleDrag(e, 'photo')}
                  onDragOver={(e) => handleDrag(e, 'photo')}
                  onDrop={(e) => handleDrop(e, 'photo')}
                >
                  {photoPreview ? (
                    <div className="photo-preview">
                      <img src={photoPreview} alt="Preview" />
                      <button 
                        className="remove-photo"
                        onClick={() => {
                          setPhotoPreview(null);
                          setFormData(prev => ({ ...prev, photo: null }));
                        }}
                      >
                        ✕
                      </button>
                    </div>
                  ) : (
                    <>
                      <div className="upload-icon">📷</div>
                      <p>Drag and drop or click to upload</p>
                      <input 
                        type="file" 
                        accept="image/jpeg,image/png"
                        onChange={(e) => handleFileChange(e, 'photo')}
                        className="file-input"
                      />
                    </>
                  )}
                </div>
              </div>

              {/* Candidate Name */}
              <div className="form-group">
                <label className="form-label">Candidate Name</label>
                <input 
                  type="text"
                  name="candidateName"
                  value={formData.candidateName}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your full name"
                />
              </div>

              {/* Mail ID */}
              <div className="form-group">
                <label className="form-label">Mail ID</label>
                <input 
                  type="email"
                  name="mailId"
                  value={formData.mailId}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your email"
                />
              </div>

              {/* Phone Number */}
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <input
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your phone number"
                />
              </div>

              {/* Location */}
              <div className="form-group">
                <label className="form-label">Location</label>
                <input
                  type="text"
                  name="location"
                  value={formData.location}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your location"
                />
              </div>

              {/* Current CTC */}
              <div className="form-group">
                <label className="form-label">Current CTC (in LPA)</label>
                <input
                  type="text"
                  name="currentCtc"
                  value={formData.currentCtc}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your current CTC (e.g., 5.5)"
                />
              </div>

              {/* HR Mail ID */}
              <div className="form-group">
                <label className="form-label">HR Mail ID</label>
                <input
                  type="email"
                  name="hrMailId"
                  value={formData.hrMailId}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter HR email address"
                />
              </div>

              {/* Position */}
              <div className="form-group">
                <label className="form-label">Position</label>
                <input
                  type="text"
                  name="position"
                  value={formData.position}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="e.g., Software Engineer, Data Analyst"
                />
              </div>

              {/* Experience Years */}
              <div className="form-group">
                <label className="form-label">Experience (Years)</label>
                <input
                  type="number"
                  name="experienceYears"
                  value={formData.experienceYears}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter years of experience"
                  min="0"
                  max="50"
                />
              </div>

              {/* Skills */}
              <div className="form-group full-width">
                <label className="form-label">
                  Skills (Max 200 words)
                  {formData.skills && (
                    <span className="word-count">
                      {' '}
                      - {formData.skills.trim().split(/\s+/).filter(word => word.length > 0).length} / 200 words
                    </span>
                  )}
                </label>
                <textarea
                  name="skills"
                  value={formData.skills}
                  onChange={handleInputChange}
                  className="form-textarea"
                  placeholder="List your technical and soft skills, programming languages, frameworks, tools, etc."
                  rows="6"
                />
              </div>

              {/* CV Upload */}
              <div className="form-group full-width">
                <label className="form-label">CV Upload (PDF)</label>
                <div 
                  className={`upload-box ${dragActive.cv ? 'drag-active' : ''}`}
                  onDragEnter={(e) => handleDrag(e, 'cv')}
                  onDragLeave={(e) => handleDrag(e, 'cv')}
                  onDragOver={(e) => handleDrag(e, 'cv')}
                  onDrop={(e) => handleDrop(e, 'cv')}
                >
                  <div className="upload-icon">📄</div>
                  <p>{formData.cv ? formData.cv.name : 'Drag and drop or click to upload PDF'}</p>
                  <input 
                    type="file" 
                    accept="application/pdf"
                    onChange={(e) => handleFileChange(e, 'cv')}
                    className="file-input"
                  />
                </div>
              </div>

              {/* GVT ID Upload */}
              <div className="form-group full-width">
                <label className="form-label">Upload GVT ID (.jpg)</label>
                <div 
                  className={`upload-box ${dragActive.gvtId ? 'drag-active' : ''}`}
                  onDragEnter={(e) => handleDrag(e, 'gvtId')}
                  onDragLeave={(e) => handleDrag(e, 'gvtId')}
                  onDragOver={(e) => handleDrag(e, 'gvtId')}
                  onDrop={(e) => handleDrop(e, 'gvtId')}
                >
                  <div className="upload-icon">🆔</div>
                  <p>{formData.gvtId ? formData.gvtId.name : 'Drag and drop or click to upload JPG'}</p>
                  <input 
                    type="file" 
                    accept="image/jpeg"
                    onChange={(e) => handleFileChange(e, 'gvtId')}
                    className="file-input"
                  />
                </div>
              </div>
            </div>

            {/* Save Button */}
            <div className="form-actions">
              <button 
                className="save-btn"
                onClick={handleSave}
                disabled={saveStatus === 'saving'}
              >
                {saveStatus === 'saving' ? 'Saving...' : 'Save'}
              </button>
              {saveStatus === 'success' && (
                <span className="status-message success">
                  ✓ Saved successfully! Your details have been sent to HR via email as PDF and will appear in their dashboard.
                </span>
              )}
              {saveStatus === 'error' && (
                <span className="status-message error">✗ Error saving data</span>
              )}
            </div>
          </div>
        )}

        {activeTab === 'myInterviews' && (
          <div className="info-card">
            <h2 className="card-title">My Scheduled Interviews</h2>
            
            {loadingInterviews ? (
              <div className="loading-message">Loading interviews...</div>
            ) : interviews.length === 0 ? (
              <div className="no-interviews-message">
                <p>No interviews scheduled yet.</p>
              </div>
            ) : (
              <div className="interviews-list">
                {interviews.map((interview) => (
                  <div key={interview.interviewId} className="interview-card">
                    <div className="interview-header">
                      <h3 className="interview-position">{interview.position}</h3>
                      <span className={`status-badge ${getStatusBadgeClass(interview.status)}`}>
                        {interview.status}
                      </span>
                    </div>
                    
                    <div className="interview-details">
                      <div className="detail-row">
                        <span className="detail-label">📅 Date:</span>
                        <span className="detail-value">{formatDate(interview.interviewDate)}</span>
                      </div>
                      
                      <div className="detail-row">
                        <span className="detail-label">🕐 Time:</span>
                        <span className="detail-value">
                          {formatTime(interview.interviewTimeFrom)} - {formatTime(interview.interviewTimeTo)}
                        </span>
                      </div>
                      
                      <div className="detail-section">
                        <h4 className="section-title">👤 Scheduled By (HR)</h4>
                        <div className="detail-row">
                          <span className="detail-label">Name:</span>
                          <span className="detail-value">{interview.hrName || 'N/A'}</span>
                        </div>
                        {interview.hrDesignation && (
                          <div className="detail-row">
                            <span className="detail-label">Designation:</span>
                            <span className="detail-value">{interview.hrDesignation}</span>
                          </div>
                        )}
                        <div className="detail-row">
                          <span className="detail-label">Email:</span>
                          <span className="detail-value">{interview.hrEmail || 'N/A'}</span>
                        </div>
                        {interview.hrPhone && (
                          <div className="detail-row">
                            <span className="detail-label">Phone:</span>
                            <span className="detail-value">{interview.hrPhone}</span>
                          </div>
                        )}
                      </div>
                      
                      {interview.panelistName && (
                        <div className="detail-section">
                          <h4 className="section-title">👨‍💼 Interviewer (Panelist)</h4>
                          <div className="detail-row">
                            <span className="detail-label">Name:</span>
                            <span className="detail-value">{interview.panelistName}</span>
                          </div>
                          <div className="detail-row">
                            <span className="detail-label">Email:</span>
                            <span className="detail-value">{interview.panelistEmail || 'N/A'}</span>
                          </div>
                        </div>
                      )}
                      
                      {interview.notes && (
                        <div className="detail-section">
                          <h4 className="section-title">📝 Notes</h4>
                          <p className="interview-notes">{interview.notes}</p>
                        </div>
                      )}
                      
                      {interview.meetingLink && (
                        <div className="detail-section">
                          <h4 className="section-title">🔗 Meeting Link</h4>
                          <a
                            href={interview.meetingLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="meeting-link"
                          >
                            Join Meeting
                          </a>
                        </div>
                      )}
                      
                      {interview.feedback && (
                        <div className="detail-section">
                          <h4 className="section-title">💬 Feedback</h4>
                          <p className="interview-feedback">{interview.feedback}</p>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'feedback' && (
          <div className="info-card">
            <h2 className="card-title">Interview Feedback</h2>
            
            {loadingFeedback ? (
              <div className="loading-message">Loading feedback...</div>
            ) : feedbackList.length === 0 ? (
              <div className="no-feedback-message">
                <p>No interview feedback available yet.</p>
                <p className="info-text">Feedback will appear here after your interviews are completed and evaluated by the panelist.</p>
              </div>
            ) : (
              <div className="feedback-list">
                {feedbackList.map((feedback) => (
                  <div key={feedback.id} className="feedback-card">
                    <div className="feedback-header">
                      <h3 className="feedback-position">{feedback.jobRoleSpecification}</h3>
                      <span className={`recommendation-badge ${feedback.techPanelRecommendation === 'SELECTED' ? 'selected' : feedback.techPanelRecommendation === 'REJECTED' ? 'rejected' : 'hold'}`}>
                        {feedback.techPanelRecommendation}
                      </span>
                    </div>
                    
                    <div className="feedback-details">
                      <div className="detail-row">
                        <span className="detail-label">📅 Evaluation Date:</span>
                        <span className="detail-value">{formatDate(feedback.evaluationDate)}</span>
                      </div>
                      
                      <div className="detail-row">
                        <span className="detail-label">👨‍💼 Evaluator:</span>
                        <span className="detail-value">{feedback.evaluatorNames}</span>
                      </div>
                      
                      <div className="detail-row">
                        <span className="detail-label">⭐ Overall Rating:</span>
                        <span className="detail-value rating-value">{feedback.overallRating} / 10</span>
                      </div>
                      
                      <div className="detail-row">
                        <span className="detail-label">💼 Job Level:</span>
                        <span className="detail-value">{feedback.jobLevel}</span>
                      </div>
                      
                      <div className="detail-row">
                        <span className="detail-label">📊 Experience:</span>
                        <span className="detail-value">{feedback.yearsOfExperience} years (Tech: {feedback.yearsOfExperienceInTech} years)</span>
                      </div>
                      
                      {feedback.certifications && (
                        <div className="detail-section">
                          <h4 className="section-title">🎓 Certifications</h4>
                          <p className="feedback-text">{feedback.certifications}</p>
                        </div>
                      )}
                      
                      {feedback.overallFeedback && (
                        <div className="detail-section">
                          <h4 className="section-title">💬 Overall Feedback</h4>
                          <p className="feedback-text">{feedback.overallFeedback}</p>
                        </div>
                      )}
                      
                      {feedback.suitabilityForRequirement && (
                        <div className="detail-section">
                          <h4 className="section-title">✅ Suitability for Requirement</h4>
                          <p className="feedback-text">{feedback.suitabilityForRequirement}</p>
                        </div>
                      )}
                      
                      {feedback.improvementFocusArea && (
                        <div className="detail-section">
                          <h4 className="section-title">📈 Areas for Improvement</h4>
                          <p className="feedback-text">{feedback.improvementFocusArea}</p>
                        </div>
                      )}
                      
                      {/* Technical Skills Ratings */}
                      <div className="detail-section">
                        <h4 className="section-title">🔧 Technical Skills Assessment</h4>
                        <div className="skills-grid">
                          {feedback.communicationRating && (
                            <div className="skill-item">
                              <span className="skill-name">Communication:</span>
                              <span className="skill-rating">{feedback.communicationRating}/10</span>
                            </div>
                          )}
                          {feedback.programmingLanguageRating && (
                            <div className="skill-item">
                              <span className="skill-name">Programming:</span>
                              <span className="skill-rating">{feedback.programmingLanguageRating}/10</span>
                            </div>
                          )}
                          {feedback.awsNativeServicesRating && (
                            <div className="skill-item">
                              <span className="skill-name">AWS Native Services:</span>
                              <span className="skill-rating">{feedback.awsNativeServicesRating}/10</span>
                            </div>
                          )}
                          {feedback.microservicesDesignPatternsRating && (
                            <div className="skill-item">
                              <span className="skill-name">Microservices:</span>
                              <span className="skill-rating">{feedback.microservicesDesignPatternsRating}/10</span>
                            </div>
                          )}
                          {feedback.containerizationRating && (
                            <div className="skill-item">
                              <span className="skill-name">Containerization:</span>
                              <span className="skill-rating">{feedback.containerizationRating}/10</span>
                            </div>
                          )}
                          {feedback.frontendStackRating && (
                            <div className="skill-item">
                              <span className="skill-name">Frontend Stack:</span>
                              <span className="skill-rating">{feedback.frontendStackRating}/10</span>
                            </div>
                          )}
                        </div>
                      </div>
                      
                      {feedback.awsNativeServicesNotes && (
                        <div className="detail-section">
                          <h4 className="section-title">📝 Technical Notes</h4>
                          <p className="feedback-text">{feedback.awsNativeServicesNotes}</p>
                        </div>
                      )}
                      
                      <div className="feedback-footer">
                        <span className="status-info">Status: {feedback.status}</span>
                        {feedback.sentToHR && (
                          <span className="sent-hr-badge">✓ Sent to HR</span>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
};

export default CandidateInfo;

// Made with Bob
