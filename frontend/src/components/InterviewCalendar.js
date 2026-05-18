import React, { useState, useEffect } from 'react';
import './InterviewCalendar.css';

/**
 * Interview Calendar Component - Schedule interviews with date/time selection
 * @author Bob
 */
const InterviewCalendar = ({ user, onSchedule }) => {
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState('');
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [candidates, setCandidates] = useState([]);
  const [panelists, setPanelists] = useState([]);
  const [selectedCandidate, setSelectedCandidate] = useState('');
  const [selectedPanelist, setSelectedPanelist] = useState('');
  const [position, setPosition] = useState('');
  const [notes, setNotes] = useState('');
  const [duration, setDuration] = useState(60);
  const [scheduledInterviews, setScheduledInterviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Time slots (9 AM to 6 PM)
  const timeSlots = [
    '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '12:00', '12:30', '13:00', '13:30', '14:00', '14:30',
    '15:00', '15:30', '16:00', '16:30', '17:00', '17:30'
  ];

  useEffect(() => {
    fetchCandidates();
    fetchPanelists();
    fetchScheduledInterviews();
  }, []);

  const fetchCandidates = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/candidates/all`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      if (data.success) {
        setCandidates(data.candidates || []);
      }
    } catch (err) {
      console.error('Error fetching candidates:', err);
    }
  };

  const fetchPanelists = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/panelists/active`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      if (data.success) {
        setPanelists(data.panelists || []);
      }
    } catch (err) {
      console.error('Error fetching panelists:', err);
    }
  };

  const fetchScheduledInterviews = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/hr/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      if (data.success) {
        setScheduledInterviews(data.meetings || []);
      }
    } catch (err) {
      console.error('Error fetching scheduled interviews:', err);
    }
  };

  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];
    // Add empty cells for days before the first day of the month
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    // Add all days of the month
    for (let i = 1; i <= daysInMonth; i++) {
      days.push(new Date(year, month, i));
    }
    return days;
  };

  const handleScheduleInterview = async () => {
    if (!selectedDate || !selectedTime || !selectedCandidate || !selectedPanelist || !position) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      
      // Format date and time
      const dateStr = selectedDate.toISOString().split('T')[0];
      const scheduledDateTime = `${dateStr}T${selectedTime}:00`;

      const candidate = candidates.find(c => c.id === parseInt(selectedCandidate));
      
      const interviewData = {
        hrId: user.id,
        panelistId: parseInt(selectedPanelist),
        candidateId: parseInt(selectedCandidate),
        candidateName: candidate?.fullName || 'Unknown',
        candidateEmail: candidate?.email || '',
        interviewDate: dateStr,
        interviewTime: selectedTime,
        position: position,
        notes: notes,
        scheduledStartTime: scheduledDateTime,
        durationMinutes: duration
      };

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/api/meetings/create`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(interviewData)
      });

      const data = await response.json();
      
      if (data.success) {
        setSuccess('Interview scheduled successfully!');
        // Reset form
        setSelectedDate(null);
        setSelectedTime('');
        setSelectedCandidate('');
        setSelectedPanelist('');
        setPosition('');
        setNotes('');
        setDuration(60);
        // Refresh scheduled interviews
        fetchScheduledInterviews();
        if (onSchedule) onSchedule(data.meeting);
      } else {
        setError(data.message || 'Failed to schedule interview');
      }
    } catch (err) {
      setError('Error scheduling interview: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const isDateDisabled = (date) => {
    if (!date) return true;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  };

  const isTimeSlotBooked = (date, time) => {
    if (!date || !selectedPanelist) return false;
    const dateStr = date.toISOString().split('T')[0];
    return scheduledInterviews.some(interview => 
      interview.panelistId === parseInt(selectedPanelist) &&
      interview.scheduledStartTime?.startsWith(dateStr) &&
      interview.scheduledStartTime?.includes(time)
    );
  };

  const formatDate = (date) => {
    if (!date) return '';
    return date.toLocaleDateString('en-IN', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  const previousMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1));
  };

  const nextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1));
  };

  const days = getDaysInMonth(currentMonth);
  const monthName = currentMonth.toLocaleDateString('en-IN', { month: 'long', year: 'numeric' });

  return (
    <div className="interview-calendar-container">
      <h2>📅 Schedule Interview</h2>

      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <div className="calendar-layout">
        {/* Calendar */}
        <div className="calendar-section">
          <div className="calendar-header">
            <button onClick={previousMonth} className="nav-button">‹</button>
            <h3>{monthName}</h3>
            <button onClick={nextMonth} className="nav-button">›</button>
          </div>

          <div className="calendar-grid">
            <div className="day-header">Sun</div>
            <div className="day-header">Mon</div>
            <div className="day-header">Tue</div>
            <div className="day-header">Wed</div>
            <div className="day-header">Thu</div>
            <div className="day-header">Fri</div>
            <div className="day-header">Sat</div>

            {days.map((day, index) => (
              <div
                key={index}
                className={`calendar-day ${!day ? 'empty' : ''} ${
                  day && selectedDate && day.toDateString() === selectedDate.toDateString() ? 'selected' : ''
                } ${day && isDateDisabled(day) ? 'disabled' : ''}`}
                onClick={() => day && !isDateDisabled(day) && setSelectedDate(day)}
              >
                {day ? day.getDate() : ''}
              </div>
            ))}
          </div>
        </div>

        {/* Scheduling Form */}
        <div className="schedule-form">
          <h3>Interview Details</h3>

          {selectedDate && (
            <div className="selected-date-info">
              <strong>Selected Date:</strong> {formatDate(selectedDate)}
            </div>
          )}

          <div className="form-group">
            <label>Candidate *</label>
            <select 
              value={selectedCandidate} 
              onChange={(e) => setSelectedCandidate(e.target.value)}
              required
            >
              <option value="">Select Candidate</option>
              {candidates.map(candidate => (
                <option key={candidate.id} value={candidate.id}>
                  {candidate.fullName} - {candidate.email}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Panelist *</label>
            <select 
              value={selectedPanelist} 
              onChange={(e) => setSelectedPanelist(e.target.value)}
              required
            >
              <option value="">Select Panelist</option>
              {panelists.map(panelist => (
                <option key={panelist.id} value={panelist.id}>
                  {panelist.user?.username} - {panelist.specialization}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Position *</label>
            <input
              type="text"
              value={position}
              onChange={(e) => setPosition(e.target.value)}
              placeholder="e.g., Senior Software Engineer"
              required
            />
          </div>

          <div className="form-group">
            <label>Time Slot *</label>
            <div className="time-slots-grid">
              {timeSlots.map(time => (
                <button
                  key={time}
                  className={`time-slot ${selectedTime === time ? 'selected' : ''} ${
                    isTimeSlotBooked(selectedDate, time) ? 'booked' : ''
                  }`}
                  onClick={() => !isTimeSlotBooked(selectedDate, time) && setSelectedTime(time)}
                  disabled={isTimeSlotBooked(selectedDate, time)}
                >
                  {time}
                  {isTimeSlotBooked(selectedDate, time) && ' (Booked)'}
                </button>
              ))}
            </div>
          </div>

          <div className="form-group">
            <label>Duration (minutes)</label>
            <select value={duration} onChange={(e) => setDuration(parseInt(e.target.value))}>
              <option value={30}>30 minutes</option>
              <option value={45}>45 minutes</option>
              <option value={60}>60 minutes</option>
              <option value={90}>90 minutes</option>
              <option value={120}>120 minutes</option>
            </select>
          </div>

          <div className="form-group">
            <label>Notes</label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Additional notes or instructions..."
              rows={4}
            />
          </div>

          <button 
            className="schedule-button" 
            onClick={handleScheduleInterview}
            disabled={loading || !selectedDate || !selectedTime || !selectedCandidate || !selectedPanelist || !position}
          >
            {loading ? '⏳ Scheduling...' : '📅 Schedule Interview'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InterviewCalendar;

// Made with Bob