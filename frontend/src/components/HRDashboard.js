import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import { apiGet, apiPost, apiPut, apiDelete, fetchWithRetry } from '../utils/apiHelper';
import './HRDashboard.css';

/**
 * HR Dashboard Component - Enhanced with HR Profile and Candidate Data Table
 */
const HRDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('home');
  const [dashboardData, setDashboardData] = useState(null);
  const [enhancedStats, setEnhancedStats] = useState(null);
  const [myCandidates, setMyCandidates] = useState([]);
  const [filteredCandidates, setFilteredCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [retryCount, setRetryCount] = useState(0);
  const [selectedGraphFilter, setSelectedGraphFilter] = useState(null);
  
  // Search and Filter state
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [positionFilter, setPositionFilter] = useState('');
  const [locationFilter, setLocationFilter] = useState('');
  const [searching, setSearching] = useState(false);
  
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
    jrs: '',
    candidateType: 'EXTERNAL',
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
  
  // All interviews state for Interviews tab
  const [allInterviews, setAllInterviews] = useState([]);
  const [filteredInterviews, setFilteredInterviews] = useState([]);
  const [interviewStatusFilter, setInterviewStatusFilter] = useState('ALL');
  const [loadingInterviews, setLoadingInterviews] = useState(false);
  const [lastInterviewUpdate, setLastInterviewUpdate] = useState(null);

  // Candidate Feedback state
  const [allFeedbacks, setAllFeedbacks] = useState([]);
  const [loadingFeedbacks, setLoadingFeedbacks] = useState(false);
  const [feedbackError, setFeedbackError] = useState('');

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
    fetchEnhancedStats();
    fetchMyCandidates();
    fetchMyPanelists();
    fetchHRProfile();
    fetchAllInterviews(); // Fetch interviews for home tab statistics
  }, []);
  
  useEffect(() => {
    if (activeTab === 'interviews') {
      fetchAllInterviews();
    } else if (activeTab === 'candidateFeedback') {
      fetchAllFeedbacks();
    }
  }, [activeTab]);
  
  // Auto-refresh interviews every 60 seconds when on interviews tab
  useEffect(() => {
    let intervalId;
    
    if (activeTab === 'interviews') {
      // Set up interval to refresh interviews every 60 seconds
      intervalId = setInterval(() => {
        console.log('Auto-refreshing interviews...');
        fetchAllInterviews();
      }, 60000); // 60 seconds
    }
    
    // Cleanup interval on unmount or when tab changes
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [activeTab]);
  
  useEffect(() => {
    // Filter interviews based on status
    if (interviewStatusFilter === 'ALL') {
      setFilteredInterviews(allInterviews);
    } else {
      setFilteredInterviews(allInterviews.filter(interview => interview.status === interviewStatusFilter));
    }
  }, [allInterviews, interviewStatusFilter]);

  const fetchAllFeedbacks = async () => {
    try {
      setLoadingFeedbacks(true);
      setFeedbackError('');

      console.log('Fetching all candidate feedbacks...');

      const result = await apiGet('/api/interview-feedback/all');

      if (result.ok && result.data.success) {
        console.log('Feedbacks received:', result.data);
        setAllFeedbacks(result.data.feedbackList || []);
      } else {
        const errorParts = [
          result.data.message || 'Failed to fetch feedbacks',
          result.data.error ? `Error: ${result.data.error}` : '',
          result.data.details ? `Details: ${result.data.details}` : ''
        ].filter(Boolean);
        setFeedbackError(errorParts.join(' | '));
      }
    } catch (err) {
      console.error('Error fetching feedbacks:', err);
      setFeedbackError('Error fetching feedbacks: ' + err.message);
    } finally {
      setLoadingFeedbacks(false);
    }
  };

  const handleDownloadFeedbackPdf = async (feedbackId, candidateName) => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        alert('No authentication token found');
        return;
      }

      console.log('Downloading feedback PDF for ID:', feedbackId);

      const response = await fetchWithRetry(
        `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/interview-feedback/${feedbackId}/download-pdf`,
        {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
          credentials: 'include'
        }
      );

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `interview-feedback-${candidateName || feedbackId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        console.log('PDF downloaded successfully');
      } else {
        alert('Failed to download PDF');
      }
    } catch (err) {
      console.error('Error downloading PDF:', err);
      alert('Error downloading PDF: ' + err.message);
    }
  };

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      console.log('Fetching HR dashboard data...');
      console.log('User ID:', user.id);

      const result = await apiGet(`/api/hr/${user.id}/dashboard`);

      console.log('Response status:', result.status);
      console.log('Response ok:', result.ok);

      if (result.ok && result.data.success) {
        setDashboardData(result.data.dashboard);
        setError(null);
        setRetryCount(0);
        console.log('Dashboard loaded successfully');
        console.log('Total Candidates:', result.data.dashboard.totalCandidates);
        console.log('Total Panelists:', result.data.dashboard.totalPanelists);
      } else {
        throw new Error(result.data.message || 'Failed to load dashboard');
      }
    } catch (err) {
      console.error('Error fetching dashboard:', err);
      // Don't set error - just log it and continue with empty data
      console.warn('Dashboard fetch failed, continuing with empty data');
      setDashboardData({
        totalCandidates: 0,
        totalPanelists: 0,
        totalInterviews: 0,
        pendingInterviews: 0
      });
    } finally {
      setLoading(false);
    }
  };

  const fetchEnhancedStats = async () => {
    try {
      console.log('Fetching enhanced dashboard statistics...');
      
      const result = await apiGet(`/api/hr/${user.id}/enhanced-stats`);
      
      if (result.ok && result.data.success) {
        setEnhancedStats(result.data.stats);
        console.log('Enhanced stats loaded successfully');
      } else {
        console.warn('Failed to fetch enhanced stats');
      }
    } catch (err) {
      console.error('Error fetching enhanced stats:', err);
    }
  };

  // Handle graph bar click for filtering
  const handleGraphBarClick = (filterType, filterValue) => {
    console.log('Graph bar clicked:', filterType, filterValue);
    
    if (filterType === 'candidateStatus') {
      // Filter candidates by status
      setStatusFilter(filterValue);
      setSelectedGraphFilter({ type: 'candidateStatus', value: filterValue });
      
      // Switch to manage candidates tab to show filtered results
      setActiveTab('manageCandidates');
    } else if (filterType === 'panelistExperience') {
      // Filter panelists by experience range
      setSelectedGraphFilter({ type: 'panelistExperience', value: filterValue });
      
      // Switch to manage panelists tab
      setActiveTab('managePanelists');
    }
  };

  // Clear graph filter
  const clearGraphFilter = () => {
    setSelectedGraphFilter(null);
    setStatusFilter('ALL');
  };

  // Get trend icon based on trend direction
  const getTrendIcon = (trend) => {
    if (trend === 'UP') return '↑';
    if (trend === 'DOWN') return '↓';
    return '→';
  };

  // Get trend color class
  const getTrendClass = (trend) => {
    if (trend === 'UP') return 'trend-up';
    if (trend === 'DOWN') return 'trend-down';
    return 'trend-stable';
  };

  const fetchMyCandidates = async () => {
    try {
      console.log('Fetching my candidates...');

      const result = await apiGet(`/api/hr/${user.id}/my-candidates`);

      if (result.ok && result.data.success) {
        console.log('My candidates received:', result.data);
        const candidates = result.data.candidates || [];
        setMyCandidates(candidates);
        
        // Fetch interviews for each candidate
        fetchInterviewsForCandidates(candidates);
      } else {
        console.warn('Failed to fetch candidates, using empty list');
        setMyCandidates([]);
      }
    } catch (err) {
      console.error('Error fetching my candidates:', err);
      console.warn('Continuing with empty candidates list');
      setMyCandidates([]);
    }
  };

  const fetchInterviewsForCandidates = async (candidates) => {
    try {
      if (!candidates || candidates.length === 0) {
        return;
      }

      console.log('Fetching interviews for candidates...');

      // Fetch interviews for each candidate
      const interviewPromises = candidates.map(async (candidate) => {
        try {
          const result = await apiGet(`/api/interviews/candidate/${encodeURIComponent(candidate.email)}`);
          
          if (result.ok && result.data.success && result.data.interviews) {
            return { email: candidate.email, interviews: result.data.interviews };
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
      console.log('Fetching my panelists...');

      const result = await apiGet(`/api/panelists/hr/${user.id}`);

      if (result.ok && result.data.success) {
        console.log('My panelists received:', result.data);
        setMyPanelists(result.data.panelists || []);
      } else {
        console.warn('Failed to fetch panelists, using empty list');
        setMyPanelists([]);
      }
    } catch (err) {
      console.error('Error fetching my panelists:', err);
      console.warn('Continuing with empty panelists list');
      setMyPanelists([]);
    }
  };

  // Search and Filter Functions
  const handleSearch = async () => {
    if (!searchTerm && statusFilter === 'ALL' && !positionFilter && !locationFilter) {
      // No filters applied, show all candidates
      setFilteredCandidates(myCandidates);
      return;
    }

    try {
      setSearching(true);
      console.log('Searching candidates with filters:', {
        searchTerm,
        statusFilter,
        positionFilter,
        locationFilter
      });

      const params = new URLSearchParams();
      if (searchTerm) params.append('searchTerm', searchTerm);
      if (statusFilter && statusFilter !== 'ALL') params.append('status', statusFilter);
      if (positionFilter) params.append('position', positionFilter);
      if (locationFilter) params.append('location', locationFilter);

      const result = await apiGet(`/api/candidates/advanced-search/hr/${user.id}?${params.toString()}`);

      if (result.ok && result.data.success) {
        console.log('Search results:', result.data.candidates);
        setFilteredCandidates(result.data.candidates || []);
      } else {
        console.error('Search failed:', result.data.message);
        setFilteredCandidates([]);
      }
    } catch (err) {
      console.error('Error searching candidates:', err);
      setFilteredCandidates(myCandidates);
    } finally {
      setSearching(false);
    }
  };

  const handleClearFilters = () => {
    setSearchTerm('');
    setStatusFilter('ALL');
    setPositionFilter('');
    setLocationFilter('');
    setFilteredCandidates(myCandidates);
  };

  const handleQuickJump = (candidateId) => {
    const element = document.getElementById(`candidate-row-${candidateId}`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'center' });
      element.classList.add('highlight-row');
      setTimeout(() => {
        element.classList.remove('highlight-row');
      }, 2000);
    }
  };

  // Update filtered candidates when myCandidates changes
  useEffect(() => {
    setFilteredCandidates(myCandidates);
  }, [myCandidates]);

  // Trigger search when filters change
  useEffect(() => {
    const debounceTimer = setTimeout(() => {
      handleSearch();
    }, 500);

    return () => clearTimeout(debounceTimer);
  }, [searchTerm, statusFilter, positionFilter, locationFilter]);

  const fetchAllInterviews = async () => {
    try {
      setLoadingInterviews(true);
      setError(null);

      console.log('Fetching all interviews for HR...');

      const result = await apiGet(`/api/interviews/hr/${user.id}`);

      if (result.ok && result.data.success) {
        console.log('All interviews received:', result.data);
        setAllInterviews(result.data.interviews || []);
        setLastInterviewUpdate(new Date());
      } else {
        setError(result.data.message || 'Failed to fetch interviews');
      }
    } catch (err) {
      console.error('Error fetching all interviews:', err);
      setError('Error fetching interviews: ' + err.message);
    } finally {
      setLoadingInterviews(false);
    }
  };
  
  const handleManualRefreshInterviews = async () => {
    console.log('Manual refresh triggered');
    await fetchAllInterviews();
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
        const result = await apiGet(`/api/hr/${user.id}/search-panelist?email=${encodeURIComponent(email)}`);

        if (result.ok && result.data.success && result.data.panelist) {
          // Auto-populate the name
          setPanelistNameInput(result.data.panelist.username);
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

      // Validate required fields
      if (!newPanelist.specialization || newPanelist.specialization.trim() === '') {
        throw new Error('Specialization is required');
      }

      console.log('=== Creating Panelist ===');
      console.log('Step 1: Creating user account...');

      // First, create the user account with PANELIST role
      const userResult = await apiPost('/api/auth/register', {
        username: newPanelist.username,
        email: newPanelist.email,
        password: newPanelist.password,
        role: 'PANELIST'
      });

      if (!userResult.ok) {
        console.error('User creation failed:', userResult.data);
        throw new Error(userResult.data.error || userResult.data.message || 'Failed to create panelist user account');
      }

      const userId = userResult.data.id;
      console.log('User created successfully with ID:', userId);

      if (!userId) {
        throw new Error('Panelist user account created, but user ID was not returned by the server');
      }

      console.log('Step 2: Creating panelist profile...');
      console.log('Payload:', {
        userId: userId,
        hrId: user.id,
        specialization: newPanelist.specialization.trim(),
        experienceYears: newPanelist.experienceYears ? parseInt(newPanelist.experienceYears) : null,
        expertise: newPanelist.expertise || null
      });

      // Then, create the panelist profile
      const panelistResult = await apiPost('/api/panelists/create', {
        userId: userId,
        hrId: user.id,
        specialization: newPanelist.specialization.trim(),
        experienceYears: newPanelist.experienceYears ? parseInt(newPanelist.experienceYears) : null,
        expertise: newPanelist.expertise || null
      });

      if (!panelistResult.ok) {
        console.error('Panelist profile creation failed:', panelistResult.data);
        throw new Error(panelistResult.data.message || 'Failed to create panelist profile');
      }

      console.log('Panelist profile created:', panelistResult.data);

      if (!panelistResult.data.panelist || !panelistResult.data.panelist.id) {
        throw new Error('Panelist profile created but ID was not returned');
      }

      const panelistId = panelistResult.data.panelist.id;
      console.log('Step 3: Updating additional profile fields...');

      // Update panelist profile with additional fields
      const updateResult = await apiPut(`/api/panelists/profile/${userId}`, {
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
      });

      if (!updateResult.ok) {
        throw new Error(updateResult.data.message || 'Panelist created but failed to update profile details');
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

      const result = await apiPost(`/api/hr/${user.id}/create-candidate`, newCandidate);

      if (!result.ok) {
        throw new Error(result.data.message || 'Failed to create candidate');
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
      status: candidate.status || 'APPLIED',
      jrs: candidate.jrs || '',
      candidateType: candidate.candidateType || 'EXTERNAL'
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
      const result = await apiPut(`/api/hr/${user.id}/update-candidate/${editingCandidate.id}`, editFormData);

      if (!result.ok) {
        throw new Error(result.data?.message || 'Failed to update candidate');
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
      const result = await apiDelete(`/api/hr/${user.id}/delete-candidate/${candidateId}`);

      if (!result.ok) {
        throw new Error(result.data?.message || 'Failed to delete candidate');
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

      const result = await apiPost(`/api/hr/${user.id}/schedule-interview`, {
        candidateId: schedulingInterview.id,
        panelistEmail: interviewFormData.panelistEmail,
        interviewDate: interviewFormData.interviewDate,
        interviewTimeFrom: interviewFormData.interviewTimeFrom,
        interviewTimeTo: interviewFormData.interviewTimeTo,
        notes: interviewFormData.notes
      });

      if (!result.ok) {
        throw new Error(result.data?.message || 'Failed to schedule interview');
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
      const result = await apiPut(`/api/panelists/${editingPanelist.id}`, {
        ...editPanelistFormData,
        hrId: user.id
      });

      if (!result.ok) {
        throw new Error(result.data?.message || 'Failed to update panelist');
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
      const result = await apiDelete(`/api/panelists/${panelistId}?hrId=${user.id}`);

      if (!result.ok) {
        throw new Error(result.data?.message || 'Failed to delete panelist');
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
      setProfileError('');

      console.log('Fetching HR profile...');

      const result = await apiGet(`/api/hr/${user.id}/profile`);

      if (result.ok && result.data.success && result.data.profile) {
        console.log('HR profile received:', result.data);
        setHrProfile(result.data.profile);
        setProfileFormData({
          fullName: result.data.profile.fullName || '',
          phone: result.data.profile.phone || '',
          location: result.data.profile.location || '',
          address: result.data.profile.address || '',
          designation: result.data.profile.designation || '',
          department: result.data.profile.department || '',
          employeeId: result.data.profile.employeeId || '',
          experienceYears: result.data.profile.experienceYears || '',
          company: result.data.profile.company || '',
          bio: result.data.profile.bio || '',
          linkedinUrl: result.data.profile.linkedinUrl || '',
          slackHandle: result.data.profile.slackHandle || '',
          emergencyContact: result.data.profile.emergencyContact || '',
          emergencyPhone: result.data.profile.emergencyPhone || '',
          skills: result.data.profile.skills || '',
          certifications: result.data.profile.certifications || '',
          education: result.data.profile.education || '',
          workType: result.data.profile.workType || 'On-site',
          teamName: result.data.profile.teamName || '',
          reportingManager: result.data.profile.reportingManager || '',
          hrSpecialization: result.data.profile.hrSpecialization || '',
          region: result.data.profile.region || ''
        });
      } else {
        setProfileError(result.data.message || 'Failed to fetch HR profile');
      }
    } catch (err) {
      console.error('Error fetching HR profile:', err);
      setProfileError('Error fetching HR profile: ' + err.message);
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
      console.log('Saving HR profile...');

      const result = await apiPost(`/api/hr/${user.id}/profile`, profileFormData);

      if (result.ok && result.data.success) {
        setProfileSuccess('Profile saved successfully!');
        setHrProfile(result.data.profile);
        setTimeout(() => setProfileSuccess(''), 3000);
      } else {
        throw new Error(result.data.message || 'Failed to save profile');
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

  const candidateStatusConfig = [
    { key: 'APPLIED', label: 'Applied', colorClass: 'bar-applied' },
    { key: 'SCREENING', label: 'Screening', colorClass: 'bar-screening' },
    { key: 'INTERVIEW', label: 'Interview', colorClass: 'bar-interview' },
    { key: 'INTERVIEW_COMPLETED', label: 'Completed', colorClass: 'bar-completed' },
    { key: 'SELECTED', label: 'Selected', colorClass: 'bar-selected' },
    { key: 'REJECTED', label: 'Rejected', colorClass: 'bar-rejected' }
  ];

  const candidateStatusCounts = myCandidates.reduce((acc, candidate) => {
    const status = getCandidateDisplayStatus(candidate);
    acc[status] = (acc[status] || 0) + 1;
    return acc;
  }, {});

  const candidateGraphData = candidateStatusConfig.map((status) => ({
    ...status,
    count: candidateStatusCounts[status.key] || 0
  }));

  const maxCandidateCount = Math.max(...candidateGraphData.map((item) => item.count), 1);

  const panelistExperienceConfig = [
    { key: '0-2', label: '0-2 Yrs', min: 0, max: 2, colorClass: 'bar-junior' },
    { key: '3-5', label: '3-5 Yrs', min: 3, max: 5, colorClass: 'bar-mid' },
    { key: '6-9', label: '6-9 Yrs', min: 6, max: 9, colorClass: 'bar-senior' },
    { key: '10+', label: '10+ Yrs', min: 10, max: Infinity, colorClass: 'bar-lead' }
  ];

  const panelistGraphData = panelistExperienceConfig.map((range) => {
    const count = myPanelists.filter((panelist) => {
      const experience = Number(panelist.experienceYears || 0);
      return experience >= range.min && experience <= range.max;
    }).length;

    return {
      ...range,
      count
    };
  });

  const maxPanelistCount = Math.max(...panelistGraphData.map((item) => item.count), 1);

  // Calculate interview statistics for home tab
  const totalInterviews = allInterviews.length;
  const scheduledInterviews = allInterviews.filter(interview =>
    interview.status === 'SCHEDULED'
  ).length;
  const completedInterviews = allInterviews.filter(interview =>
    interview.status === 'COMPLETED'
  ).length;
  
  // Calculate success rate (interviews with positive feedback)
  const successRate = completedInterviews > 0
    ? Math.round((allFeedbacks.filter(f => f.overallRecommendation === 'STRONG_YES' || f.overallRecommendation === 'YES').length / completedInterviews) * 100)
    : 0;

  // Active candidates (candidates with scheduled interviews)
  const activeCandidates = new Set(
    allInterviews
      .filter(interview => interview.status === 'SCHEDULED')
      .map(interview => interview.candidateEmail)
  ).size;

  // Feedback pending (completed interviews without feedback)
  const feedbackPending = allInterviews.filter(interview => {
    const hasFeedback = allFeedbacks.some(feedback =>
      feedback.interviewId === interview.id
    );
    return interview.status === 'COMPLETED' && !hasFeedback;
  }).length;

  // This month's interviews
  const currentMonth = new Date().getMonth();
  const currentYear = new Date().getFullYear();
  const thisMonthInterviews = allInterviews.filter(interview => {
    const interviewDate = new Date(interview.interviewDate);
    return interviewDate.getMonth() === currentMonth &&
           interviewDate.getFullYear() === currentYear;
  }).length;

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
        <button
          className={`hr-tab ${activeTab === 'interviews' ? 'active' : ''}`}
          onClick={() => setActiveTab('interviews')}
        >
          📅 Interviews
        </button>
        <button
          className={`hr-tab ${activeTab === 'candidateFeedback' ? 'active' : ''}`}
          onClick={() => setActiveTab('candidateFeedback')}
        >
          📋 Feedback
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
            {/* Home Tab - Dashboard Overview with Statistics */}
            {activeTab === 'home' && (
              <div className="home-section">
                <h2>📊 Dashboard Overview</h2>
                
                {/* Dashboard Statistics Cards */}
                <div className="dashboard-stats-container">
                  {/* Total Candidates Card */}
                  <div className="dashboard-stat-card candidates-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">👥</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">TOTAL CANDIDATES</h3>
                      <p className="stat-value">
                        {myCandidates.length}
                      </p>
                    </div>
                  </div>

                  {/* Total Panelists Card */}
                  <div className="dashboard-stat-card panelists-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">🎯</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">TOTAL PANELISTS</h3>
                      <p className="stat-value">
                        {myPanelists.length}
                      </p>
                    </div>
                  </div>

                  {/* Total Interviews Card */}
                  <div className="dashboard-stat-card interviews-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">📅</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">TOTAL INTERVIEWS</h3>
                      <p className="stat-value">
                        {totalInterviews}
                      </p>
                      <p className="stat-subtitle">Scheduled + Completed</p>
                    </div>
                  </div>

                  {/* Pending Interviews Card */}
                  <div className="dashboard-stat-card pending-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">⏳</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">PENDING INTERVIEWS</h3>
                      <p className="stat-value">
                        {scheduledInterviews}
                      </p>
                      <p className="stat-subtitle">Scheduled Only</p>
                    </div>
                  </div>

                  {/* Completed Interviews Card */}
                  <div className="dashboard-stat-card completed-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">✅</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">COMPLETED INTERVIEWS</h3>
                      <p className="stat-value">
                        {completedInterviews}
                      </p>
                      <p className="stat-subtitle">Success Rate: {successRate}%</p>
                    </div>
                  </div>

                  {/* Active Candidates Card */}
                  <div className="dashboard-stat-card active-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">🔥</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">ACTIVE CANDIDATES</h3>
                      <p className="stat-value">
                        {activeCandidates}
                      </p>
                      <p className="stat-subtitle">With Upcoming Interviews</p>
                    </div>
                  </div>

                  {/* Feedback Pending Card */}
                  <div className="dashboard-stat-card feedback-pending-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">📝</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">FEEDBACK PENDING</h3>
                      <p className="stat-value">
                        {feedbackPending}
                      </p>
                      <p className="stat-subtitle">Awaiting Feedback</p>
                    </div>
                  </div>

                  {/* This Month's Interviews Card */}
                  <div className="dashboard-stat-card month-card">
                    <div className="stat-icon">
                      <span className="icon-emoji">📆</span>
                    </div>
                    <div className="stat-content">
                      <h3 className="stat-title">THIS MONTH</h3>
                      <p className="stat-value">
                        {thisMonthInterviews}
                      </p>
                      <p className="stat-subtitle">Interviews This Month</p>
                    </div>
                  </div>
                </div>

                <div className="home-graphs-grid">
                  <div className="graph-card">
                    <div className="graph-card-header">
                      <div>
                        <h3>📈 Candidates Graph</h3>
                        <p>Status-wise distribution of your candidates (Click bars to filter)</p>
                      </div>
                      <span className="graph-total">Total: {myCandidates.length}</span>
                    </div>

                    <div className="graph-bars">
                      {candidateGraphData.map((item) => {
                        const percentage = myCandidates.length > 0
                          ? Math.round((item.count / myCandidates.length) * 100)
                          : 0;
                        const statsData = enhancedStats?.candidateStatusStats?.[item.key];
                        const trend = statsData?.trend || 'STABLE';
                        const change = statsData?.change || 0;
                        
                        return (
                          <div
                            className="graph-bar-row interactive"
                            key={item.key}
                            onClick={() => handleGraphBarClick('candidateStatus', item.key)}
                            title={`Click to filter by ${item.label}`}
                          >
                            <div className="graph-label-wrap">
                              <span className="graph-label">{item.label}</span>
                              <div className="graph-stats">
                                <span className="graph-count">{item.count}</span>
                                <span className="graph-percentage">({percentage}%)</span>
                                {enhancedStats && (
                                  <span className={`graph-trend ${getTrendClass(trend)}`}>
                                    {getTrendIcon(trend)} {Math.abs(change)}
                                  </span>
                                )}
                              </div>
                            </div>
                            <div className="graph-track">
                              <div
                                className={`graph-fill ${item.colorClass}`}
                                style={{ width: `${(item.count / maxCandidateCount) * 100}%` }}
                              >
                                <span className="graph-fill-label">{percentage}%</span>
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                    
                    {enhancedStats?.overallStats?.candidates && (
                      <div className="graph-summary">
                        <span className={`summary-trend ${getTrendClass(enhancedStats.overallStats.candidates.trend)}`}>
                          {getTrendIcon(enhancedStats.overallStats.candidates.trend)}
                          {Math.abs(enhancedStats.overallStats.candidates.change)} this month
                        </span>
                        <span className="summary-text">
                          vs. previous month ({enhancedStats.overallStats.candidates.previousMonth})
                        </span>
                      </div>
                    )}
                  </div>

                  <div className="graph-card">
                    <div className="graph-card-header">
                      <div>
                        <h3>📊 Panelists Graph</h3>
                        <p>Experience-wise distribution of your panelists (Click bars to filter)</p>
                      </div>
                      <span className="graph-total">Total: {myPanelists.length}</span>
                    </div>

                    <div className="graph-bars">
                      {panelistGraphData.map((item) => {
                        const percentage = myPanelists.length > 0
                          ? Math.round((item.count / myPanelists.length) * 100)
                          : 0;
                        const statsData = enhancedStats?.panelistExperienceStats?.[item.key];
                        const trend = statsData?.trend || 'STABLE';
                        const change = statsData?.change || 0;
                        
                        return (
                          <div
                            className="graph-bar-row interactive"
                            key={item.key}
                            onClick={() => handleGraphBarClick('panelistExperience', item.key)}
                            title={`Click to filter by ${item.label}`}
                          >
                            <div className="graph-label-wrap">
                              <span className="graph-label">{item.label}</span>
                              <div className="graph-stats">
                                <span className="graph-count">{item.count}</span>
                                <span className="graph-percentage">({percentage}%)</span>
                                {enhancedStats && (
                                  <span className={`graph-trend ${getTrendClass(trend)}`}>
                                    {getTrendIcon(trend)} {Math.abs(change)}
                                  </span>
                                )}
                              </div>
                            </div>
                            <div className="graph-track">
                              <div
                                className={`graph-fill ${item.colorClass}`}
                                style={{ width: `${(item.count / maxPanelistCount) * 100}%` }}
                              >
                                <span className="graph-fill-label">{percentage}%</span>
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                    
                    {enhancedStats?.overallStats?.panelists && (
                      <div className="graph-summary">
                        <span className={`summary-trend ${getTrendClass(enhancedStats.overallStats.panelists.trend)}`}>
                          {getTrendIcon(enhancedStats.overallStats.panelists.trend)}
                          {Math.abs(enhancedStats.overallStats.panelists.change)} this month
                        </span>
                        <span className="summary-text">
                          vs. previous month ({enhancedStats.overallStats.panelists.previousMonth})
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Welcome Message */}
                <div className="welcome-message-box">
                  <p className="welcome-greeting">
                    👋 Welcome back, <strong>{user.username}</strong>!
                  </p>
                  <p className="welcome-hint">
                    Use the tabs above to manage candidates, panelists, and interviews.
                  </p>
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
                        <label htmlFor="jrs">JRS</label>
                        <input
                          type="text"
                          id="jrs"
                          name="jrs"
                          value={newCandidate.jrs}
                          onChange={handleInputChange}
                          placeholder="Job Requisition System ID"
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="candidateType">Candidate Type</label>
                        <select
                          id="candidateType"
                          name="candidateType"
                          value={newCandidate.candidateType}
                          onChange={handleInputChange}
                        >
                          <option value="EXTERNAL">External</option>
                          <option value="INTERNAL">Internal</option>
                          <option value="REFERRAL">Referral</option>
                          <option value="AGENCY">Agency</option>
                        </select>
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
                            <label htmlFor="edit-jrs">JRS</label>
                            <input
                              type="text"
                              id="edit-jrs"
                              name="jrs"
                              value={editFormData.jrs || ''}
                              onChange={handleEditInputChange}
                              placeholder="Job Requisition System ID"
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor="edit-candidateType">Candidate Type</label>
                            <select
                              id="edit-candidateType"
                              name="candidateType"
                              value={editFormData.candidateType || 'EXTERNAL'}
                              onChange={handleEditInputChange}
                            >
                              <option value="EXTERNAL">External</option>
                              <option value="INTERNAL">Internal</option>
                              <option value="REFERRAL">Referral</option>
                              <option value="AGENCY">Agency</option>
                            </select>
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
                            <label htmlFor="jrs-display">JRS</label>
                            <input
                              type="text"
                              id="jrs-display"
                              value={schedulingInterview.jrs || schedulingInterview.position}
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
                    {/* Search and Filter Bar */}
                    <div className="search-filter-container">
                      <div className="search-filter-header">
                        <h3>🔍 Search & Filter Candidates</h3>
                        <button
                          onClick={handleClearFilters}
                          className="clear-filters-button"
                          title="Clear all filters"
                        >
                          🔄 Clear Filters
                        </button>
                      </div>

                      <div className="search-filter-grid">
                        {/* Search Input */}
                        <div className="search-box">
                          <label htmlFor="search-input">
                            <span className="search-icon">🔎</span> Search by Name or Email
                          </label>
                          <input
                            id="search-input"
                            type="text"
                            placeholder="Type to search candidates..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-input"
                          />
                          {searching && <span className="searching-indicator">Searching...</span>}
                        </div>

                        {/* Status Filter */}
                        <div className="filter-box">
                          <label htmlFor="status-filter">
                            <span className="filter-icon">📊</span> Status
                          </label>
                          <select
                            id="status-filter"
                            value={statusFilter}
                            onChange={(e) => setStatusFilter(e.target.value)}
                            className="filter-select"
                          >
                            <option value="ALL">All Statuses</option>
                            <option value="APPLIED">Applied</option>
                            <option value="SCREENING">Screening</option>
                            <option value="INTERVIEW">Interview</option>
                            <option value="SELECTED">Selected</option>
                            <option value="REJECTED">Rejected</option>
                          </select>
                        </div>

                        {/* Position Filter */}
                        <div className="filter-box">
                          <label htmlFor="position-filter">
                            <span className="filter-icon">💼</span> Position
                          </label>
                          <input
                            id="position-filter"
                            type="text"
                            placeholder="Filter by position..."
                            value={positionFilter}
                            onChange={(e) => setPositionFilter(e.target.value)}
                            className="filter-input"
                          />
                        </div>

                        {/* Location Filter */}
                        <div className="filter-box">
                          <label htmlFor="location-filter">
                            <span className="filter-icon">📍</span> Location
                          </label>
                          <input
                            id="location-filter"
                            type="text"
                            placeholder="Filter by location..."
                            value={locationFilter}
                            onChange={(e) => setLocationFilter(e.target.value)}
                            className="filter-input"
                          />
                        </div>
                      </div>

                      {/* Results Summary */}
                      <div className="search-results-summary">
                        <span className="results-count">
                          📋 Showing <strong>{filteredCandidates.length}</strong> of <strong>{myCandidates.length}</strong> candidates
                        </span>
                        {(searchTerm || statusFilter !== 'ALL' || positionFilter || locationFilter) && (
                          <span className="active-filters-badge">
                            🔍 Filters Active
                          </span>
                        )}
                      </div>
                    </div>

                    {filteredCandidates && filteredCandidates.length > 0 ? (
                      <div className="table-container">
                        <table className="candidate-table">
                          <thead>
                            <tr>
                              <th>Name</th>
                              <th>Email</th>
                              <th>Phone</th>
                              <th>JRS</th>
                              <th>Candidate Type</th>
                              <th>Experience</th>
                              <th>Location</th>
                              <th>Interview</th>
                              <th>Status</th>
                              <th>Actions</th>
                            </tr>
                          </thead>
                          <tbody>
                            {filteredCandidates.map((candidate) => {
                              const interviews = candidateInterviews[candidate.email] || [];
                              const latestInterview = interviews.length > 0 ? interviews[0] : null;
                              
                              return (
                              <tr key={candidate.id} id={`candidate-row-${candidate.id}`}>
                                <td>{candidate.name}</td>
                                <td>{candidate.email}</td>
                                <td>{candidate.phone}</td>
                                <td>{candidate.jrs || 'N/A'}</td>
                                <td>{candidate.candidateType || 'N/A'}</td>
                                <td>{candidate.experienceYears ? `${candidate.experienceYears} years` : 'N/A'}</td>
                                <td>{candidate.location || 'N/A'}</td>
                                <td className="interview-cell">
                                  {latestInterview && (
                                    <div className="interview-info">
                                      <span className={`interview-status-badge interview-${latestInterview.status?.toLowerCase()}`}>
                                        {latestInterview.status}
                                      </span>
                                      {latestInterview.status !== 'COMPLETED' && (
                                        <div className="interview-details">
                                          <small>📅 {latestInterview.interviewDate}</small>
                                          <small>🕐 {latestInterview.interviewTimeFrom} - {latestInterview.interviewTimeTo}</small>
                                        </div>
                                      )}
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
                                  <span className={`status-badge status-${candidate.status?.toLowerCase()}`}>
                                    {candidate.status}
                                  </span>
                                </td>
                                <td className="action-buttons">
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

            {/* Interviews Tab */}
            {activeTab === 'interviews' && (
              <div className="interviews-section">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                  <div>
                    <h2>📅 All Interviews</h2>
                    <p className="section-description">
                      View and manage all scheduled interviews ({allInterviews.length} total)
                      {lastInterviewUpdate && (
                        <span style={{ marginLeft: '1rem', fontSize: '0.85em', color: '#666' }}>
                          Last updated: {lastInterviewUpdate.toLocaleTimeString('en-IN', {
                            hour: '2-digit',
                            minute: '2-digit',
                            second: '2-digit'
                          })}
                        </span>
                      )}
                    </p>
                  </div>
                  <button
                    onClick={handleManualRefreshInterviews}
                    disabled={loadingInterviews}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: '#4CAF50',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: loadingInterviews ? 'not-allowed' : 'pointer',
                      fontSize: '0.9em',
                      fontWeight: '500',
                      opacity: loadingInterviews ? 0.6 : 1
                    }}
                  >
                    {loadingInterviews ? '🔄 Refreshing...' : '🔄 Refresh Now'}
                  </button>
                </div>

                {/* Status Filter Dropdown */}
                <div className="filter-section">
                  <label htmlFor="status-filter">Filter by Status:</label>
                  <select
                    id="status-filter"
                    value={interviewStatusFilter}
                    onChange={(e) => setInterviewStatusFilter(e.target.value)}
                    className="status-filter-dropdown"
                  >
                    <option value="ALL">All Statuses</option>
                    <option value="SCHEDULED">Scheduled</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                    <option value="RESCHEDULED">Rescheduled</option>
                  </select>
                  <span className="filter-count">
                    Showing {filteredInterviews.length} of {allInterviews.length} interviews
                  </span>
                  <span style={{ marginLeft: '1rem', fontSize: '0.85em', color: '#666', fontStyle: 'italic' }}>
                    ⏱️ Auto-refreshes every 60 seconds
                  </span>
                </div>

                {loadingInterviews ? (
                  <div className="loading-message">
                    <div className="spinner"></div>
                    <p>Loading interviews...</p>
                  </div>
                ) : filteredInterviews && filteredInterviews.length > 0 ? (
                  <div className="table-container">
                    <table className="candidate-table interviews-table">
                      <thead>
                        <tr>
                          <th>Interview ID</th>
                          <th>Candidate Name</th>
                          <th>Candidate Email</th>
                          <th>JRS</th>
                          <th>Interview Date</th>
                          <th>Time From</th>
                          <th>Time To</th>
                          <th>Status</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredInterviews.map((interview) => (
                          <tr key={interview.id}>
                            <td>{interview.id}</td>
                            <td>{interview.candidateName || 'N/A'}</td>
                            <td>{interview.candidateEmail || 'N/A'}</td>
                            <td>{interview.jrs || 'N/A'}</td>
                            <td>
                              {interview.interviewDate ?
                                new Date(interview.interviewDate).toLocaleDateString('en-IN', {
                                  year: 'numeric',
                                  month: 'short',
                                  day: 'numeric'
                                }) : 'N/A'}
                            </td>
                            <td>
                              {interview.interviewTimeFrom || 'N/A'}
                            </td>
                            <td>
                              {interview.interviewTimeTo || 'N/A'}
                            </td>
                            <td>
                              <span className={`interview-status-badge interview-${interview.status?.toLowerCase()}`}>
                                {interview.status || 'N/A'}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="no-candidates-message">
                    <p>📭 No interviews found</p>
                    <p className="hint-text">
                      {interviewStatusFilter !== 'ALL'
                        ? `No interviews with status "${interviewStatusFilter}"`
                        : 'Schedule interviews from the "Manage Candidates" tab'}
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* Candidate Feedback Tab */}
            {activeTab === 'candidateFeedback' && (
              <div className="feedback-section">
                <h2>📋 Feedback</h2>
                <p className="section-description">
                  View all technical feedback submitted by panelists ({allFeedbacks.length} total)
                </p>

                {feedbackError && (
                  <div className="form-error-message">
                    ❌ {feedbackError}
                  </div>
                )}

                {loadingFeedbacks ? (
                  <div className="loading-message">
                    <div className="spinner"></div>
                    <p>Loading feedbacks...</p>
                  </div>
                ) : allFeedbacks && allFeedbacks.length > 0 ? (
                  <div className="table-container">
                    <table className="candidate-table feedback-table">
                      <thead>
                        <tr>
                          <th>Feedback ID</th>
                          <th>Candidate Name</th>
                          <th>JRS</th>
                          <th>Evaluation Date</th>
                          <th>Overall Rating</th>
                          <th>Recommendation</th>
                          <th>Status</th>
                          <th>Submitted At</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {allFeedbacks.map((feedback) => (
                          <tr key={feedback.id}>
                            <td>{feedback.id}</td>
                            <td>{feedback.candidateName || ''}</td>
                            <td>{feedback.jobRoleSpecification || ''}</td>
                            <td>
                              {feedback.evaluationDate ?
                                new Date(feedback.evaluationDate).toLocaleDateString('en-IN', {
                                  year: 'numeric',
                                  month: 'short',
                                  day: 'numeric'
                                }) : ''}
                            </td>
                            <td>
                              <span className="rating-badge">
                                {feedback.overallRating ? `${feedback.overallRating}/10` : ''}
                              </span>
                            </td>
                            <td>
                              <span className={`recommendation-badge recommendation-${feedback.techPanelRecommendation?.toLowerCase()}`}>
                                {feedback.techPanelRecommendation || ''}
                              </span>
                            </td>
                            <td>
                              <span className={`status-badge status-${feedback.status?.toLowerCase()}`}>
                                {feedback.status || ''}
                              </span>
                            </td>
                            <td>
                              {feedback.createdAt ?
                                new Date(feedback.createdAt).toLocaleString('en-IN', {
                                  year: 'numeric',
                                  month: 'short',
                                  day: 'numeric',
                                  hour: '2-digit',
                                  minute: '2-digit'
                                }) : ''}
                            </td>
                            <td>
                              <button
                                onClick={() => handleDownloadFeedbackPdf(feedback.id, feedback.candidateName)}
                                className="download-button"
                                title="Download PDF"
                              >
                                📥 Download PDF
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="no-candidates-message">
                    <p>📭 No feedback submitted yet</p>
                    <p className="hint-text">
                      Feedback will appear here once panelists submit their technical assessments
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* HR Profile Tab */}
            {activeTab === 'profile' && (
              <div className="profile-section">
                <h2 style={{ textAlign: 'left', width: '100%' }}>👤 HR Profile</h2>

                {profileError && (
                  <div className="form-error-message">
                    ❌ {profileError}
                  </div>
                )}

                {profileSuccess && (
                  <div className="form-success-message">
                    ✅ {profileSuccess}
                  </div>
                )}

                {profileLoading && !hrProfile ? (
                  <div className="loading-message">
                    <div className="spinner"></div>
                    <p>Loading profile...</p>
                  </div>
                ) : (
                  <form onSubmit={handleSaveProfile} className="profile-form">
                    {/* Personal Information Section */}
                    <div className="form-section">
                      <h3>📋 Personal Information</h3>
                      <div className="form-grid">
                        <div className="form-group">
                          <label htmlFor="fullName">Full Name <span className="required">*</span></label>
                          <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            value={profileFormData.fullName}
                            onChange={handleProfileInputChange}
                            placeholder="Enter your full name"
                            required
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="phone">Phone</label>
                          <input
                            type="tel"
                            id="phone"
                            name="phone"
                            value={profileFormData.phone}
                            onChange={handleProfileInputChange}
                            placeholder="Enter phone number"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="location">Location</label>
                          <input
                            type="text"
                            id="location"
                            name="location"
                            value={profileFormData.location}
                            onChange={handleProfileInputChange}
                            placeholder="City, State"
                          />
                        </div>

                        <div className="form-group full-width">
                          <label htmlFor="address">Address</label>
                          <input
                            type="text"
                            id="address"
                            name="address"
                            value={profileFormData.address}
                            onChange={handleProfileInputChange}
                            placeholder="Complete address"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Professional Details Section */}
                    <div className="form-section">
                      <h3>💼 Professional Details</h3>
                      <div className="form-grid">
                        <div className="form-group">
                          <label htmlFor="designation">Designation</label>
                          <input
                            type="text"
                            id="designation"
                            name="designation"
                            value={profileFormData.designation}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., Senior HR Manager"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="department">Department</label>
                          <input
                            type="text"
                            id="department"
                            name="department"
                            value={profileFormData.department}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., Human Resources"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="employeeId">Employee ID</label>
                          <input
                            type="text"
                            id="employeeId"
                            name="employeeId"
                            value={profileFormData.employeeId}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., HR001"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="experienceYears">Experience (Years)</label>
                          <input
                            type="number"
                            id="experienceYears"
                            name="experienceYears"
                            value={profileFormData.experienceYears}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., 5"
                            min="0"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="company">Company</label>
                          <input
                            type="text"
                            id="company"
                            name="company"
                            value={profileFormData.company}
                            onChange={handleProfileInputChange}
                            placeholder="Company name"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="workType">Work Type</label>
                          <select
                            id="workType"
                            name="workType"
                            value={profileFormData.workType}
                            onChange={handleProfileInputChange}
                          >
                            <option value="On-site">On-site</option>
                            <option value="Remote">Remote</option>
                            <option value="Hybrid">Hybrid</option>
                          </select>
                        </div>

                        <div className="form-group full-width">
                          <label htmlFor="bio">Bio</label>
                          <textarea
                            id="bio"
                            name="bio"
                            value={profileFormData.bio}
                            onChange={handleProfileInputChange}
                            placeholder="Brief description about yourself"
                            rows="3"
                          />
                        </div>
                      </div>
                    </div>

                    {/* HR Specific Information */}
                    <div className="form-section">
                      <h3>🎯 HR Specific Information</h3>
                      <div className="form-grid">
                        <div className="form-group">
                          <label htmlFor="hrSpecialization">HR Specialization</label>
                          <input
                            type="text"
                            id="hrSpecialization"
                            name="hrSpecialization"
                            value={profileFormData.hrSpecialization}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., Recruitment, Training"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="region">Region</label>
                          <input
                            type="text"
                            id="region"
                            name="region"
                            value={profileFormData.region}
                            onChange={handleProfileInputChange}
                            placeholder="Geographic region managed"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="teamName">Team Name</label>
                          <input
                            type="text"
                            id="teamName"
                            name="teamName"
                            value={profileFormData.teamName}
                            onChange={handleProfileInputChange}
                            placeholder="Your team name"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="reportingManager">Reporting Manager</label>
                          <input
                            type="text"
                            id="reportingManager"
                            name="reportingManager"
                            value={profileFormData.reportingManager}
                            onChange={handleProfileInputChange}
                            placeholder="Manager's name"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Contact Information */}
                    <div className="form-section">
                      <h3>📞 Contact Information</h3>
                      <div className="form-grid">
                        <div className="form-group">
                          <label htmlFor="linkedinUrl">LinkedIn URL</label>
                          <input
                            type="url"
                            id="linkedinUrl"
                            name="linkedinUrl"
                            value={profileFormData.linkedinUrl}
                            onChange={handleProfileInputChange}
                            placeholder="https://linkedin.com/in/yourprofile"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="slackHandle">Slack Handle</label>
                          <input
                            type="text"
                            id="slackHandle"
                            name="slackHandle"
                            value={profileFormData.slackHandle}
                            onChange={handleProfileInputChange}
                            placeholder="@yourhandle"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="emergencyContact">Emergency Contact Name</label>
                          <input
                            type="text"
                            id="emergencyContact"
                            name="emergencyContact"
                            value={profileFormData.emergencyContact}
                            onChange={handleProfileInputChange}
                            placeholder="Emergency contact person"
                          />
                        </div>

                        <div className="form-group">
                          <label htmlFor="emergencyPhone">Emergency Phone</label>
                          <input
                            type="tel"
                            id="emergencyPhone"
                            name="emergencyPhone"
                            value={profileFormData.emergencyPhone}
                            onChange={handleProfileInputChange}
                            placeholder="Emergency contact number"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Skills and Education */}
                    <div className="form-section">
                      <h3>🎓 Skills & Education</h3>
                      <div className="form-grid">
                        <div className="form-group full-width">
                          <label htmlFor="skills">Skills</label>
                          <input
                            type="text"
                            id="skills"
                            name="skills"
                            value={profileFormData.skills}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., Recruitment, Employee Relations, HRIS"
                          />
                        </div>

                        <div className="form-group full-width">
                          <label htmlFor="certifications">Certifications</label>
                          <input
                            type="text"
                            id="certifications"
                            name="certifications"
                            value={profileFormData.certifications}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., SHRM-CP, PHR"
                          />
                        </div>

                        <div className="form-group full-width">
                          <label htmlFor="education">Education</label>
                          <input
                            type="text"
                            id="education"
                            name="education"
                            value={profileFormData.education}
                            onChange={handleProfileInputChange}
                            placeholder="e.g., MBA in HR Management"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Profile Statistics (Read-only) */}
                    {hrProfile && (
                      <div className="form-section">
                        <h3>📊 Profile Statistics</h3>
                        <div className="stats-grid">
                          <div className="stat-item">
                            <span className="stat-label">Total Candidates Managed:</span>
                            <span className="stat-value">{hrProfile.totalCandidatesManaged || 0}</span>
                          </div>
                          <div className="stat-item">
                            <span className="stat-label">Total Panelists Managed:</span>
                            <span className="stat-value">{hrProfile.totalPanelistsManaged || 0}</span>
                          </div>
                          <div className="stat-item">
                            <span className="stat-label">Account Status:</span>
                            <span className={`stat-value ${hrProfile.active ? 'active-status' : 'inactive-status'}`}>
                              {hrProfile.active ? '✅ Active' : '❌ Inactive'}
                            </span>
                          </div>
                          <div className="stat-item">
                            <span className="stat-label">Email:</span>
                            <span className="stat-value">{hrProfile.email || user.email}</span>
                          </div>
                        </div>
                      </div>
                    )}

                    {/* Form Actions */}
                    <div className="form-actions">
                      <button
                        type="submit"
                        className="submit-button"
                        disabled={profileLoading}
                      >
                        {profileLoading ? '💾 Saving...' : '💾 Save Profile'}
                      </button>
                      <button
                        type="button"
                        className="cancel-button"
                        onClick={() => setActiveTab('home')}
                      >
                        ❌ Cancel
                      </button>
                    </div>
                  </form>
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