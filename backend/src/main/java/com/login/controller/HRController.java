package com.login.controller;

import com.login.model.Candidate;
import com.login.model.Panelist;
import com.login.service.HRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * HR Controller - REST API endpoints for HR operations
 * HR can access both Panelist and Candidate data
 *
 * @author Bob
 */
@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
             allowedHeaders = "*",
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowCredentials = "true")
public class HRController {

    @Autowired
    private HRService hrService;

    /**
     * Get HR dashboard with overview of panelists and candidates
     * Enhanced with better error handling and logging
     */
    @GetMapping("/{hrId}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Long hrId) {
        try {
            System.out.println("=== HR Dashboard Request ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Timestamp: " + new java.util.Date());
            
            Map<String, Object> dashboard = hrService.getHRDashboard(hrId);
            
            System.out.println("Dashboard data retrieved successfully");
            System.out.println("Total Candidates: " + dashboard.get("totalCandidates"));
            System.out.println("Total Panelists: " + dashboard.get("totalPanelists"));
            System.out.println("===========================");
            
            return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "http://localhost:3000")
                .header("Access-Control-Allow-Credentials", "true")
                .body(Map.of(
                    "success", true,
                    "dashboard", dashboard,
                    "timestamp", System.currentTimeMillis()
                ));
        } catch (RuntimeException e) {
            System.err.println("=== HR Dashboard Error ===");
            System.err.println("Error Type: RuntimeException");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===========================");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "http://localhost:3000")
                .header("Access-Control-Allow-Credentials", "true")
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "error", "RUNTIME_ERROR"
                ));
        } catch (Exception e) {
            System.err.println("=== HR Dashboard Error ===");
            System.err.println("Error Type: Exception");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===========================");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Access-Control-Allow-Origin", "http://localhost:3000")
                .header("Access-Control-Allow-Credentials", "true")
                .body(Map.of(
                    "success", false,
                    "message", "An unexpected error occurred: " + e.getMessage(),
                    "error", "INTERNAL_ERROR"
                ));
        }
    }

    /**
     * Assign panelist to candidate
     */
    @PostMapping("/{hrId}/assign-panelist")
    public ResponseEntity<?> assignPanelistToCandidate(@PathVariable Long hrId, @RequestBody Map<String, Object> request) {
        try {
            Long candidateId = Long.valueOf(request.get("candidateId").toString());
            Long panelistUserId = Long.valueOf(request.get("panelistUserId").toString());

            Candidate updated = hrService.assignPanelistToCandidate(hrId, candidateId, panelistUserId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist assigned to candidate successfully",
                "candidate", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get statistics for HR
     */
    @GetMapping("/{hrId}/statistics")
    public ResponseEntity<?> getStatistics(@PathVariable Long hrId) {
        try {
            Map<String, Object> stats = hrService.getStatistics(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all panelists with detailed information
     */
    @GetMapping("/{hrId}/all-panelists")
    public ResponseEntity<?> getAllPanelists(@PathVariable Long hrId) {
        try {
            List<Map<String, Object>> panelists = hrService.getAllPanelistsWithDetails(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "panelists", panelists
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all candidates with detailed information
     */
    @GetMapping("/{hrId}/all-candidates")
    public ResponseEntity<?> getAllCandidates(@PathVariable Long hrId) {
        try {
            List<Map<String, Object>> candidates = hrService.getAllCandidatesWithDetails(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "candidates", candidates
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get real-time dashboard updates (for polling)
     */
    @GetMapping("/{hrId}/dashboard-updates")
    public ResponseEntity<?> getDashboardUpdates(@PathVariable Long hrId) {
        try {
            Map<String, Object> updates = hrService.getDashboardUpdates(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "updates", updates
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Create a new candidate with login credentials
     */
    @PostMapping("/{hrId}/create-candidate")
    public ResponseEntity<?> createCandidate(@PathVariable Long hrId, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== Creating New Candidate ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Request: " + request);
            
            Map<String, Object> result = hrService.createCandidateWithLogin(hrId, request);
            
            System.out.println("Candidate created successfully");
            System.out.println("==============================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate created successfully",
                "candidate", result.get("candidate"),
                "user", result.get("user")
            ));
        } catch (Exception e) {
            System.err.println("Error creating candidate: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all candidates created by this HR
     */
    @GetMapping("/{hrId}/my-candidates")
    public ResponseEntity<?> getMyCandidates(@PathVariable Long hrId) {
        try {
            System.out.println("=== Fetching HR's Candidates ===");
            System.out.println("HR ID: " + hrId);
            
            List<Map<String, Object>> candidates = hrService.getHRCreatedCandidates(hrId);
            
            System.out.println("Candidates count: " + candidates.size());
            System.out.println("================================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "candidates", candidates,
                "count", candidates.size()
            ));
        } catch (Exception e) {
            System.err.println("Error fetching HR's candidates: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update candidate details
     */
    @PutMapping("/{hrId}/update-candidate/{candidateId}")
    public ResponseEntity<?> updateCandidate(
            @PathVariable Long hrId,
            @PathVariable Long candidateId,
            @RequestBody Map<String, Object> updates) {
        try {
            System.out.println("=== Updating Candidate ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Candidate ID: " + candidateId);
            System.out.println("Updates: " + updates);
            
            Map<String, Object> result = hrService.updateCandidate(hrId, candidateId, updates);
            
            System.out.println("Candidate updated successfully");
            System.out.println("==========================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate updated successfully",
                "candidate", result
            ));
        } catch (Exception e) {
            System.err.println("Error updating candidate: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Delete candidate
     */
    @DeleteMapping("/{hrId}/delete-candidate/{candidateId}")
    public ResponseEntity<?> deleteCandidate(
            @PathVariable Long hrId,
            @PathVariable Long candidateId) {
        try {
            System.out.println("=== Deleting Candidate ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Candidate ID: " + candidateId);
            
            hrService.deleteCandidate(hrId, candidateId);
            
            System.out.println("Candidate deleted successfully");
            System.out.println("==========================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate deleted successfully"
            ));
        } catch (Exception e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get HR profile
     */
    @GetMapping("/{hrId}/profile")
    public ResponseEntity<?> getHRProfile(@PathVariable Long hrId) {
        try {
            System.out.println("=== Fetching HR Profile ===");
            System.out.println("HR ID: " + hrId);
            
            var profile = hrService.getHRProfile(hrId);
            
            System.out.println("HR Profile retrieved successfully");
            System.out.println("===========================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "profile", profile
            ));
        } catch (Exception e) {
            System.err.println("Error fetching HR profile: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Create or update HR profile
     */
    @PostMapping("/{hrId}/profile")
    public ResponseEntity<?> createOrUpdateHRProfile(
            @PathVariable Long hrId,
            @RequestBody Map<String, Object> profileData) {
        try {
            System.out.println("=== Creating/Updating HR Profile ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Profile Data: " + profileData);
            
            var profile = hrService.createOrUpdateHRProfile(hrId, profileData);
            
            System.out.println("HR Profile saved successfully");
            System.out.println("====================================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile saved successfully",
                "profile", profile
            ));
        } catch (Exception e) {
            System.err.println("Error saving HR profile: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Search panelist by email
     */
    @GetMapping("/{hrId}/search-panelist")
    public ResponseEntity<?> searchPanelistByEmail(
            @PathVariable Long hrId,
            @RequestParam String email) {
        try {
            System.out.println("=== Searching Panelist by Email ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Email: " + email);
            
            Map<String, Object> panelist = hrService.searchPanelistByEmail(hrId, email);
            
            if (panelist != null) {
                System.out.println("Panelist found: " + panelist.get("username"));
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "panelist", panelist
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Panelist not found with this email"
                ));
            }
        } catch (Exception e) {
            System.err.println("Error searching panelist: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Schedule interview for a candidate
     */
    @PostMapping("/{hrId}/schedule-interview")
    public ResponseEntity<?> scheduleInterview(
            @PathVariable Long hrId,
            @RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== Scheduling Interview ===");
            System.out.println("HR ID: " + hrId);
            System.out.println("Request: " + request);
            
            Map<String, Object> result = hrService.scheduleInterview(hrId, request);
            
            System.out.println("Interview scheduled successfully");
            System.out.println("============================");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interview scheduled successfully. Notifications sent to candidate and panelist.",
                "interview", result
            ));
        } catch (Exception e) {
            System.err.println("Error scheduling interview: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

// Made with Bob