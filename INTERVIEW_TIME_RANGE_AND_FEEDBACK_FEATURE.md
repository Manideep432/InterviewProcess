# Interview Time Range and Feedback Feature

## Overview
Enhancement to the interview scheduling system to include:
1. Start time and end time (time range) for interviews
2. Automatic status change to COMPLETED when interview ends
3. Feedback button for completed interviews
4. Integration with panelist dashboard

## Required Changes

### 1. Database Schema Changes

#### Interview Model Updates
Add new fields to `Interview.java`:
```java
@Column(name = "start_time", nullable = false)
private LocalTime startTime;

@Column(name = "end_time", nullable = false)
private LocalTime endTime;

@Column(name = "feedback", length = 2000)
private String feedback;

@Column(name = "feedback_submitted_at")
private LocalDateTime feedbackSubmittedAt;

@Column(name = "feedback_by")
private String feedbackBy; // "PANELIST" or "HR"
```

### 2. Backend Changes

#### A. Update Interview Model
File: `backend/src/main/java/com/login/model/Interview.java`

Add fields:
- `startTime` (LocalTime)
- `endTime` (LocalTime)
- `feedback` (String)
- `feedbackSubmittedAt` (LocalDateTime)
- `feedbackBy` (String)

Update constructor to include start and end times.

#### B. Update InterviewService
File: `backend/src/main/java/com/login/service/InterviewService.java`

Add methods:
```java
@Transactional(timeout = 30)
public Interview submitFeedback(Long interviewId, String feedback, String feedbackBy) {
    Interview interview = interviewRepository.findById(interviewId)
        .orElseThrow(() -> new RuntimeException("Interview not found"));
    
    interview.setFeedback(feedback);
    interview.setFeedbackBy(feedbackBy);
    interview.setFeedbackSubmittedAt(LocalDateTime.now());
    
    return interviewRepository.save(interview);
}

@Transactional(readOnly = true)
public List<Interview> getCompletedInterviews() {
    return interviewRepository.findByStatus(InterviewStatus.COMPLETED);
}

// Scheduled task to auto-complete interviews
@Scheduled(fixedRate = 60000) // Run every minute
public void autoCompleteInterviews() {
    LocalDateTime now = LocalDateTime.now();
    List<Interview> activeInterviews = interviewRepository.findByStatus(InterviewStatus.SCHEDULED);
    
    for (Interview interview : activeInterviews) {
        LocalDateTime interviewEnd = LocalDateTime.of(
            interview.getInterviewDate(),
            interview.getEndTime()
        );
        
        if (now.isAfter(interviewEnd)) {
            interview.setStatus(InterviewStatus.COMPLETED);
            interviewRepository.save(interview);
        }
    }
}
```

#### C. Update HRService
File: `backend/src/main/java/com/login/service/HRService.java`

Update `scheduleInterview` method to accept start and end times:
```java
interview.setStartTime(LocalTime.parse(startTime));
interview.setEndTime(LocalTime.parse(endTime));
```

#### D. Add Feedback Controller Endpoint
File: `backend/src/main/java/com/login/controller/InterviewController.java`

```java
@PostMapping("/{id}/feedback")
public ResponseEntity<?> submitFeedback(
        @PathVariable Long id,
        @RequestBody Map<String, String> request) {
    try {
        String feedback = request.get("feedback");
        String feedbackBy = request.get("feedbackBy");
        
        Interview updated = interviewService.submitFeedback(id, feedback, feedbackBy);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Feedback submitted successfully",
            "interview", updated
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "success", false,
            "message", e.getMessage()
        ));
    }
}
```

### 3. Frontend Changes

#### A. Update Interview Scheduling Form
File: `frontend/src/components/HRDashboard.js`

Update state:
```javascript
const [interviewFormData, setInterviewFormData] = useState({
  panelistEmail: '',
  interviewDate: '',
  startTime: '',
  endTime: '',
  notes: ''
});
```

Update form JSX to include start and end time fields:
```jsx
<div className="form-group">
  <label htmlFor="startTime">Start Time <span className="required">*</span></label>
  <input
    type="time"
    id="startTime"
    name="startTime"
    value={interviewFormData.startTime}
    onChange={handleInterviewInputChange}
    required
  />
</div>

<div className="form-group">
  <label htmlFor="endTime">End Time <span className="required">*</span></label>
  <input
    type="time"
    id="endTime"
    name="endTime"
    value={interviewFormData.endTime}
    onChange={handleInterviewInputChange}
    required
  />
</div>
```

#### B. Update Interview Display
Show time range in interview column:
```jsx
<div className="interview-details">
  <small>📅 {latestInterview.interviewDate}</small>
  <small>🕐 {latestInterview.startTime} - {latestInterview.endTime}</small>
</div>
```

