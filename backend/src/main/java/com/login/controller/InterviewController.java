package com.login.controller;

import com.login.dto.CandidateInterviewDTO;
import com.login.model.Interview;
import com.login.model.Interview.InterviewStatus;
import com.login.service.InterviewService;
import com.login.service.InterviewSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Interview Controller - REST API endpoints for interview management
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewSchedulerService interviewSchedulerService;

    /**
     * Schedule a new interview
     */
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleInterview(@RequestBody Map<String, Object> request) {
        try {
            Long panelistId = Long.valueOf(request.get("panelistId").toString());
            String candidateName = (String) request.get("candidateName");
            String candidateEmail = (String) request.get("candidateEmail");
            LocalDate interviewDate = LocalDate.parse((String) request.get("interviewDate"));
            LocalTime interviewTimeFrom = LocalTime.parse((String) request.get("interviewTimeFrom"));
            LocalTime interviewTimeTo = LocalTime.parse((String) request.get("interviewTimeTo"));
            String jrs = (String) request.get("jrs");

            Interview interview = interviewService.scheduleInterview(
                panelistId, candidateName, candidateEmail,
                interviewDate, interviewTimeFrom, interviewTimeTo, jrs
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview scheduled successfully",
                "interview", interview
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Failed to schedule interview: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all interviews for a panelist
     */
    @GetMapping("/panelist/{panelistId}")
    public ResponseEntity<?> getInterviewsByPanelist(@PathVariable Long panelistId) {
        try {
            List<Interview> interviews = interviewService.getInterviewsByPanelist(panelistId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get interviews by panelist and status
     */
    @GetMapping("/panelist/{panelistId}/status/{status}")
    public ResponseEntity<?> getInterviewsByPanelistAndStatus(
            @PathVariable Long panelistId, 
            @PathVariable String status) {
        try {
            InterviewStatus interviewStatus = InterviewStatus.valueOf(status.toUpperCase());
            List<Interview> interviews = interviewService.getInterviewsByPanelistAndStatus(
                panelistId, interviewStatus
            );
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get interviews by candidate email
     */
    @GetMapping("/candidate/{candidateEmail}")
    public ResponseEntity<?> getInterviewsByCandidateEmail(@PathVariable String candidateEmail) {
        try {
            List<Interview> interviews = interviewService.getInterviewsByCandidateEmail(candidateEmail);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get interview by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInterviewById(@PathVariable Long id) {
        try {
            return interviewService.getInterviewById(id)
                .map(interview -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "interview", interview
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Interview not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update interview status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateInterviewStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            InterviewStatus status = InterviewStatus.valueOf(request.get("status").toUpperCase());
            Interview updated = interviewService.updateInterviewStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview status updated successfully",
                "interview", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update interview details
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInterview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String candidateName = (String) request.get("candidateName");
            String candidateEmail = (String) request.get("candidateEmail");
            LocalDate interviewDate = request.get("interviewDate") != null ?
                LocalDate.parse((String) request.get("interviewDate")) : null;
            LocalTime interviewTimeFrom = request.get("interviewTimeFrom") != null ?
                LocalTime.parse((String) request.get("interviewTimeFrom")) : null;
            LocalTime interviewTimeTo = request.get("interviewTimeTo") != null ?
                LocalTime.parse((String) request.get("interviewTimeTo")) : null;
            String jrs = (String) request.get("jrs");

            Interview updated = interviewService.updateInterview(
                id, candidateName, candidateEmail,
                interviewDate, interviewTimeFrom, interviewTimeTo, jrs
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview updated successfully",
                "interview", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Delete interview
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInterview(@PathVariable Long id) {
        try {
            interviewService.deleteInterview(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Cancel interview
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelInterview(@PathVariable Long id) {
        try {
            Interview cancelled = interviewService.cancelInterview(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview cancelled successfully",
                "interview", cancelled
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Complete interview
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeInterview(@PathVariable Long id) {
        try {
            Interview completed = interviewService.completeInterview(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview marked as completed",
                "interview", completed
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Reschedule interview
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleInterview(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            LocalDate newDate = LocalDate.parse(request.get("newDate"));
            LocalTime newTimeFrom = LocalTime.parse(request.get("newTimeFrom"));
            LocalTime newTimeTo = LocalTime.parse(request.get("newTimeTo"));
            
            Interview rescheduled = interviewService.rescheduleInterview(id, newDate, newTimeFrom, newTimeTo);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview rescheduled successfully",
                "interview", rescheduled
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Submit feedback for an interview
     */
    @PutMapping("/{id}/feedback")
    public ResponseEntity<?> submitFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String feedback = request.get("feedback");
            String decision = request.get("decision");

            Interview updated = interviewService.submitFeedback(id, feedback, decision);
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

    /**
     * Get interview statistics for a panelist
     */
    @GetMapping("/panelist/{panelistId}/stats")
    public ResponseEntity<?> getInterviewStats(@PathVariable Long panelistId) {
        try {
            long total = interviewService.getInterviewCountByPanelist(panelistId);
            long scheduled = interviewService.getInterviewCountByPanelistAndStatus(
                panelistId, InterviewStatus.SCHEDULED
            );
            long completed = interviewService.getInterviewCountByPanelistAndStatus(
                panelistId, InterviewStatus.COMPLETED
            );
            long cancelled = interviewService.getInterviewCountByPanelistAndStatus(
                panelistId, InterviewStatus.CANCELLED
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", Map.of(
                    "total", total,
                    "scheduled", scheduled,
                    "completed", completed,
                    "cancelled", cancelled
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all interviews (admin/HR use)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllInterviews() {
        try {
            List<Interview> interviews = interviewService.getAllInterviews();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get candidate's interviews with HR details by candidate ID
     */
    @GetMapping("/candidate/id/{candidateId}/details")
    public ResponseEntity<?> getCandidateInterviewsWithDetails(@PathVariable Long candidateId) {
        try {
            List<CandidateInterviewDTO> interviews = interviewService.getCandidateInterviewsWithDetails(candidateId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch interviews: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get candidate's interviews with HR details by candidate email
     */
    @GetMapping("/candidate/email/{candidateEmail}/details")
    public ResponseEntity<?> getCandidateInterviewsByEmailWithDetails(@PathVariable String candidateEmail) {
        try {
            List<CandidateInterviewDTO> interviews = interviewService.getCandidateInterviewsByEmailWithDetails(candidateEmail);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch interviews: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get all interviews for an HR
     */
    @GetMapping("/hr/{hrId}")
    public ResponseEntity<?> getInterviewsByHrId(@PathVariable Long hrId) {
        try {
            List<Interview> interviews = interviewService.getInterviewsByHrId(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch interviews: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get interviews by HR ID and status
     */
    @GetMapping("/hr/{hrId}/status/{status}")
    public ResponseEntity<?> getInterviewsByHrIdAndStatus(
            @PathVariable Long hrId,
            @PathVariable String status) {
        try {
            InterviewStatus interviewStatus = InterviewStatus.valueOf(status.toUpperCase());
            List<Interview> interviews = interviewService.getInterviewsByHrIdAndStatus(hrId, interviewStatus);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews,
                "count", interviews.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Failed to fetch interviews: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Manually trigger update of expired interviews
     * Checks all SCHEDULED interviews and marks them as COMPLETED if their end time has passed
     */
    @PostMapping("/update-expired")
    public ResponseEntity<?> updateExpiredInterviews() {
        try {
            int updatedCount = interviewSchedulerService.updateExpiredInterviews();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview statuses updated successfully",
                "updatedCount", updatedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to update interview statuses: " + e.getMessage()
            ));
        }
    }
}

// Made with Bob