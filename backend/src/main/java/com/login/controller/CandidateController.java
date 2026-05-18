package com.login.controller;

import com.login.dto.CandidateDTO;
import com.login.model.Candidate;
import com.login.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Candidate Controller - REST API endpoints for candidate management
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "http://localhost:3000")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    /**
     * Create a new candidate
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCandidate(@RequestBody Map<String, Object> request) {
        try {
            Candidate candidate = new Candidate();
            candidate.setName((String) request.get("name"));
            candidate.setEmail((String) request.get("email"));
            candidate.setPhone((String) request.get("phone"));
            candidate.setPosition((String) request.get("position"));
            candidate.setExperienceYears((Integer) request.get("experienceYears"));
            candidate.setSkills((String) request.get("skills"));

            Long hrId = Long.valueOf(request.get("hrId").toString());
            Candidate created = candidateService.createCandidate(candidate, hrId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate created successfully",
                "candidate", created
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all candidates
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllCandidates() {
        try {
            List<Candidate> candidates = candidateService.getAllCandidates();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "candidates", candidates
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get candidate by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
        try {
            return candidateService.getCandidateById(id)
                .map(candidate -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "candidate", candidate
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Candidate not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get candidates by HR with full details
     */
    @GetMapping("/hr/{hrId}")
    public ResponseEntity<?> getCandidatesByHr(@PathVariable Long hrId) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByHr(hrId);
            // Convert to DTOs to include all fields and assigned panelist info
            List<CandidateDTO> candidateDTOs = candidates.stream()
                .map(CandidateDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "candidates", candidateDTOs
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get candidates by panelist
     */
    @GetMapping("/panelist/{panelistId}")
    public ResponseEntity<?> getCandidatesByPanelist(@PathVariable Long panelistId) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByPanelist(panelistId);
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
     * Assign panelist to candidate
     */
    @PutMapping("/{candidateId}/assign-panelist")
    public ResponseEntity<?> assignPanelist(@PathVariable Long candidateId, @RequestBody Map<String, Object> request) {
        try {
            Long panelistId = Long.valueOf(request.get("panelistId").toString());
            Long hrId = Long.valueOf(request.get("hrId").toString());

            Candidate updated = candidateService.assignPanelistToCandidate(candidateId, panelistId, hrId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist assigned successfully",
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
     * Update candidate status
     */
    @PutMapping("/{candidateId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long candidateId, @RequestBody Map<String, Object> request) {
        try {
            String status = (String) request.get("status");
            Long userId = Long.valueOf(request.get("userId").toString());

            Candidate updated = candidateService.updateCandidateStatus(candidateId, status, userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Status updated successfully",
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
     * Update candidate details
     */
    @PutMapping("/{candidateId}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long candidateId, @RequestBody Map<String, Object> request) {
        try {
            Candidate updatedCandidate = new Candidate();
            updatedCandidate.setName((String) request.get("name"));
            updatedCandidate.setEmail((String) request.get("email"));
            updatedCandidate.setPhone((String) request.get("phone"));
            updatedCandidate.setPosition((String) request.get("position"));
            updatedCandidate.setExperienceYears((Integer) request.get("experienceYears"));
            updatedCandidate.setSkills((String) request.get("skills"));

            Long hrId = Long.valueOf(request.get("hrId").toString());
            Candidate updated = candidateService.updateCandidate(candidateId, updatedCandidate, hrId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate updated successfully",
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
     * Delete candidate
     */
    @DeleteMapping("/{candidateId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long candidateId, @RequestParam Long hrId) {
        try {
            candidateService.deleteCandidate(candidateId, hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Save candidate information (for candidates to fill their own info)
     * Sends candidate details as PDF to HR email
     */
    @PostMapping("/save-info")
    public ResponseEntity<?> saveCandidateInfo(
            @RequestParam("candidateName") String candidateName,
            @RequestParam("mailId") String mailId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("location") String location,
            @RequestParam(value = "currentCtc", required = false) String currentCtc,
            @RequestParam(value = "hrMailId", required = false) String hrMailId,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "experienceYears", required = false) Integer experienceYears,
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "cv", required = false) MultipartFile cv,
            @RequestParam(value = "gvtId", required = false) MultipartFile gvtId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            
            Map<String, Object> result = candidateService.saveCandidateInfo(
                username, candidateName, mailId, phoneNumber, location,
                currentCtc, hrMailId, position, experienceYears, skills,
                photo, cv, gvtId
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate information saved successfully",
                "data", result
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace for debugging
            System.err.println("Error saving candidate info: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error occurred"
            ));
        }
    }
}

// Made with Bob