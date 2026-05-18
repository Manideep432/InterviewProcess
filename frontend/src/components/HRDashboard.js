import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import './HRDashboard.css';

/**
 * HR Dashboard Component - Enhanced with HR Profile and Candidate Data Table
 */
const HRDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('home');
  const [dashboardData, setDashboardData] = useState(null);
  const [myCandidates, setMyCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [retryCount, setRetryCount] = useState(0);
  
  // Edit candidate state
  const [editingCandidate, setEditingCandidate] = useState(null);
  const [editFormData, setEditFormData] = useState({});
  const [editError, setEditError] = useState('');
  const [editSuccess, setEditSuccess] = useState('');
  const [updating, setUpdating] = useState(false);
  
  // Add New Candidate form state
  const [newCandidate, setNewCandidate] = useState({
    name: '',
    email: '',
    phone: '',
    position: '',
    experienceYears: '',
    skills: '',
    currentCtc: '',
    hrMailId: user.email, // Pre-fill with HR's email
    jdDetails: '',
    employmentType: 'FULL_TIME',
    location: '',
    username: '',
    password: ''
  });
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Panelist assignment state for Add New Candidate
  const [assignedPanelists, setAssignedPanelists] = useState([]);
  const [panelistEmailInput, setPanelistEmailInput] = useState('');
  const [panelistNameInput, setPanelistNameInput] = useState('');
  const [searchingPanelist, setSearchingPanelist] = useState(false);
  const [panelistSearchError, setPanelistSearchError] = useState('');

  // Add New Panelist form state
  const [newPanelist, setNewPanelist] = useState({
    email: '',
    username: '',
    password: '',
    specialization: '',
    experienceYears: '',
    expertise: '',
    phone: '',
    location: '',
    linkedinUrl: '',
    slackHandle: '',
    designation: '',
    company: '',
    bio: '',
    skills: '',
    certifications: '',
    education: '',
    department: '',
    employeeId: '',
    workType: 'On-site',
    teamName: '',
    reportingManager: ''
  });
  const [panelistFormError, setPanelistFormError] = useState('');
  const [panelistFormSuccess, setPanelistFormSuccess] = useState('');
  const [submittingPanelist, setSubmittingPanelist] = useState(false);

  // Manage Panelists state
  const [myPanelists, setMyPanelists] = useState([]);
  const [editingPanelist, setEditingPanelist] = useState(null);
  const [editPanelistFormData, setEditPanelistFormData] = useState({});
  const [editPanelistError, setEditPanelistError] = useState('');
  const [editPanelistSuccess, setEditPanelistSuccess] = useState('');
  const [updatingPanelist, setUpdatingPanelist] = useState(false);

  // Interview scheduling state
  const [schedulingInterview, setSchedulingInterview] = useState(null);
  const [interviewFormData, setInterviewFormData] = useState({
    panelistEmail: '',
    interviewDate: '',
    interviewTimeFrom: '',
    interviewTimeTo: '',
    notes: ''
  });
  const [interviewFormError, setInterviewFormError] = useState('');
  const [interviewFormSuccess, setInterviewFormSuccess] = useState('');
  const [submittingInterview, setSubmittingInterview] = useState(false);

  // Interview data state - stores interviews for each candidate
  const [candidateInterviews, setCandidateInterviews] = useState({});

  // HR Profile state
  const [hrProfile, setHrProfile] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState('');
  const [profileSuccess, setProfileSuccess] = useState('');
  const [profileFormData, setProfileFormData] = useState({
    fullName: '',
    phone: '',
    location: '',
    address: '',
    designation: '',
    department: '',
    employeeId: '',
    experienceYears: '',
    company: '',
    bio: '',
    linkedinUrl: '',
    slackHandle: '',
    emergencyContact: '',
    emergencyPhone: '',
    skills: '',
    certifications: '',
    education: '',
    workType: 'On-site',
    teamName: '',
    reportingManager: '',
    hrSpecialization: '',
    region: ''
  });

  useEffect(() => {
    fetchDashboardData();
    fetchMyCandidates();
    fetchMyPanelists();
    fetchHRProfile();
  }, []);

  const fetchDashboardData = async (isRetry = false) => {
    try {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem('token');
      
      if (!token) {
        throw new Error('No authentication token found. Please login again.');
      }

      console.log('Fetching HR dashboard data...');
      console.log('User ID:', user.id);
      console.log('Token exists:', !!token);

      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 30000); // 30 second timeout

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/dashboard`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      console.log('Response status:', response.status);
      console.log('Response ok:', response.ok);

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('Authentication failed. Please login again.');
        } else if (response.status === 403) {
          throw new Error('Access denied. You do not have HR permissions.');
        } else if (response.status === 404) {
          throw new Error('HR dashboard endpoint not found. Please check backend server.');
        } else if (response.status >= 500) {
          throw new Error('Server error. Please try again later.');
        } else {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.message || `Failed to fetch dashboard data (Status: ${response.status})`);
        }
      }

      const data = await response.json();
      console.log('Dashboard data received:', data);

      if (data.success) {
        setDashboardData(data.dashboard);
        setError(null);
        setRetryCount(0);
        console.log('Dashboard loaded successfully');
      } else {
        throw new Error(data.message || 'Failed to load dashboard');
      }
    } catch (err) {
      console.error('Error fetching dashboard:', err);
      
      // Silently retry without showing any error to user
      console.log('Error detected, retrying silently...');
      
      // Auto-retry logic (max 5 times)
      if (!isRetry && retryCount < 5) {
        console.log(`Auto-retrying... Attempt ${retryCount + 1}/5`);
        setRetryCount(prev => prev + 1);
        setTimeout(() => fetchDashboardData(true), 3000 * (retryCount + 1)); // Exponential backoff
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchMyCandidates = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        return;
      }

      console.log('Fetching my candidates...');

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/my-candidates`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        console.log('My candidates received:', data);
        
        if (data.success) {
          const candidates = data.candidates || [];
          setMyCandidates(candidates);
          
          // Fetch interviews for each candidate
          fetchInterviewsForCandidates(candidates);
        }
      }
    } catch (err) {
      console.error('Error fetching my candidates:', err);
    }
  };

  const fetchInterviewsForCandidates = async (candidates) => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token || !candidates || candidates.length === 0) {
        return;
      }

      console.log('Fetching interviews for candidates...');

      // Fetch interviews for each candidate
      const interviewPromises = candidates.map(async (candidate) => {
        try {
          const response = await fetch(
            `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/interviews/candidate/${encodeURIComponent(candidate.email)}`,
            {
              method: 'GET',
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json'
              },
              credentials: 'include'
            }
          );

          if (response.ok) {
            const data = await response.json();
            if (data.success && data.interviews) {
              return { email: candidate.email, interviews: data.interviews };
            }
          }
          return { email: candidate.email, interviews: [] };
        } catch (err) {
          console.error(`Error fetching interviews for ${candidate.email}:`, err);
          return { email: candidate.email, interviews: [] };
        }
      });

      const results = await Promise.all(interviewPromises);
      
      // Convert array to object for easy lookup
      const interviewsMap = {};
      results.forEach(result => {
        interviewsMap[result.email] = result.interviews;
      });
      
      setCandidateInterviews(interviewsMap);
      console.log('Interviews fetched:', interviewsMap);
    } catch (err) {
      console.error('Error fetching interviews for candidates:', err);
    }
  };

  const fetchMyPanelists = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        return;
      }

      console.log('Fetching my panelists...');

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/hr/${user.id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        console.log('My panelists received:', data);
        
        if (data.success) {
          setMyPanelists(data.panelists || []);
        }
      }
    } catch (err) {
      console.error('Error fetching my panelists:', err);
    }
  };

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewCandidate(prev => ({
      ...prev,
      [name]: value
    }));
    setFormError('');
  };

  // Handle panelist email input with auto-complete
  const handlePanelistEmailChange = async (e) => {
    const email = e.target.value;
    setPanelistEmailInput(email);
    setPanelistSearchError('');

    // Clear name if email is cleared
    if (!email.trim()) {
      setPanelistNameInput('');
      return;
    }

    // Only search if email looks valid
    if (email.includes('@') && email.length > 5) {
      setSearchingPanelist(true);
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(
          `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/search-panelist?email=${encodeURIComponent(email)}`,
          {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );

        const data = await response.json();

        if (response.ok && data.success && data.panelist) {
          // Auto-populate the name
          setPanelistNameInput(data.panelist.username);
          setPanelistSearchError('');
        } else {
          setPanelistNameInput('');
          setPanelistSearchError('Panelist not found with this email');
        }
      } catch (err) {
        console.error('Error searching panelist:', err);
        setPanelistNameInput('');
        setPanelistSearchError('Error searching panelist');
      } finally {
        setSearchingPanelist(false);
      }
    }
  };

  // Add panelist to the list
  const handleAddPanelist = () => {
    if (!panelistEmailInput.trim() || !panelistNameInput.trim()) {
      setPanelistSearchError('Please enter a valid panelist email');
      return;
    }

    // Check if already added
    if (assignedPanelists.some(p => p.email === panelistEmailInput)) {
      setPanelistSearchError('This panelist is already added');
      return;
    }

    setAssignedPanelists(prev => [
      ...prev,
      { email: panelistEmailInput, name: panelistNameInput }
    ]);

    // Clear inputs
    setPanelistEmailInput('');
    setPanelistNameInput('');
    setPanelistSearchError('');
  };

  // Remove panelist from the list
  const handleRemovePanelist = (email) => {
    setAssignedPanelists(prev => prev.filter(p => p.email !== email));
  };
  const handlePanelistInputChange = (e) => {
    const { name, value } = e.target;
    setNewPanelist(prev => ({
      ...prev,
      [name]: value
    }));
    setPanelistFormError('');
  };


  const handleSubmitNewPanelist = async (e) => {
    e.preventDefault();
    setSubmittingPanelist(true);
    setPanelistFormError('');
    setPanelistFormSuccess('');

    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        throw new Error('No authentication token found');
      }

      // First, create the user account with PANELIST role
      const userResponse = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/auth/register`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: newPanelist.username,
          email: newPanelist.email,
          password: newPanelist.password,
          role: 'PANELIST'
        })
      });

      if (!userResponse.ok) {
        const errorData = await userResponse.json().catch(() => ({}));
        throw new Error(errorData.error || errorData.message || 'Failed to create panelist user account');
      }

      const userData = await userResponse.json();
      const userId = userData.id;

      if (!userId) {
        throw new Error('Panelist user account created, but user ID was not returned by the server');
      }

      // Then, create the panelist profile
      const panelistResponse = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/create`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: userId,
          hrId: user.id,
          specialization: newPanelist.specialization,
          experienceYears: newPanelist.experienceYears ? parseInt(newPanelist.experienceYears) : null,
          expertise: newPanelist.expertise
        })
      });

      if (!panelistResponse.ok) {
        const errorText = await panelistResponse.text();
        let errorMessage = 'Failed to create panelist profile';
        try {
          const errorData = JSON.parse(errorText);
          errorMessage = errorData.message || errorMessage;
        } catch (e) {
          errorMessage = errorText || errorMessage;
        }
        throw new Error(errorMessage);
      }

      const responseText = await panelistResponse.text();
      let panelistData;
      try {
        panelistData = JSON.parse(responseText);
      } catch (e) {
        throw new Error('Invalid response from server when creating panelist profile');
      }
      const panelistId = panelistData.panelist.id;

      // Update panelist profile with additional fields
      const updateResponse = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/profile/${userId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          specialization: newPanelist.specialization,
          experienceYears: newPanelist.experienceYears ? parseInt(newPanelist.experienceYears) : null,
          expertise: newPanelist.expertise,
          phone: newPanelist.phone,
          location: newPanelist.location,
          linkedinUrl: newPanelist.linkedinUrl,
          slackHandle: newPanelist.slackHandle,
          designation: newPanelist.designation,
          company: newPanelist.company,
          bio: newPanelist.bio,
          skills: newPanelist.skills,
          certifications: newPanelist.certifications,
          education: newPanelist.education,
          department: newPanelist.department,
          employeeId: newPanelist.employeeId,
          workType: newPanelist.workType,
          teamName: newPanelist.teamName,
          reportingManager: newPanelist.reportingManager
        })
      });

      if (!updateResponse.ok) {
        const errorText = await updateResponse.text();
        let errorMessage = 'Panelist created but failed to update profile details';
        try {
          const errorData = JSON.parse(errorText);
          errorMessage = errorData.message || errorMessage;
        } catch (e) {
          errorMessage = errorText || errorMessage;
        }
        throw new Error(errorMessage);
      }

      setPanelistFormSuccess('✅ Panelist created successfully!');
      
      // Reset form
      setNewPanelist({
        email: '',
        username: '',
        password: '',
        specialization: '',
        experienceYears: '',
        expertise: '',
        phone: '',
        location: '',
        linkedinUrl: '',
        slackHandle: '',
        designation: '',
        company: '',
        bio: '',
        skills: '',
        certifications: '',
        education: '',
        department: '',
        employeeId: '',
        workType: 'On-site',
        teamName: '',
        reportingManager: ''
      });

      // Refresh dashboard data
      fetchDashboardData();

    } catch (err) {
      console.error('Error creating panelist:', err);
      setPanelistFormError(err.message || 'Failed to create panelist');
    } finally {
      setSubmittingPanelist(false);
    }
  };

  const handleSubmitNewCandidate = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setFormError('');
    setFormSuccess('');

    try {
      // Validate required fields
      if (!newCandidate.name || !newCandidate.email || !newCandidate.phone ||
          !newCandidate.position || !newCandidate.username || !newCandidate.password) {
        throw new Error('Please fill in all required fields');
      }

      // Validate password length
      if (newCandidate.password.length < 8) {
        throw new Error('Password must be at least 8 characters long');
      }

      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/create-candidate`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newCandidate)
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Failed to create candidate');
      }

      setFormSuccess(`Candidate created successfully! Username: ${newCandidate.username}`);
      
      // Reset form
      setNewCandidate({
        name: '',
        email: '',
        phone: '',
        position: '',
        experienceYears: '',
        skills: '',
        currentCtc: '',
        hrMailId: user.email,
        jdDetails: '',
        employmentType: 'FULL_TIME',
        location: '',
        username: '',
        password: ''
      });

      // Reset panelist assignments
      setAssignedPanelists([]);
      setPanelistEmailInput('');
      setPanelistNameInput('');
      setPanelistSearchError('');

      // Refresh dashboard data
      fetchDashboardData();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditCandidate = (candidate) => {
    setEditingCandidate(candidate);
    setEditFormData({
      name: candidate.name || '',
      phone: candidate.phone || '',
      position: candidate.position || '',
      experienceYears: candidate.experienceYears || '',
      skills: candidate.skills || '',
      currentCtc: candidate.currentCtc || '',
      jdDetails: candidate.jdDetails || '',
      employmentType: candidate.employmentType || 'FULL_TIME',
      location: candidate.location || '',
      status: candidate.status || 'APPLIED'
    });
    setEditError('');
    setEditSuccess('');
  };

  const handleCancelEdit = () => {
    setEditingCandidate(null);
    setEditFormData({});
    setEditError('');
    setEditSuccess('');
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setEditError('');
  };

  const handleUpdateCandidate = async (e) => {
    e.preventDefault();
    setUpdating(true);
    setEditError('');
    setEditSuccess('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/update-candidate/${editingCandidate.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(editFormData)
      });

      // Check if response has content before parsing JSON
      const contentType = response.headers.get('content-type');
      let data = null;
      
      if (contentType && contentType.includes('application/json')) {
        const text = await response.text();
        if (text) {
          data = JSON.parse(text);
        }
      }

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to update candidate');
      }

      setEditSuccess('Candidate updated successfully!');
      
      // Refresh candidate list
      await fetchMyCandidates();
      
      // Close edit form after 2 seconds
      setTimeout(() => {
        handleCancelEdit();
      }, 2000);
    } catch (err) {
      setEditError(err.message || 'Failed to update candidate. Please try again.');
    } finally {
      setUpdating(false);
    }
  };

  const handleDeleteCandidate = async (candidateId, candidateName) => {
    if (!window.confirm(`Are you sure you want to delete ${candidateName}? This action cannot be undone.`)) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/delete-candidate/${candidateId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      // Check if response has content before parsing JSON
      const contentType = response.headers.get('content-type');
      let data = null;
      
      if (contentType && contentType.includes('application/json')) {
        const text = await response.text();
        if (text) {
          data = JSON.parse(text);
        }
      }

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to delete candidate');
      }

      // Refresh candidate list
      await fetchMyCandidates();
      await fetchDashboardData();
      
      alert('✅ Candidate deleted successfully!');
    } catch (err) {
      alert('❌ ' + (err.message || 'Failed to delete candidate. Please try again.'));
    }
  };

  const handleScheduleInterview = (candidate) => {
    setSchedulingInterview(candidate);
    setInterviewFormData({
      panelistEmail: candidate.assignedPanelist?.email || '',
      interviewDate: '',
      interviewTime: '',
      notes: ''
    });
    setInterviewFormError('');
    setInterviewFormSuccess('');
  };

  const handleCancelScheduleInterview = () => {
    setSchedulingInterview(null);
    setInterviewFormData({
      panelistEmail: '',
      interviewDate: '',
      interviewTimeFrom: '',
      interviewTimeTo: '',
      notes: ''
    });
    setInterviewFormError('');
    setInterviewFormSuccess('');
  };

  const handleInterviewInputChange = (e) => {
    const { name, value } = e.target;
    setInterviewFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setInterviewFormError('');
  };

  const handleSubmitInterview = async (e) => {
    e.preventDefault();
    setSubmittingInterview(true);
    setInterviewFormError('');
    setInterviewFormSuccess('');

    try {
      // Validate required fields
      if (!interviewFormData.panelistEmail) {
        throw new Error('Panelist email is required');
      }
      if (!interviewFormData.interviewDate) {
        throw new Error('Interview date is required');
      }
      if (!interviewFormData.interviewTimeFrom) {
        throw new Error('Interview start time is required');
      }
      if (!interviewFormData.interviewTimeTo) {
        throw new Error('Interview end time is required');
      }
      
      // Validate that end time is after start time
      if (interviewFormData.interviewTimeFrom >= interviewFormData.interviewTimeTo) {
        throw new Error('Interview end time must be after start time');
      }

      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/schedule-interview`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          candidateId: schedulingInterview.id,
          panelistEmail: interviewFormData.panelistEmail,
          interviewDate: interviewFormData.interviewDate,
          interviewTimeFrom: interviewFormData.interviewTimeFrom,
          interviewTimeTo: interviewFormData.interviewTimeTo,
          notes: interviewFormData.notes
        })
      });

      const contentType = response.headers.get('content-type');
      let data = null;
      
      if (contentType && contentType.includes('application/json')) {
        const text = await response.text();
        if (text) {
          data = JSON.parse(text);
        }
      }

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to schedule interview');
      }

      setInterviewFormSuccess('Interview scheduled successfully! Notifications sent to candidate and panelist.');
      
      // Refresh data
      await fetchMyCandidates();
      await fetchDashboardData();
      
      // Close form after 3 seconds
      setTimeout(() => {
        handleCancelScheduleInterview();
      }, 3000);
    } catch (err) {
      setInterviewFormError(err.message || 'Failed to schedule interview. Please try again.');
    } finally {
      setSubmittingInterview(false);
    }
  };

  const handleEditPanelist = (panelist) => {
    setEditingPanelist(panelist);
    setEditPanelistFormData({
      specialization: panelist.specialization || '',
      experienceYears: panelist.experienceYears || '',
      expertise: panelist.expertise || ''
    });
    setEditPanelistError('');
    setEditPanelistSuccess('');
  };

  const handleCancelEditPanelist = () => {
    setEditingPanelist(null);
    setEditPanelistFormData({});
    setEditPanelistError('');
    setEditPanelistSuccess('');
  };

  const handleEditPanelistInputChange = (e) => {
    const { name, value } = e.target;
    setEditPanelistFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setEditPanelistError('');
  };

  const handleUpdatePanelist = async (e) => {
    e.preventDefault();
    setUpdatingPanelist(true);
    setEditPanelistError('');
    setEditPanelistSuccess('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/${editingPanelist.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          ...editPanelistFormData,
          hrId: user.id
        })
      });

      const contentType = response.headers.get('content-type');
      let data = null;
      
      if (contentType && contentType.includes('application/json')) {
        const text = await response.text();
        if (text) {
          data = JSON.parse(text);
        }
      }

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to update panelist');
      }

      setEditPanelistSuccess('Panelist updated successfully!');
      
      // Refresh panelist list
      await fetchMyPanelists();
      await fetchDashboardData();
      
      // Close edit form after 2 seconds
      setTimeout(() => {
        handleCancelEditPanelist();
      }, 2000);
    } catch (err) {
      setEditPanelistError(err.message || 'Failed to update panelist. Please try again.');
    } finally {
      setUpdatingPanelist(false);
    }
  };

  const handleDeletePanelist = async (panelistId, panelistName) => {
    if (!window.confirm(`Are you sure you want to delete ${panelistName}? This action cannot be undone.`)) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/${panelistId}?hrId=${user.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      const contentType = response.headers.get('content-type');
      let data = null;
      
      if (contentType && contentType.includes('application/json')) {
        const text = await response.text();
        if (text) {
          data = JSON.parse(text);
        }
      }

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to delete panelist');
      }

      // Refresh panelist list
      await fetchMyPanelists();
      await fetchDashboardData();
      
      alert('✅ Panelist deleted successfully!');
    } catch (err) {
      alert('❌ ' + (err.message || 'Failed to delete panelist. Please try again.'));
    }
  };

  // HR Profile Functions
  const fetchHRProfile = async () => {
    try {
      setProfileLoading(true);
      const token = localStorage.getItem('token');
      
      if (!token) {
        return;
      }

      console.log('Fetching HR profile...');

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/profile`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        console.log('HR profile received:', data);
        
        if (data.success && data.profile) {
          setHrProfile(data.profile);
          setProfileFormData({
            fullName: data.profile.fullName || '',
            phone: data.profile.phone || '',
            location: data.profile.location || '',
            address: data.profile.address || '',
            designation: data.profile.designation || '',
            department: data.profile.department || '',
            employeeId: data.profile.employeeId || '',
            experienceYears: data.profile.experienceYears || '',
            company: data.profile.company || '',
            bio: data.profile.bio || '',
            linkedinUrl: data.profile.linkedinUrl || '',
            slackHandle: data.profile.slackHandle || '',
            emergencyContact: data.profile.emergencyContact || '',
            emergencyPhone: data.profile.emergencyPhone || '',
            skills: data.profile.skills || '',
            certifications: data.profile.certifications || '',
            education: data.profile.education || '',
            workType: data.profile.workType || 'On-site',
            teamName: data.profile.teamName || '',
            reportingManager: data.profile.reportingManager || '',
            hrSpecialization: data.profile.hrSpecialization || '',
            region: data.profile.region || ''
          });
        }
      }
    } catch (err) {
      console.error('Error fetching HR profile:', err);
    } finally {
      setProfileLoading(false);
    }
  };

  const handleProfileInputChange = (e) => {
    const { name, value } = e.target;
    setProfileFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setProfileError('');
    setProfileSuccess('');
    setProfileLoading(true);

    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        throw new Error('No authentication token found');
      }

      console.log('Saving HR profile...');

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/hr/${user.id}/profile`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(profileFormData)
      });

      const data = await response.json();

      if (response.ok && data.success) {
        setProfileSuccess('Profile saved successfully!');
        setHrProfile(data.profile);
        setTimeout(() => setProfileSuccess(''), 3000);
      } else {
        throw new Error(data.message || 'Failed to save profile');
      }
    } catch (err) {
      console.error('Error saving profile:', err);
      setProfileError(err.message || 'Failed to save profile. Please try again.');
    } finally {
      setProfileLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN');
  };

  const formatCurrency = (amount) => {
    if (!amount) return 'N/A';
    return `₹${parseFloat(amount).toLocaleString('en-IN')}`;
  };

  const getCandidateDisplayStatus = (candidate) => {
    const interviews = candidateInterviews[candidate.email] || [];
    if (!interviews.length) {
      return candidate.status || 'N/A';
    }

    const latestInterview = interviews
      .slice()
      .sort((a, b) => new Date(`${b.interviewDate}T${b.interviewTimeTo || '00:00'}`) - new Date(`${a.interviewDate}T${a.interviewTimeTo || '00:00'}`))[0];

    if (candidate.status === 'SELECTED' || candidate.status === 'REJECTED') {
      return candidate.status;
    }

    if (latestInterview?.status === 'COMPLETED') {
      return 'INTERVIEW_COMPLETED';
    }

    return candidate.status || latestInterview?.status || 'N/A';
  };

  return (
    <div className="hr-dashboard-container">
      <div className="hr-dashboard-header">
        <div className="header-content">
          <h1>💼 HR Dashboard</h1>
          <div className="user-info-header">
            <span className="welcome-text">Welcome, <strong>{user.username}</strong></span>
            <div className="header-actions">
              <button
                className="hr-profile-button"
                onClick={() => setActiveTab('profile')}
              >
                👤 HR Profile
              </button>
              <button onClick={handleLogout} className="logout-button">
                🚪 Logout
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="hr-tabs">
        <button
          className={`hr-tab ${activeTab === 'home' ? 'active' : ''}`}
          onClick={() => setActiveTab('home')}
        >
          🏠 Home
        </button>
        <button
          className={`hr-tab ${activeTab === 'manageCandidates' ? 'active' : ''}`}
          onClick={() => setActiveTab('manageCandidates')}
        >
          📝 Manage Candidates
        </button>
        <button
          className={`hr-tab ${activeTab === 'addCandidate' ? 'active' : ''}`}
          onClick={() => setActiveTab('addCandidate')}
        >
          ➕ Add New Candidate
        </button>
        <button
          className={`hr-tab ${activeTab === 'addPanelist' ? 'active' : ''}`}
          onClick={() => setActiveTab('addPanelist')}
        >
          👨‍💼 Add New Panelist
        </button>
        <button
          className={`hr-tab ${activeTab === 'managePanelists' ? 'active' : ''}`}
          onClick={() => setActiveTab('managePanelists')}
        >
          👥 Manage Panelists
        </button>
      </div>

      {/* Content Area */}
      <div className="hr-content">
        {loading ? (
          <div className="loading-message">
            <div className="spinner"></div>
            <p>Loading dashboard data...</p>
          </div>
        ) : error ? (
          <div className="error-message">
            <p>❌ Error: {error}</p>
            <button onClick={fetchDashboardData} className="retry-button">
              🔄 Retry
            </button>
          </div>
        ) : (
          <>
            {/* Home Tab - Candidate Data Table */}
            {activeTab === 'home' && dashboardData && (
              <div className="home-section">
                <h2>📊 Dashboard Overview</h2>
                
                {/* Statistics Cards */}
                <div className="dashboard-stats">
                  <div className="stat-card-large">
                    <div className="stat-icon-large">👥</div>
                    <div className="stat-content">
                      <div className="stat-number-large">{dashboardData.totalCandidates || 0}</div>
                      <div className="stat-label-large">Total Candidates</div>
                    </div>
                  </div>
                  
                  <div className="stat-card-large">
                    <div className="stat-icon-large">👨‍💼</div>
                    <div className="stat-content">
                      <div className="stat-number-large">{dashboardData.totalPanelists || 0}</div>
                      <div className="stat-label-large">Total Panelists</div>
                    </div>
                  </div>
                  
                  <div className="stat-card-large">
                    <div className="stat-icon-large">📅</div>
                    <div className="stat-content">
                      <div className="stat-number-large">{dashboardData.totalInterviews || 0}</div>
                      <div className="stat-label-large">Total Interviews</div>
                    </div>
                  </div>
                  
                  <div className="stat-card-large">
                    <div className="stat-icon-large">✅</div>
                    <div className="stat-content">
                      <div className="stat-number-large">{dashboardData.activePanelists || 0}</div>
                      <div className="stat-label-large">Active Panelists</div>
                    </div>
                  </div>
                </div>

                {/* Candidate Status Breakdown */}
                {dashboardData.candidatesByStatus && (
                  <div className="status-breakdown">
                    <h3>📈 Candidates by Status</h3>
                    <div className="status-cards">
                      <div className="status-card status-applied-card">
                        <div className="status-count">{dashboardData.candidatesByStatus.APPLIED || 0}</div>
                        <div className="status-name">Applied</div>
                      </div>
                      <div className="status-card status-screening-card">
                        <div className="status-count">{dashboardData.candidatesByStatus.SCREENING || 0}</div>
                        <div className="status-name">Screening</div>
                      </div>
                      <div className="status-card status-interview-card">
                        <div className="status-count">{dashboardData.candidatesByStatus.INTERVIEW || 0}</div>
                        <div className="status-name">Interview</div>
                      </div>
                      <div className="status-card status-selected-card">
                        <div className="status-count">{dashboardData.candidatesByStatus.SELECTED || 0}</div>
                        <div className="status-name">Selected</div>
                      </div>
                      <div className="status-card status-rejected-card">
                        <div className="status-count">{dashboardData.candidatesByStatus.REJECTED || 0}</div>
                        <div className="status-name">Rejected</div>
                      </div>
                    </div>
                  </div>
                )}

                <h3 style={{ marginTop: '30px', color: '#667eea' }}>📋 Detailed Candidate Information</h3>
                <div className="table-container">
                  <table className="candidate-table">
                    <thead>
                      <tr>
                        <th>Candidate Name</th>
                        <th>Panelist Name</th>
                        <th>JD Details</th>
                        <th>Interview Date/Time</th>
                        <th>Status</th>
                        <th>Joining Date</th>
                        <th>OLD CTC</th>
                        <th>NEW CTC</th>
                        <th>Employment Type</th>
                        <th>Location</th>
                      </tr>
                    </thead>
                    <tbody>
                      {dashboardData.dashboardRecords && dashboardData.dashboardRecords.length > 0 ? (
                        dashboardData.dashboardRecords.map((record, index) => (
                          <tr key={index}>
                            <td>{record.candidateName || 'N/A'}</td>
                            <td>{record.panelistName || 'Not Assigned'}</td>
                            <td className="jd-cell" title={record.jdDetails}>
                              {record.jdDetails ?
                                (record.jdDetails.length > 50 ?
                                  record.jdDetails.substring(0, 50) + '...' :
                                  record.jdDetails) :
                                'N/A'}
                            </td>
                            <td>
                              {record.interviewDate && record.interviewTime ?
                                `${formatDate(record.interviewDate)} ${record.interviewTime}` :
                                'Not Scheduled'}
                            </td>
                            <td>
                              <span className={`status-badge status-${getCandidateDisplayStatus({
                                email: record.candidateEmail,
                                status: record.status
                              })?.toLowerCase().replace(/_/g, '-')}`}>
                                {getCandidateDisplayStatus({
                                  email: record.candidateEmail,
                                  status: record.status
                                })}
                              </span>
                            </td>
                            <td>{formatDate(record.joiningDate)}</td>
                            <td>{formatCurrency(record.oldCtc)}</td>
                            <td>{formatCurrency(record.newCtc)}</td>
                            <td>{record.employmentType || 'N/A'}</td>
                            <td>{record.location || 'N/A'}</td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan="10" className="no-data">
                            No candidate data available
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Add New Candidate Tab */}
            {activeTab === 'addCandidate' && (
              <div className="add-candidate-section">
                <h2>➕ Add New Candidate</h2>
                <p className="section-description">
                  Create a new candidate profile and login credentials
                </p>

                {formError && (
                  <div className="form-error-message">
                    ❌ {formError}
                  </div>
                )}

                {formSuccess && (
                  <div className="form-success-message">
                    ✅ {formSuccess}
                  </div>
                )}

                <form onSubmit={handleSubmitNewCandidate} className="candidate-form">
                  <div className="form-section">
                    <h3>👤 Personal Information</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="name">Name <span className="required">*</span></label>
                        <input
                          type="text"
                          id="name"
                          name="name"
                          value={newCandidate.name}
                          onChange={handleInputChange}
                          placeholder="Enter candidate name"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="email">Email <span className="required">*</span></label>
                        <input
                          type="email"
                          id="email"
                          name="email"
                          value={newCandidate.email}
                          onChange={handleInputChange}
                          placeholder="candidate@example.com"
                          required
                        />
                        <small style={{ color: '#666', fontSize: '0.85em', marginTop: '4px', display: 'block' }}>
                          📧 OTP will be sent to this email for candidate login
                        </small>
                      </div>

                      <div className="form-group">
                        <label htmlFor="phone">Phone <span className="required">*</span></label>
                        <input
                          type="tel"
                          id="phone"
                          name="phone"
                          value={newCandidate.phone}
                          onChange={handleInputChange}
                          placeholder="Enter phone number"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="position">Position <span className="required">*</span></label>
                        <input
                          type="text"
                          id="position"
                          name="position"
                          value={newCandidate.position}
                          onChange={handleInputChange}
                          placeholder="e.g., Java Developer"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="experienceYears">Experience (Years)</label>
                        <input
                          type="number"
                          id="experienceYears"
                          name="experienceYears"
                          value={newCandidate.experienceYears}
                          onChange={handleInputChange}
                          placeholder="e.g., 5"
                          min="0"
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="skills">Skills</label>
                        <input
                          type="text"
                          id="skills"
                          name="skills"
                          value={newCandidate.skills}
                          onChange={handleInputChange}
                          placeholder="e.g., Java, Spring Boot, React"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-section">
                    <h3>💼 Job Details</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="currentCtc">Current CTC (₹)</label>
                        <input
                          type="number"
                          id="currentCtc"
                          name="currentCtc"
                          value={newCandidate.currentCtc}
                          onChange={handleInputChange}
                          placeholder="e.g., 1200000"
                          min="0"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="employmentType">Employment Type <span className="required">*</span></label>
                        <select
                          id="employmentType"
                          name="employmentType"
                          value={newCandidate.employmentType}
                          onChange={handleInputChange}
                          required
                        >
                          <option value="FULL_TIME">Full Time</option>
                          <option value="PART_TIME">Part Time</option>
                          <option value="CONTRACT">Contract</option>
                          <option value="INTERN">Intern</option>
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor="location">Location</label>
                        <input
                          type="text"
                          id="location"
                          name="location"
                          value={newCandidate.location}
                          onChange={handleInputChange}
                          placeholder="e.g., Bangalore"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="hrMailId">HR Email <span className="required">*</span></label>
                        <input
                          type="email"
                          id="hrMailId"
                          name="hrMailId"
                          value={newCandidate.hrMailId}
                          onChange={handleInputChange}
                          placeholder="HR email address"
                          required
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="jdDetails">Job Description</label>
                        <textarea
                          id="jdDetails"
                          name="jdDetails"
                          value={newCandidate.jdDetails}
                          onChange={handleInputChange}
                          placeholder="Enter job description details"
                          rows="4"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-section">
                    <h3>🔐 Login Credentials</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="username">Username <span className="required">*</span></label>
                        <input
                          type="text"
                          id="username"
                          name="username"
                          value={newCandidate.username}
                          onChange={handleInputChange}
                          placeholder="Enter username for login"
                          required
                          minLength="3"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="password">Password <span className="required">*</span></label>
                        <input
                          type="password"
                          id="password"
                          name="password"
                          value={newCandidate.password}
                          onChange={handleInputChange}
                          placeholder="Enter password (min 8 characters)"
                          required
                          minLength="8"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-section">
                    <h3>👥 Assign Panelists (Optional)</h3>
                    <p className="section-description" style={{ fontSize: '0.9em', color: '#666', marginBottom: '15px' }}>
                      Add panelist email addresses to assign them to this candidate. Names will be auto-populated.
                    </p>
                    
                    <div className="panelist-assignment-container">
                      <div className="form-grid">
                        <div className="form-group">
                          <label htmlFor="panelistEmail">Panelist Email</label>
                          <input
                            type="email"
                            id="panelistEmail"
                            value={panelistEmailInput}
                            onChange={handlePanelistEmailChange}
                            placeholder="Enter panelist email"
                            disabled={searchingPanelist}
                          />
                          {searchingPanelist && (
                            <small style={{ color: '#007bff', fontSize: '0.85em', marginTop: '4px', display: 'block' }}>
                              🔍 Searching...
                            </small>
                          )}
                          {panelistSearchError && (
                            <small style={{ color: '#dc3545', fontSize: '0.85em', marginTop: '4px', display: 'block' }}>
                              ⚠️ {panelistSearchError}
                            </small>
                          )}
                        </div>

                        <div className="form-group">
                          <label htmlFor="panelistName">Panelist Name</label>
                          <input
                            type="text"
                            id="panelistName"
                            value={panelistNameInput}
                            readOnly
                            placeholder="Auto-populated from email"
                            style={{ backgroundColor: '#f8f9fa', cursor: 'not-allowed' }}
                          />
                          <small style={{ color: '#666', fontSize: '0.85em', marginTop: '4px', display: 'block' }}>
                            ✨ Name is automatically filled when you enter a valid email
                          </small>
                        </div>
                      </div>

                      <button
                        type="button"
                        onClick={handleAddPanelist}
                        className="add-panelist-button"
                        disabled={!panelistEmailInput || !panelistNameInput || searchingPanelist}
                        style={{
                          padding: '8px 16px',
                          backgroundColor: '#28a745',
                          color: 'white',
                          border: 'none',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          fontSize: '0.9em',
                          marginTop: '10px',
                          opacity: (!panelistEmailInput || !panelistNameInput || searchingPanelist) ? 0.6 : 1
                        }}
                      >
                        ➕ Add Panelist
                      </button>

                      {assignedPanelists.length > 0 && (
                        <div className="assigned-panelists-list" style={{ marginTop: '20px' }}>
                          <h4 style={{ fontSize: '1em', marginBottom: '10px', color: '#333' }}>
                            📋 Assigned Panelists ({assignedPanelists.length})
                          </h4>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                            {assignedPanelists.map((panelist, index) => (
                              <div
                                key={index}
                                style={{
                                  display: 'flex',
                                  justifyContent: 'space-between',
                                  alignItems: 'center',
                                  padding: '10px',
                                  backgroundColor: '#f8f9fa',
                                  borderRadius: '4px',
                                  border: '1px solid #dee2e6'
                                }}
                              >
                                <div>
                                  <strong>{panelist.name}</strong>
                                  <br />
                                  <small style={{ color: '#666' }}>{panelist.email}</small>
                                </div>
                                <button
                                  type="button"
                                  onClick={() => handleRemovePanelist(panelist.email)}
                                  style={{
                                    padding: '5px 10px',
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    cursor: 'pointer',
                                    fontSize: '0.85em'
                                  }}
                                >
                                  ❌ Remove
                                </button>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="form-actions">
                    <button
                      type="submit"
                      className="submit-button"
                      disabled={submitting}
                    >
                      {submitting ? '⏳ Creating...' : '✅ Create Candidate'}
                    </button>
                    <button
                      type="button"
                      className="cancel-button"
                      onClick={() => {
                        setNewCandidate({
                          name: '',
                          email: '',
                          phone: '',
                          position: '',
                          experienceYears: '',
                          skills: '',
                          currentCtc: '',
                          hrMailId: user.email,
                          jdDetails: '',
                          employmentType: 'FULL_TIME',
                          location: '',
                          username: '',
                          password: ''
                        });
                        setFormError('');
                        setFormSuccess('');
                      }}
                    >
                      🔄 Reset Form
                    </button>
                  </div>
                </form>
              </div>
            )}

            {/* Add Panelist Tab */}
            {activeTab === 'addPanelist' && (
              <div className="add-candidate-section">
                <h2>👨‍💼 Add New Panelist</h2>
                <p className="section-description">
                  Create a new panelist account with complete profile information
                </p>

                {panelistFormError && (
                  <div className="form-error-message">
                    ❌ {panelistFormError}
                  </div>
                )}

                {panelistFormSuccess && (
                  <div className="form-success-message">
                    ✅ {panelistFormSuccess}
                  </div>
                )}

                <form onSubmit={handleSubmitNewPanelist} className="candidate-form">
                  {/* Account Information */}
                  <div className="form-section">
                    <h3>🔐 Account Information</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="panelist-email">Email <span className="required">*</span></label>
                        <input
                          type="email"
                          id="panelist-email"
                          name="email"
                          value={newPanelist.email}
                          onChange={handlePanelistInputChange}
                          placeholder="panelist@company.com"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-username">Username <span className="required">*</span></label>
                        <input
                          type="text"
                          id="panelist-username"
                          name="username"
                          value={newPanelist.username}
                          onChange={handlePanelistInputChange}
                          placeholder="Enter username"
                          required
                          minLength="3"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-password">Password <span className="required">*</span></label>
                        <input
                          type="password"
                          id="panelist-password"
                          name="password"
                          value={newPanelist.password}
                          onChange={handlePanelistInputChange}
                          placeholder="Enter password (min 8 characters)"
                          required
                          minLength="8"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Professional Details */}
                  <div className="form-section">
                    <h3>💼 Professional Details</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="panelist-specialization">Specialization <span className="required">*</span></label>
                        <input
                          type="text"
                          id="panelist-specialization"
                          name="specialization"
                          value={newPanelist.specialization}
                          onChange={handlePanelistInputChange}
                          placeholder="e.g., Java, React, DevOps"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-experienceYears">Experience (Years)</label>
                        <input
                          type="number"
                          id="panelist-experienceYears"
                          name="experienceYears"
                          value={newPanelist.experienceYears}
                          onChange={handlePanelistInputChange}
                          placeholder="Years of experience"
                          min="0"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-designation">Designation</label>
                        <input
                          type="text"
                          id="panelist-designation"
                          name="designation"
                          value={newPanelist.designation}
                          onChange={handlePanelistInputChange}
                          placeholder="e.g., Senior Engineer, Tech Lead"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-company">Company</label>
                        <input
                          type="text"
                          id="panelist-company"
                          name="company"
                          value={newPanelist.company}
                          onChange={handlePanelistInputChange}
                          placeholder="Company name"
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="panelist-expertise">Expertise</label>
                        <textarea
                          id="panelist-expertise"
                          name="expertise"
                          value={newPanelist.expertise}
                          onChange={handlePanelistInputChange}
                          placeholder="Describe areas of expertise"
                          rows="3"
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="panelist-bio">Bio</label>
                        <textarea
                          id="panelist-bio"
                          name="bio"
                          value={newPanelist.bio}
                          onChange={handlePanelistInputChange}
                          placeholder="Professional biography"
                          rows="3"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Contact Information */}
                  <div className="form-section">
                    <h3>📞 Contact Information</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="panelist-phone">Phone</label>
                        <input
                          type="tel"
                          id="panelist-phone"
                          name="phone"
                          value={newPanelist.phone}
                          onChange={handlePanelistInputChange}
                          placeholder="+1234567890"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-location">Location</label>
                        <input
                          type="text"
                          id="panelist-location"
                          name="location"
                          value={newPanelist.location}
                          onChange={handlePanelistInputChange}
                          placeholder="City, Country"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-linkedinUrl">LinkedIn URL</label>
                        <input
                          type="url"
                          id="panelist-linkedinUrl"
                          name="linkedinUrl"
                          value={newPanelist.linkedinUrl}
                          onChange={handlePanelistInputChange}
                          placeholder="https://linkedin.com/in/username"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-slackHandle">Slack Handle</label>
                        <input
                          type="text"
                          id="panelist-slackHandle"
                          name="slackHandle"
                          value={newPanelist.slackHandle}
                          onChange={handlePanelistInputChange}
                          placeholder="@username"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Skills & Education */}
                  <div className="form-section">
                    <h3>🎓 Skills & Education</h3>
                    <div className="form-grid">
                      <div className="form-group full-width">
                        <label htmlFor="panelist-skills">Skills</label>
                        <textarea
                          id="panelist-skills"
                          name="skills"
                          value={newPanelist.skills}
                          onChange={handlePanelistInputChange}
                          placeholder="List technical skills (comma-separated)"
                          rows="2"
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="panelist-certifications">Certifications</label>
                        <textarea
                          id="panelist-certifications"
                          name="certifications"
                          value={newPanelist.certifications}
                          onChange={handlePanelistInputChange}
                          placeholder="Professional certifications"
                          rows="2"
                        />
                      </div>

                      <div className="form-group full-width">
                        <label htmlFor="panelist-education">Education</label>
                        <textarea
                          id="panelist-education"
                          name="education"
                          value={newPanelist.education}
                          onChange={handlePanelistInputChange}
                          placeholder="Educational background"
                          rows="2"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Organization Details */}
                  <div className="form-section">
                    <h3>🏢 Organization Details</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="panelist-department">Department</label>
                        <input
                          type="text"
                          id="panelist-department"
                          name="department"
                          value={newPanelist.department}
                          onChange={handlePanelistInputChange}
                          placeholder="e.g., Engineering, IT"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-employeeId">Employee ID</label>
                        <input
                          type="text"
                          id="panelist-employeeId"
                          name="employeeId"
                          value={newPanelist.employeeId}
                          onChange={handlePanelistInputChange}
                          placeholder="Employee ID"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-workType">Work Type</label>
                        <select
                          id="panelist-workType"
                          name="workType"
                          value={newPanelist.workType}
                          onChange={handlePanelistInputChange}
                        >
                          <option value="On-site">On-site</option>
                          <option value="Remote">Remote</option>
                          <option value="Hybrid">Hybrid</option>
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-teamName">Team Name</label>
                        <input
                          type="text"
                          id="panelist-teamName"
                          name="teamName"
                          value={newPanelist.teamName}
                          onChange={handlePanelistInputChange}
                          placeholder="Team name"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="panelist-reportingManager">Reporting Manager</label>
                        <input
                          type="text"
                          id="panelist-reportingManager"
                          name="reportingManager"
                          value={newPanelist.reportingManager}
                          onChange={handlePanelistInputChange}
                          placeholder="Manager name"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-actions">
                    <button
                      type="submit"
                      className="submit-button"
                      disabled={submittingPanelist}
                    >
                      {submittingPanelist ? '⏳ Creating...' : '✅ Create Panelist'}
                    </button>
                    <button
                      type="button"
                      className="cancel-button"
                      onClick={() => {
                        setNewPanelist({
                          email: '',
                          username: '',
                          password: '',
                          specialization: '',
                          experienceYears: '',
                          expertise: '',
                          phone: '',
                          location: '',
                          linkedinUrl: '',
                          slackHandle: '',
                          designation: '',
                          company: '',
                          bio: '',
                          skills: '',
                          certifications: '',
                          education: '',
                          department: '',
                          employeeId: '',
                          workType: 'On-site',
                          teamName: '',
                          reportingManager: ''
                        });
                        setPanelistFormError('');
                        setPanelistFormSuccess('');
                      }}
                    >
                      🔄 Reset Form
                    </button>
                  </div>
                </form>
              </div>
            )}

            {/* Manage Candidates Tab */}
            {activeTab === 'manageCandidates' && (
              <div className="manage-candidates-section">
                <h2>📝 Manage Candidates</h2>
                <p className="section-description">
                  View and edit all candidates you have created ({myCandidates.length} total)
                </p>

                {editingCandidate ? (
                  <div className="edit-candidate-form">
                    <div className="form-header">
                      <h3>✏️ Edit Candidate: {editingCandidate.name}</h3>
                      <button onClick={handleCancelEdit} className="close-button">✕</button>
                    </div>

                    {editError && (
                      <div className="form-error-message">
                        ❌ {editError}
                      </div>
                    )}

                    {editSuccess && (
                      <div className="form-success-message">
                        ✅ {editSuccess}
                      </div>
                    )}

                    <form onSubmit={handleUpdateCandidate} className="candidate-form">
                      <div className="form-section">
                        <h4>👤 Personal Information</h4>
                        <div className="form-grid">
                          <div className="form-group">
                            <label htmlFor="edit-name">Name <span className="required">*</span></label>
                            <input
                              type="text"
                              id="edit-name"
                              name="name"
                              value={editFormData.name}
                              onChange={handleEditInputChange}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-phone">Phone <span className="required">*</span></label>
                            <input
                              type="tel"
                              id="edit-phone"
                              name="phone"
                              value={editFormData.phone}
                              onChange={handleEditInputChange}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-position">Position <span className="required">*</span></label>
                            <input
                              type="text"
                              id="edit-position"
                              name="position"
                              value={editFormData.position}
                              onChange={handleEditInputChange}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-status">Status</label>
                            <select
                              id="edit-status"
                              name="status"
                              value={editFormData.status}
                              onChange={handleEditInputChange}
                            >
                              <option value="APPLIED">Applied</option>
                              <option value="SCREENING">Screening</option>
                              <option value="INTERVIEW">Interview</option>
                              <option value="SELECTED">Selected</option>
                              <option value="REJECTED">Rejected</option>
                            </select>
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-experienceYears">Experience (Years)</label>
                            <input
                              type="number"
                              id="edit-experienceYears"
                              name="experienceYears"
                              value={editFormData.experienceYears}
                              onChange={handleEditInputChange}
                              min="0"
                            />
                          </div>

                          <div className="form-group full-width">
                            <label htmlFor="edit-skills">Skills</label>
                            <input
                              type="text"
                              id="edit-skills"
                              name="skills"
                              value={editFormData.skills}
                              onChange={handleEditInputChange}
                            />
                          </div>
                        </div>
                      </div>

                      <div className="form-section">
                        <h4>💼 Job Details</h4>
                        <div className="form-grid">
                          <div className="form-group">
                            <label htmlFor="edit-currentCtc">Current CTC (₹)</label>
                            <input
                              type="number"
                              id="edit-currentCtc"
                              name="currentCtc"
                              value={editFormData.currentCtc}
                              onChange={handleEditInputChange}
                              min="0"
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-employmentType">Employment Type</label>
                            <select
                              id="edit-employmentType"
                              name="employmentType"
                              value={editFormData.employmentType}
                              onChange={handleEditInputChange}
                            >
                              <option value="FULL_TIME">Full Time</option>
                              <option value="PART_TIME">Part Time</option>
                              <option value="CONTRACT">Contract</option>
                              <option value="INTERN">Intern</option>
                            </select>
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-location">Location</label>
                            <input
                              type="text"
                              id="edit-location"
                              name="location"
                              value={editFormData.location}
                              onChange={handleEditInputChange}
                            />
                          </div>

                          <div className="form-group full-width">
                            <label htmlFor="edit-jdDetails">Job Description</label>
                            <textarea
                              id="edit-jdDetails"
                              name="jdDetails"
                              value={editFormData.jdDetails}
                              onChange={handleEditInputChange}
                              rows="4"
                            />
                          </div>
                        </div>
                      </div>

                      <div className="form-actions">
                        <button
                          type="submit"
                          className="submit-button"
                          disabled={updating}
                        >
                          {updating ? '⏳ Updating...' : '✅ Update Candidate'}
                        </button>
                        <button
                          type="button"
                          className="cancel-button"
                          onClick={handleCancelEdit}
                        >
                          ❌ Cancel
                        </button>
                      </div>
                    </form>
                  </div>
                ) : schedulingInterview ? (
                  <div className="schedule-interview-form">
                    <div className="form-header">
                      <h3>📅 Schedule Interview: {schedulingInterview.name}</h3>
                      <button onClick={handleCancelScheduleInterview} className="close-button">✕</button>
                    </div>

                    {interviewFormError && (
                      <div className="form-error-message">
                        ❌ {interviewFormError}
                      </div>
                    )}

                    {interviewFormSuccess && (
                      <div className="form-success-message">
                        ✅ {interviewFormSuccess}
                      </div>
                    )}

                    <form onSubmit={handleSubmitInterview} className="candidate-form">
                      <div className="form-section">
                        <h4>📋 Interview Details</h4>
                        <div className="form-grid">
                          <div className="form-group">
                            <label htmlFor="candidate-name-display">Candidate Name</label>
                            <input
                              type="text"
                              id="candidate-name-display"
                              value={schedulingInterview.name}
                              disabled
                              style={{ backgroundColor: '#f8f9fa', cursor: 'not-allowed' }}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="candidate-email-display">Candidate Email</label>
                            <input
                              type="text"
                              id="candidate-email-display"
                              value={schedulingInterview.email}
                              disabled
                              style={{ backgroundColor: '#f8f9fa', cursor: 'not-allowed' }}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="position-display">Position</label>
                            <input
                              type="text"
                              id="position-display"
                              value={schedulingInterview.position}
                              disabled
                              style={{ backgroundColor: '#f8f9fa', cursor: 'not-allowed' }}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="panelistEmail">Panelist Email <span className="required">*</span></label>
                            <input
                              type="email"
                              id="panelistEmail"
                              name="panelistEmail"
                              value={interviewFormData.panelistEmail}
                              onChange={handleInterviewInputChange}
                              placeholder="panelist@example.com"
                              required
                            />
                            <small style={{ color: '#666', fontSize: '0.85em', marginTop: '4px', display: 'block' }}>
                              Enter the email of the panelist who will conduct the interview
                            </small>
                          </div>

                          <div className="form-group">
                            <label htmlFor="interviewDate">Interview Date <span className="required">*</span></label>
                            <input
                              type="date"
                              id="interviewDate"
                              name="interviewDate"
                              value={interviewFormData.interviewDate}
                              onChange={handleInterviewInputChange}
                              min={new Date().toISOString().split('T')[0]}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="interviewTimeFrom">Interview Time From <span className="required">*</span></label>
                            <input
                              type="time"
                              id="interviewTimeFrom"
                              name="interviewTimeFrom"
                              value={interviewFormData.interviewTimeFrom}
                              onChange={handleInterviewInputChange}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="interviewTimeTo">Interview Time To <span className="required">*</span></label>
                            <input
                              type="time"
                              id="interviewTimeTo"
                              name="interviewTimeTo"
                              value={interviewFormData.interviewTimeTo}
                              onChange={handleInterviewInputChange}
                              required
                            />
                          </div>

                          <div className="form-group full-width">
                            <label htmlFor="notes">Notes (Optional)</label>
                            <textarea
                              id="notes"
                              name="notes"
                              value={interviewFormData.notes}
                              onChange={handleInterviewInputChange}
                              placeholder="Add any additional notes or instructions for the interview..."
                              rows="4"
                            />
                          </div>
                        </div>
                      </div>

                      <div className="form-actions">
                        <button
                          type="submit"
                          className="submit-button"
                          disabled={submittingInterview}
                        >
                          {submittingInterview ? '⏳ Scheduling...' : '✅ Schedule Interview'}
                        </button>
                        <button
                          type="button"
                          className="cancel-button"
                          onClick={handleCancelScheduleInterview}
                        >
                          ❌ Cancel
                        </button>
                      </div>
                    </form>
                  </div>
                ) : (
                  <div className="candidates-list">
                    {myCandidates && myCandidates.length > 0 ? (
                      <div className="table-container">
                        <table className="candidate-table">
                          <thead>
                            <tr>
                              <th>Name</th>
                              <th>Email</th>
                              <th>Phone</th>
                              <th>Position</th>
                              <th>Status</th>
                              <th>Experience</th>
                              <th>Location</th>
                              <th>Interview</th>
                              <th>Actions</th>
                            </tr>
                          </thead>
                          <tbody>
                            {myCandidates.map((candidate) => {
                              const interviews = candidateInterviews[candidate.email] || [];
                              const latestInterview = interviews.length > 0 ? interviews[0] : null;
                              
                              return (
                              <tr key={candidate.id}>
                                <td>{candidate.name}</td>
                                <td>{candidate.email}</td>
                                <td>{candidate.phone}</td>
                                <td>{candidate.position}</td>
                                <td>
                                  <span className={`status-badge status-${candidate.status?.toLowerCase()}`}>
                                    {candidate.status}
                                  </span>
                                </td>
                                <td>{candidate.experienceYears ? `${candidate.experienceYears} years` : 'N/A'}</td>
                                <td>{candidate.location || 'N/A'}</td>
                                <td className="interview-cell">
                                  {latestInterview && (
                                    <div className="interview-info">
                                      <span className={`interview-status-badge interview-${latestInterview.status?.toLowerCase()}`}>
                                        {latestInterview.status}
                                      </span>
                                      <div className="interview-details">
                                        <small>📅 {latestInterview.interviewDate}</small>
                                        <small>🕐 {latestInterview.interviewTimeFrom} - {latestInterview.interviewTimeTo}</small>
                                      </div>
                                      {interviews.length > 1 && (
                                        <small className="interview-count">+{interviews.length - 1} more</small>
                                      )}
                                    </div>
                                  )}
                                  <button
                                    onClick={() => handleScheduleInterview(candidate)}
                                    className="schedule-button"
                                    title="Schedule interview"
                                    style={{
                                      backgroundColor: '#28a745',
                                      color: 'white',
                                      border: 'none',
                                      padding: '6px 12px',
                                      borderRadius: '4px',
                                      cursor: 'pointer',
                                      fontSize: '0.85em',
                                      marginTop: latestInterview ? '8px' : '0'
                                    }}
                                  >
                                    📅 Schedule
                                  </button>
                                </td>
                                <td>
                                  <button
                                    onClick={() => handleEditCandidate(candidate)}
                                    className="edit-button"
                                    title="Edit candidate"
                                  >
                                    ✏️ Edit
                                  </button>
                                  <button
                                    onClick={() => handleDeleteCandidate(candidate.id, candidate.name)}
                                    className="delete-button"
                                    title="Delete candidate"
                                    style={{ marginLeft: '8px' }}
                                  >
                                   🗑️ Delete
                                 </button>
                               </td>
                             </tr>
                           );
                           })}
                          </tbody>
                        </table>
                      </div>
                    ) : (
                      <div className="no-candidates-message">
                        <p>📭 No candidates created yet</p>
                        <p className="hint-text">Click "Add New Candidate" to create your first candidate</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}

            {/* Manage Panelists Tab */}
            {activeTab === 'managePanelists' && (
              <div className="manage-panelists-section">
                <h2>👥 Manage Panelists</h2>
                <p className="section-description">
                  View and edit all panelists you have created ({myPanelists.length} total)
                </p>

                {editingPanelist ? (
                  <div className="edit-panelist-form">
                    <div className="form-header">
                      <h3>✏️ Edit Panelist: {editingPanelist.user?.username || 'Unknown'}</h3>
                      <button onClick={handleCancelEditPanelist} className="close-button">✕</button>
                    </div>

                    {editPanelistError && (
                      <div className="form-error-message">
                        ❌ {editPanelistError}
                      </div>
                    )}

                    {editPanelistSuccess && (
                      <div className="form-success-message">
                        ✅ {editPanelistSuccess}
                      </div>
                    )}

                    <form onSubmit={handleUpdatePanelist} className="candidate-form">
                      <div className="form-section">
                        <h4>👨‍💼 Professional Information</h4>
                        <div className="form-grid">
                          <div className="form-group">
                            <label htmlFor="edit-panelist-specialization">Specialization <span className="required">*</span></label>
                            <input
                              type="text"
                              id="edit-panelist-specialization"
                              name="specialization"
                              value={editPanelistFormData.specialization}
                              onChange={handleEditPanelistInputChange}
                              placeholder="e.g., Java, Python, Frontend"
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-panelist-experienceYears">Experience (Years)</label>
                            <input
                              type="number"
                              id="edit-panelist-experienceYears"
                              name="experienceYears"
                              value={editPanelistFormData.experienceYears}
                              onChange={handleEditPanelistInputChange}
                              placeholder="Years of experience"
                              min="0"
                            />
                          </div>

                          <div className="form-group full-width">
                            <label htmlFor="edit-panelist-expertise">Expertise</label>
                            <textarea
                              id="edit-panelist-expertise"
                              name="expertise"
                              value={editPanelistFormData.expertise}
                              onChange={handleEditPanelistInputChange}
                              placeholder="Describe areas of expertise"
                              rows="4"
                            />
                          </div>
                        </div>
                      </div>

                      <div className="form-actions">
                        <button
                          type="submit"
                          className="submit-button"
                          disabled={updatingPanelist}
                        >
                          {updatingPanelist ? '⏳ Updating...' : '✅ Update Panelist'}
                        </button>
                        <button
                          type="button"
                          className="cancel-button"
                          onClick={handleCancelEditPanelist}
                        >
                          ❌ Cancel
                        </button>
                      </div>
                    </form>
                  </div>
                ) : (
                  <div className="panelists-list">
                    {myPanelists && myPanelists.length > 0 ? (
                      <div className="table-container">
                        <table className="candidate-table">
                          <thead>
                            <tr>
                              <th>Name</th>
                              <th>Email</th>
                              <th>Specialization</th>
                              <th>Experience</th>
                              <th>Expertise</th>
                              <th>Actions</th>
                            </tr>
                          </thead>
                          <tbody>
                            {myPanelists.map((panelist) => (
                              <tr key={panelist.id}>
                                <td>{panelist.user?.username || 'N/A'}</td>
                                <td>{panelist.user?.email || 'N/A'}</td>
                                <td>{panelist.specialization || 'N/A'}</td>
                                <td>{panelist.experienceYears ? `${panelist.experienceYears} years` : 'N/A'}</td>
                                <td className="jd-cell" title={panelist.expertise}>
                                  {panelist.expertise ?
                                    (panelist.expertise.length > 50 ?
                                      panelist.expertise.substring(0, 50) + '...' :
                                      panelist.expertise) :
                                    'N/A'}
                                </td>
                                <td>
                                  <button
                                    onClick={() => handleEditPanelist(panelist)}
                                    className="edit-button"
                                    title="Edit panelist"
                                  >
                                    ✏️ Edit
                                  </button>
                                  <button
                                    onClick={() => handleDeletePanelist(panelist.id, panelist.user?.username || 'this panelist')}
                                    className="delete-button"
                                    title="Delete panelist"
                                    style={{ marginLeft: '8px' }}
                                  >
                                    🗑️ Delete
                                  </button>
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ) : (
                      <div className="no-candidates-message">
                        <p>📭 No panelists created yet</p>
                        <p className="hint-text">Click "Add New Panelist" to create your first panelist</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}

          </>
        )}
      </div>
    </div>
  );
};

export default HRDashboard;

// Made with Bob