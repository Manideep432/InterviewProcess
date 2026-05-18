package com.login.controller;

import com.login.dto.InterviewFeedbackDTO;
import com.login.model.InterviewFeedback;
import com.login.model.User;
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = extractUserId(authentication);

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
            List<Map<String, Object>> feedbackSummaryList = feedbackList.stream()
                .map(feedback -> Map.<String, Object>of(
                    "id", feedback.getId(),
                    "candidateName", feedback.getCandidateName() != null ? feedback.getCandidateName() : "",
                    "jobRoleSpecification", feedback.getJobRoleSpecification() != null ? feedback.getJobRoleSpecification() : "",
                    "evaluationDate", feedback.getEvaluationDate(),
                    "overallRating", feedback.getOverallRating(),
                    "techPanelRecommendation", feedback.getTechPanelRecommendation() != null ? feedback.getTechPanelRecommendation() : "",
                    "status", feedback.getStatus() != null ? feedback.getStatus() : "",
                    "createdAt", feedback.getCreatedAt()
                ))
                .toList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "feedbackList", feedbackSummaryList
            ));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching feedbacks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch feedbacks. Check backend data/schema consistency.",
                "error", e.getClass().getSimpleName(),
                "details", e.getMessage() != null ? e.getMessage() : "Unknown server error"
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
     * Download feedback PDF by ID
     */
    @GetMapping("/{feedbackId}/download-pdf")
    public ResponseEntity<?> downloadFeedbackPdf(@PathVariable Long feedbackId) {
        try {
            byte[] pdfContent = feedbackService.generateFeedbackPdf(feedbackId);
            
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=interview-feedback-" + feedbackId + ".pdf")
                .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to generate PDF: " + e.getMessage()
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

    /**
     * Extract numeric user id from authenticated principal safely.
     */
    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long longPrincipal) {
            return longPrincipal;
        }

        if (principal instanceof Integer intPrincipal) {
            return intPrincipal.longValue();
        }

        if (principal instanceof User userPrincipal) {
            return userPrincipal.getId();
        }

        String username = authentication.getName();
        return feedbackService.getUserIdByUsername(username);
    }
}

// Made with Bob