#### C. Add Feedback Button
```jsx
{latestInterview.status === 'COMPLETED' && !latestInterview.feedback && (
  <button
    onClick={() => handleOpenFeedbackModal(latestInterview)}
    className="feedback-button"
  >
    📝 Add Feedback
  </button>
)}

{latestInterview.feedback && (
  <span className="feedback-submitted">✅ Feedback Submitted</span>
)}
```

#### D. Add Feedback Modal
```jsx
{showFeedbackModal && (
  <div className="modal-overlay">
    <div className="feedback-modal">
      <h3>Submit Interview Feedback</h3>
      <textarea
        value={feedbackText}
        onChange={(e) => setFeedbackText(e.target.value)}
        placeholder="Enter your feedback..."
        rows="6"
      />
      <div className="modal-actions">
        <button onClick={handleSubmitFeedback}>Submit</button>
        <button onClick={handleCloseFeedbackModal}>Cancel</button>
      </div>
    </div>
  </div>
)}
```

### 4. CSS Styling

Add to `HRDashboard.css`:
```css
.feedback-button {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75em;
  margin-top: 4px;
  width: 100%;
}

.feedback-button:hover {
  background-color: #1976d2;
}

.feedback-submitted {
  color: #4caf50;
  font-size: 0.75em;
  font-weight: 600;
  display: block;
  margin-top: 4px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.feedback-modal {
  background: white;
  padding: 30px;
  border-radius: 10px;
  max-width: 500px;
  width: 90%;
}

.feedback-modal h3 {
  margin-top: 0;
  color: #333;
}

.feedback-modal textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  justify-content: flex-end;
}

.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.modal-actions button:first-child {
  background-color: #4caf50;
  color: white;
}

.modal-actions button:last-child {
  background-color: #f44336;
  color: white;
}
```

### 5. Panelist Dashboard Integration

#### Update PanelistDashboard.js
Add completed interviews section:
```jsx
<div className="completed-interviews">
  <h3>Completed Interviews</h3>
  {completedInterviews.map(interview => (
    <div key={interview.id} className="interview-card completed">
      <h4>{interview.candidateName}</h4>
      <p>Position: {interview.position}</p>
      <p>Date: {interview.interviewDate}</p>
      <p>Time: {interview.startTime} - {interview.endTime}</p>
      {interview.feedback && (
        <div className="feedback-display">
          <strong>Feedback:</strong>
          <p>{interview.feedback}</p>
        </div>
      )}
    </div>
  ))}
</div>
```

### 6. Database Migration

Run SQL to add new columns:
```sql
ALTER TABLE interviews 
ADD COLUMN start_time TIME,
ADD COLUMN end_time TIME,
ADD COLUMN feedback VARCHAR(2000),
ADD COLUMN feedback_submitted_at TIMESTAMP,
ADD COLUMN feedback_by VARCHAR(50);

-- Update existing records with default values
UPDATE interviews 
SET start_time = interview_time,
    end_time = ADDTIME(interview_time, '01:00:00')
WHERE start_time IS NULL;

-- Make columns NOT NULL after setting defaults
ALTER TABLE interviews 
MODIFY COLUMN start_time TIME NOT NULL,
MODIFY COLUMN end_time TIME NOT NULL;
```

## Implementation Steps

1. **Phase 1: Database**
   - Update Interview model
   - Run database migration

2. **Phase 2: Backend**
   - Update InterviewService
   - Update HRService
   - Add feedback endpoint
   - Add scheduled task for auto-completion

3. **Phase 3: Frontend - HR Dashboard**
   - Update scheduling form
   - Update interview display
   - Add feedback button and modal

4. **Phase 4: Frontend - Panelist Dashboard**
   - Add completed interviews section
   - Display feedback

5. **Phase 5: Testing**
   - Test interview scheduling with time range
   - Test auto-completion
   - Test feedback submission
   - Test panelist view

## Benefits

1. **Better Time Management**: Clear start and end times
2. **Automatic Status Updates**: No manual intervention needed
3. **Feedback Collection**: Structured feedback from panelists
4. **Improved Tracking**: Better visibility of interview lifecycle
5. **Enhanced Reporting**: Time-based analytics possible

## Future Enhancements

1. Email notifications when interview is completed
2. Reminder notifications before interview starts
3. Rating system (1-5 stars) along with feedback
4. Feedback templates for common scenarios
5. Analytics dashboard for interview metrics

---

**Note**: This is a comprehensive feature that requires careful implementation. It's recommended to implement in phases and test thoroughly at each stage.