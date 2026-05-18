package com.login.controller;

import com.login.dto.PanelistProfileDTO;
import com.login.model.Panelist;
import com.login.service.PanelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Panelist Controller - REST API endpoints for panelist management
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/panelists")
@CrossOrigin(origins = "http://localhost:3000")
public class PanelistController {

    @Autowired
    private PanelistService panelistService;

    /**
     * Create a new panelist (assigned by HR)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPanelist(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long hrId = Long.valueOf(request.get("hrId").toString());
            String specialization = (String) request.get("specialization");
            Integer experienceYears = request.get("experienceYears") != null ? 
                Integer.valueOf(request.get("experienceYears").toString()) : null;
            String expertise = (String) request.get("expertise");

            Panelist created = panelistService.createPanelist(userId, hrId, specialization, experienceYears, expertise);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist created successfully",
                "panelist", created
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all panelists
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllPanelists() {
        try {
            List<Panelist> panelists = panelistService.getAllPanelists();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "panelists", panelists
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get panelist by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPanelistById(@PathVariable Long id) {
        try {
            return panelistService.getPanelistById(id)
                .map(panelist -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "panelist", panelist
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Panelist not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get panelist by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPanelistByUserId(@PathVariable Long userId) {
        try {
            return panelistService.getPanelistByUserId(userId)
                .map(panelist -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "panelist", panelist
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Panelist not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get panelists by HR
     */
    @GetMapping("/hr/{hrId}")
    public ResponseEntity<?> getPanelistsByHr(@PathVariable Long hrId) {
        try {
            List<Panelist> panelists = panelistService.getPanelistsByHr(hrId);
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
     * Get active panelists by HR
     */
    @GetMapping("/hr/{hrId}/active")
    public ResponseEntity<?> getActivePanelistsByHr(@PathVariable Long hrId) {
        try {
            List<Panelist> panelists = panelistService.getActivePanelistsByHr(hrId);
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
     * Get all active panelists
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActivePanelists() {
        try {
            List<Panelist> panelists = panelistService.getActivePanelists();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "panelists", panelists
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update panelist details
     */
    @PutMapping("/{panelistId}")
    public ResponseEntity<?> updatePanelist(@PathVariable Long panelistId, @RequestBody Map<String, Object> request) {
        try {
            String specialization = (String) request.get("specialization");
            Integer experienceYears = request.get("experienceYears") != null ? 
                Integer.valueOf(request.get("experienceYears").toString()) : null;
            String expertise = (String) request.get("expertise");
            Long hrId = Long.valueOf(request.get("hrId").toString());

            Panelist updated = panelistService.updatePanelist(panelistId, specialization, experienceYears, expertise, hrId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist updated successfully",
                "panelist", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Toggle panelist active status
     */
    @PutMapping("/{panelistId}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long panelistId, @RequestParam Long hrId) {
        try {
            Panelist updated = panelistService.togglePanelistStatus(panelistId, hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist status updated successfully",
                "panelist", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Delete panelist
     */
    @DeleteMapping("/{panelistId}")
    public ResponseEntity<?> deletePanelist(@PathVariable Long panelistId, @RequestParam Long hrId) {
        try {
            panelistService.deletePanelist(panelistId, hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Panelist deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get assigned HR name for a panelist
     */
    @GetMapping("/user/{userId}/hr-name")
    public ResponseEntity<?> getAssignedHrName(@PathVariable Long userId) {
        try {
            String hrName = panelistService.getAssignedHrName(userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "hrName", hrName
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get panelist profile by user ID
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getPanelistProfile(@PathVariable Long userId) {
        try {
            PanelistProfileDTO profile = panelistService.getPanelistProfile(userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "profile", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update panelist profile
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updatePanelistProfile(
            @PathVariable Long userId,
            @RequestBody PanelistProfileDTO profileDTO) {
        try {
            PanelistProfileDTO updated = panelistService.updatePanelistProfile(userId, profileDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "profile", updated
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