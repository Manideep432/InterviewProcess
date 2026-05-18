import React, { useState } from 'react';
import authService from '../services/authService';
import './CandidateInfo.css';

const CandidateInfo = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('home');
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

  return (
    <div className="candidate-dashboard">
      {/* Header/Navbar */}
      <header className="candidate-header">
        <div className="header-left">
          <span className="role-badge">Role: CANDIDATE</span>
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

        {activeTab === 'feedback' && (
          <div className="info-card">
            <h2 className="card-title">Feed Back</h2>
            <textarea 
              className="feedback-textarea"
              placeholder="Enter your feedback here..."
              rows="8"
            ></textarea>
            <button className="save-btn">Submit Feedback</button>
          </div>
        )}
      </main>
    </div>
  );
};

export default CandidateInfo;

// Made with Bob
