package com.login.controller;

import com.login.dto.InterviewFeedbackDTO;
import com.login.model.InterviewFeedback;
import com.login.service.InterviewFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * InterviewFeedbackController - REST API endpoints for interview feedback management
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/interview-feedback")
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewFeedbackController {

    @Autowired
    private InterviewFeedbackService feedbackService;

    /**
     * Submit technical interview feedback
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody InterviewFeedbackDTO feedbackDTO) {
        try {
            // Get current user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Extract user ID from token (assuming it's stored in the principal)
            Long userId = Long.parseLong(authentication.getPrincipal().toString());

            InterviewFeedback feedback = feedbackService.submitFeedback(userId, feedbackDTO);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Technical Interview Assessment Form submitted successfully and sent to HR",
                "feedback", feedback
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get feedback by interview ID
     */
    @GetMapping("/interview/{interviewId}")
    public ResponseEntity<?> getFeedbackByInterview(@PathVariable Long interviewId) {
        try {
            return feedbackService.getFeedbackByInterviewId(interviewId)
                .map(feedback -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "feedback", feedback
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Feedback not found for this interview"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all feedback by panelist
     */
    @GetMapping("/panelist/{panelistId}")
    public ResponseEntity<?> getFeedbackByPanelist(@PathVariable Long panelistId) {
        try {
            List<InterviewFeedback> feedbackList = feedbackService.getFeedbackByPanelist(panelistId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all feedback by candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getFeedbackByCandidate(@PathVariable Long candidateId) {
        try {
            List<InterviewFeedback> feedbackList = feedbackService.getFeedbackByCandidate(candidateId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all feedback (HR only)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback() {
        try {
            List<InterviewFeedback> feedbackList = feedbackService.getAllFeedback();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get feedback by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getFeedbackByStatus(@PathVariable String status) {
        try {
            List<InterviewFeedback> feedbackList = feedbackService.getFeedbackByStatus(status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get feedback by recommendation
     */
    @GetMapping("/recommendation/{recommendation}")
    public ResponseEntity<?> getFeedbackByRecommendation(@PathVariable String recommendation) {
        try {
            List<InterviewFeedback> feedbackList = feedbackService.getFeedbackByRecommendation(recommendation);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Resend feedback to HR
     */
    @PostMapping("/{feedbackId}/resend")
    public ResponseEntity<?> resendFeedbackToHR(@PathVariable Long feedbackId) {
        try {
            feedbackService.resendFeedbackToHR(feedbackId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Feedback resent to HR successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

// Made with Bob