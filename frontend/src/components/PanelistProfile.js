import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import './PanelistProfile.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

/**
 * PanelistProfile Component - Tabbed profile view with organized sections
 */
const PanelistProfile = ({ user, onBack }) => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [editedProfile, setEditedProfile] = useState({});
  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_URL}/api/panelists/profile/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${authService.getToken()}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Profile data received:', data);
      
      if (data.success && data.profile) {
        // Ensure isActive is properly set (handle both 'active' and 'isActive' from backend)
        const profileData = {
          ...data.profile,
          isActive: data.profile.isActive !== undefined ? data.profile.isActive :
                   (data.profile.active !== undefined ? data.profile.active : true)
        };
        setProfile(profileData);
        setEditedProfile(profileData);
      } else {
        const basicProfile = {
          id: null,
          username: user.username || 'Unknown User',
          email: user.email || 'No email',
          specialization: null,
          experienceYears: 0,
          expertise: null,
          phone: null,
          location: null,
          linkedinUrl: null,
          designation: null,
          company: null,
          bio: null,
          skills: null,
          certifications: null,
          education: null,
          department: null,
          employeeId: null,
          workType: null,
          teamName: null,
          reportingManager: null,
          isActive: true,
          assignedHrName: null
        };
        setProfile(basicProfile);
        setEditedProfile(basicProfile);
        setError('⚠️ No panelist profile found. Please contact HR to set up your profile.');
      }
    } catch (err) {
      console.error('Error loading profile:', err);
      const basicProfile = {
        id: null,
        username: user.username || 'Unknown User',
        email: user.email || 'No email',
        specialization: null,
        experienceYears: 0,
        expertise: null,
        phone: null,
        location: null,
        linkedinUrl: null,
        designation: null,
        company: null,
        bio: null,
        skills: null,
        certifications: null,
        education: null,
        department: null,
        employeeId: null,
        workType: null,
        teamName: null,
        reportingManager: null,
        isActive: true,
        assignedHrName: null
      };
      setProfile(basicProfile);
      setEditedProfile(basicProfile);
      setError(`⚠️ Unable to load profile: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
    setEditedProfile({ ...profile });
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditedProfile({ ...profile });
    setError('');
  };

  const handleChange = (field, value) => {
    setEditedProfile({
      ...editedProfile,
      [field]: value
    });
  };

  const handleSave = async () => {
    setLoading(true);
    setError('');
    
    // Validate required fields
    if (!editedProfile.specialization || editedProfile.specialization.trim() === '') {
      setError('❌ Specialization is required');
      setLoading(false);
      return;
    }
    
    try {
      const response = await fetch(`${API_URL}/api/panelists/profile/${user.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authService.getToken()}`
        },
        body: JSON.stringify(editedProfile)
      });

      const data = await response.json();
      if (data.success) {
        setProfile(data.profile);
        setEditedProfile(data.profile);
        setIsEditing(false);
        setError('');
        alert('✅ Profile saved successfully!');
      } else {
        setError('❌ ' + (data.message || 'Failed to update profile'));
      }
    } catch (err) {
      setError('❌ Error updating profile: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && !profile) {
    return (
      <div className="profile-loading">
        <div className="spinner"></div>
        <p>Loading profile...</p>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="profile-error">
        <h2>❌ Unable to Load Profile</h2>
        <p>{error || 'Profile not found. Please contact your HR administrator.'}</p>
        <button onClick={onBack} className="back-button">← Back to Dashboard</button>
        <button onClick={loadProfile} className="back-button" style={{marginLeft: '10px'}}>🔄 Retry</button>
      </div>
    );
  }

  const currentData = isEditing ? editedProfile : profile;

  const tabs = [
    { id: 'overview', label: 'Overview', icon: '👤' },
    { id: 'contact', label: 'Contact', icon: '📞' },
    { id: 'professional', label: 'Professional', icon: '💼' },
    { id: 'credentials', label: 'Credentials', icon: '🎓' },
    { id: 'team', label: 'Team', icon: '👥' }
  ];

  const renderTabContent = () => {
    switch (activeTab) {
      case 'overview':
        return (
          <div className="tab-content">
            <h3 className="tab-section-title">Personal Information</h3>
            <div className="info-grid">
              <div className="info-row">
                <label className="info-label">Full Name</label>
                <div className="info-value">{profile.username}</div>
              </div>
              <div className="info-row">
                <label className="info-label">Email</label>
                <div className="info-value">{profile.email}</div>
              </div>
              <div className="info-row">
                <label className="info-label">Specialization <span style={{color: 'red'}}>*</span></label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.specialization || ''}
                    onChange={(e) => handleChange('specialization', e.target.value)}
                    placeholder="e.g., Full Stack Development"
                    required
                  />
                ) : (
                  <div className="info-value">{currentData.specialization || 'Not set'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Experience (Years)</label>
                {isEditing ? (
                  <input
                    type="number"
                    className="info-input"
                    value={currentData.experienceYears || ''}
                    onChange={(e) => handleChange('experienceYears', parseInt(e.target.value))}
                    placeholder="Years of experience"
                  />
                ) : (
                  <div className="info-value">{currentData.experienceYears || 0} years</div>
                )}
              </div>
              <div className="info-row full-width">
                <label className="info-label">Bio</label>
                {isEditing ? (
                  <textarea
                    className="info-textarea"
                    value={currentData.bio || ''}
                    onChange={(e) => handleChange('bio', e.target.value)}
                    placeholder="Tell us about yourself..."
                    rows="4"
                  />
                ) : (
                  <div className="info-value">{currentData.bio || 'No bio provided'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Status</label>
                <div className="info-value">
                  <span className={`status-badge ${currentData.isActive ? 'active' : 'inactive'}`}>
                    {currentData.isActive ? '✅ Active' : '❌ Inactive'}
                  </span>
                </div>
              </div>
              <div className="info-row">
                <label className="info-label">Assigned HR</label>
                <div className="info-value">{currentData.assignedHrName || 'Not assigned'}</div>
              </div>
            </div>
          </div>
        );

      case 'contact':
        return (
          <div className="tab-content">
            <h3 className="tab-section-title">Contact Information</h3>
            <div className="contact-grid">
              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">Preferred contact</span>
                </div>
                <div className="contact-content">
                  <p className="contact-description">The best way to contact me is via email</p>
                </div>
              </div>

              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">Email</span>
                </div>
                <div className="contact-content">
                  <a href={`mailto:${profile.email}`} className="contact-link">
                    {profile.email}
                  </a>
                </div>
              </div>

              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">Slack</span>
                </div>
                <div className="contact-content">
                  {isEditing ? (
                    <input
                      type="text"
                      className="info-input"
                      value={currentData.slackHandle || ''}
                      onChange={(e) => handleChange('slackHandle', e.target.value)}
                      placeholder="@username"
                    />
                  ) : (
                    <span className="contact-text">
                      {currentData.slackHandle || `@${profile.username}`}
                    </span>
                  )}
                </div>
              </div>

              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">Phone</span>
                </div>
                <div className="contact-content">
                  {isEditing ? (
                    <input
                      type="tel"
                      className="info-input"
                      value={currentData.phone || ''}
                      onChange={(e) => handleChange('phone', e.target.value)}
                      placeholder="+91-9640652224"
                    />
                  ) : (
                    currentData.phone ? (
                      <a href={`tel:${currentData.phone}`} className="contact-link">
                        Mobile {currentData.phone}
                      </a>
                    ) : (
                      <span className="contact-text">Not provided</span>
                    )
                  )}
                </div>
              </div>

              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">Location</span>
                </div>
                <div className="contact-content">
                  {isEditing ? (
                    <input
                      type="text"
                      className="info-input"
                      value={currentData.location || ''}
                      onChange={(e) => handleChange('location', e.target.value)}
                      placeholder="City, Country"
                    />
                  ) : (
                    <span className="contact-text">{currentData.location || 'Not provided'}</span>
                  )}
                </div>
              </div>

              <div className="contact-item">
                <div className="contact-header">
                  <span className="contact-label">LinkedIn</span>
                </div>
                <div className="contact-content">
                  {isEditing ? (
                    <input
                      type="url"
                      className="info-input"
                      value={currentData.linkedinUrl || ''}
                      onChange={(e) => handleChange('linkedinUrl', e.target.value)}
                      placeholder="https://linkedin.com/in/username"
                    />
                  ) : (
                    currentData.linkedinUrl ? (
                      <a href={currentData.linkedinUrl} target="_blank" rel="noopener noreferrer" className="contact-link">
                        View LinkedIn Profile
                      </a>
                    ) : (
                      <span className="contact-text">Not provided</span>
                    )
                  )}
                </div>
              </div>
            </div>
          </div>
        );

      case 'professional':
        return (
          <div className="tab-content">
            <h3 className="tab-section-title">Professional Details</h3>
            <div className="info-grid">
              <div className="info-row">
                <label className="info-label">Company</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.company || ''}
                    onChange={(e) => handleChange('company', e.target.value)}
                    placeholder="Company name"
                  />
                ) : (
                  <div className="info-value">{currentData.company || 'Not provided'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Designation</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.designation || ''}
                    onChange={(e) => handleChange('designation', e.target.value)}
                    placeholder="Job title"
                  />
                ) : (
                  <div className="info-value">{currentData.designation || 'Not provided'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Department</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.department || ''}
                    onChange={(e) => handleChange('department', e.target.value)}
                    placeholder="e.g., Engineering, IT"
                  />
                ) : (
                  <div className="info-value">{currentData.department || 'Not provided'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Employee ID</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.employeeId || ''}
                    onChange={(e) => handleChange('employeeId', e.target.value)}
                    placeholder="Employee ID"
                  />
                ) : (
                  <div className="info-value">{currentData.employeeId || 'Not provided'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Work Type</label>
                {isEditing ? (
                  <select
                    className="info-select"
                    value={currentData.workType || ''}
                    onChange={(e) => handleChange('workType', e.target.value)}
                  >
                    <option value="">Select work type</option>
                    <option value="Remote">Remote</option>
                    <option value="Hybrid">Hybrid</option>
                    <option value="On-site">On-site</option>
                  </select>
                ) : (
                  <div className="info-value">{currentData.workType || 'Not specified'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Experience</label>
                <div className="info-value">{currentData.experienceYears || 0} years</div>
              </div>
              <div className="info-row full-width">
                <label className="info-label">Skills</label>
                {isEditing ? (
                  <textarea
                    className="info-textarea"
                    value={currentData.skills || ''}
                    onChange={(e) => handleChange('skills', e.target.value)}
                    placeholder="e.g., React, Node.js, Python, AWS, Docker"
                    rows="4"
                  />
                ) : (
                  <div className="info-value">{currentData.skills || 'No skills listed'}</div>
                )}
              </div>
              <div className="info-row full-width">
                <label className="info-label">Expertise Areas</label>
                {isEditing ? (
                  <textarea
                    className="info-textarea"
                    value={currentData.expertise || ''}
                    onChange={(e) => handleChange('expertise', e.target.value)}
                    placeholder="Describe your areas of expertise..."
                    rows="4"
                  />
                ) : (
                  <div className="info-value">{currentData.expertise || 'Not provided'}</div>
                )}
              </div>
            </div>
          </div>
        );

      case 'credentials':
        return (
          <div className="tab-content">
            <h3 className="tab-section-title">Education & Certifications</h3>
            <div className="info-grid">
              <div className="info-row full-width">
                <label className="info-label">Education</label>
                {isEditing ? (
                  <textarea
                    className="info-textarea"
                    value={currentData.education || ''}
                    onChange={(e) => handleChange('education', e.target.value)}
                    placeholder="e.g., Bachelor's in Computer Science, XYZ University"
                    rows="4"
                  />
                ) : (
                  <div className="info-value">{currentData.education || 'Not provided'}</div>
                )}
              </div>
              <div className="info-row full-width">
                <label className="info-label">Certifications</label>
                {isEditing ? (
                  <textarea
                    className="info-textarea"
                    value={currentData.certifications || ''}
                    onChange={(e) => handleChange('certifications', e.target.value)}
                    placeholder="e.g., AWS Certified Solutions Architect, Google Cloud Professional"
                    rows="6"
                  />
                ) : (
                  <div className="info-value">{currentData.certifications || 'No certifications listed'}</div>
                )}
              </div>
            </div>
          </div>
        );

      case 'team':
        return (
          <div className="tab-content">
            <h3 className="tab-section-title">Team & Reporting</h3>
            <div className="info-grid">
              <div className="info-row">
                <label className="info-label">Team Name</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.teamName || ''}
                    onChange={(e) => handleChange('teamName', e.target.value)}
                    placeholder="Team name"
                  />
                ) : (
                  <div className="info-value">{currentData.teamName || 'Not assigned'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Reporting Manager</label>
                {isEditing ? (
                  <input
                    type="text"
                    className="info-input"
                    value={currentData.reportingManager || ''}
                    onChange={(e) => handleChange('reportingManager', e.target.value)}
                    placeholder="Manager name"
                  />
                ) : (
                  <div className="info-value">{currentData.reportingManager || 'Not assigned'}</div>
                )}
              </div>
              <div className="info-row">
                <label className="info-label">Assigned HR</label>
                <div className="info-value">{currentData.assignedHrName || 'Not assigned'}</div>
              </div>
              <div className="info-row">
                <label className="info-label">Department</label>
                <div className="info-value">{currentData.department || 'Not provided'}</div>
              </div>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="panelist-profile-wrapper">
      {/* Top Navigation Bar */}
      <div className="profile-top-nav">
        <button onClick={onBack} className="back-btn">← Back to Dashboard</button>
        <div className="profile-actions-top">
          {!isEditing ? (
            <button onClick={handleEdit} className="edit-btn">✏️ Edit Profile</button>
          ) : (
            <>
              <button onClick={handleSave} className="save-btn" disabled={loading}>
                {loading ? '⏳ Saving...' : '💾 Save'}
              </button>
              <button onClick={handleCancel} className="cancel-btn">❌ Cancel</button>
            </>
          )}
        </div>
      </div>

      <div className="profile-full-container">
        {/* Profile Header */}
        <div className="profile-header-card">
          <div className="profile-photo-large">
            {profile.username?.charAt(0).toUpperCase() || 'U'}
          </div>
          <div className="profile-header-info">
            <h1 className="profile-name-large">{profile.username}</h1>
            <h2 className="profile-designation-large">
              {currentData.designation || 'APPLICATION DEVELOPER-CLOUD FULLSTACK'}
            </h2>
            <p className="profile-company-large">{currentData.company || 'Consulting'}</p>
            <div className="profile-meta">
              <span className="meta-item">
                <span className="meta-icon">📧</span>
                {profile.email}
              </span>
              <span className="meta-item">
                <span className="meta-icon">💼</span>
                {currentData.experienceYears || 0} years experience
              </span>
              <span className={`status-badge ${currentData.isActive ? 'active' : 'inactive'}`}>
                {currentData.isActive ? '✅ Active' : '❌ Inactive'}
              </span>
            </div>
          </div>
        </div>

        {error && (
          <div className={error.includes('⚠️') ? 'warning-alert' : 'error-alert'}>
            {error}
          </div>
        )}

        {/* Tabbed Navigation */}
        <div className="tabs-container">
          <div className="tabs-nav">
            {tabs.map(tab => (
              <button
                key={tab.id}
                className={`tab-button ${activeTab === tab.id ? 'active' : ''}`}
                onClick={() => setActiveTab(tab.id)}
              >
                <span className="tab-icon">{tab.icon}</span>
                <span className="tab-label">{tab.label}</span>
              </button>
            ))}
          </div>

          <div className="tabs-content-wrapper">
            {renderTabContent()}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PanelistProfile;

// Made with Bob
