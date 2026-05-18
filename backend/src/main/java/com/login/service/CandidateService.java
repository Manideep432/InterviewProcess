package com.login.service;

import com.login.model.Candidate;
import com.login.model.User;
import com.login.repository.CandidateRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Candidate Service - Business logic for candidate management
 * 
 * @author Bob
 */
@Service
@Transactional
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    /**
     * Create a new candidate
     */
    public Candidate createCandidate(Candidate candidate, Long hrId) {
        Optional<User> hr = userRepository.findById(hrId);
        if (hr.isEmpty() || !"HR".equals(hr.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }
        
        if (candidateRepository.existsByEmail(candidate.getEmail())) {
            throw new RuntimeException("Candidate with this email already exists");
        }
        
        candidate.setHr(hr.get());
        candidate.setStatus("APPLIED");
        return candidateRepository.save(candidate);
    }

    /**
     * Get all candidates
     */
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    /**
     * Get candidate by ID
     */
    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    /**
     * Get candidates by HR
     */
    public List<Candidate> getCandidatesByHr(Long hrId) {
        Optional<User> hr = userRepository.findById(hrId);
        if (hr.isEmpty()) {
            throw new RuntimeException("HR not found");
        }
        return candidateRepository.findByHr(hr.get());
    }

    /**
     * Get candidates assigned to a panelist
     */
    public List<Candidate> getCandidatesByPanelist(Long panelistId) {
        Optional<User> panelist = userRepository.findById(panelistId);
        if (panelist.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        return candidateRepository.findByAssignedPanelist(panelist.get());
    }

    /**
     * Track candidate login - updates last login time and login status
     */
    public void trackCandidateLogin(String email) {
        Optional<Candidate> candidateOpt = candidateRepository.findByEmail(email);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            candidate.setLastLoginAt(java.time.LocalDateTime.now());
            candidate.setIsLoggedIn(true);
            candidateRepository.save(candidate);
            System.out.println("Tracked login for candidate: " + email + " at " + candidate.getLastLoginAt());
        }
    }

    /**
     * Assign panelist to candidate
     */
    public Candidate assignPanelistToCandidate(Long candidateId, Long panelistId, Long hrId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }
        
        Candidate candidate = candidateOpt.get();
        
        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to assign panelist to this candidate");
        }
        
        Optional<User> panelist = userRepository.findById(panelistId);
        if (panelist.isEmpty() || !"PANELIST".equals(panelist.get().getRole())) {
            throw new RuntimeException("Invalid Panelist ID");
        }
        
        candidate.setAssignedPanelist(panelist.get());
        candidate.setStatus("SCREENING");
        return candidateRepository.save(candidate);
    }

    /**
     * Update candidate status
     */
    public Candidate updateCandidateStatus(Long candidateId, String status, Long userId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }
        
        Candidate candidate = candidateOpt.get();
        
        // Verify user has permission (HR who owns candidate or assigned panelist)
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        boolean hasPermission = candidate.getHr().getId().equals(userId) ||
                               (candidate.getAssignedPanelist() != null && 
                                candidate.getAssignedPanelist().getId().equals(userId));
        
        if (!hasPermission) {
            throw new RuntimeException("You don't have permission to update this candidate");
        }
        
        candidate.setStatus(status);
        return candidateRepository.save(candidate);
    }

    /**
     * Update candidate details
     */
    public Candidate updateCandidate(Long candidateId, Candidate updatedCandidate, Long hrId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }
        
        Candidate candidate = candidateOpt.get();
        
        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to update this candidate");
        }
        
        candidate.setName(updatedCandidate.getName());
        candidate.setEmail(updatedCandidate.getEmail());
        candidate.setPhone(updatedCandidate.getPhone());
        candidate.setPosition(updatedCandidate.getPosition());
        candidate.setExperienceYears(updatedCandidate.getExperienceYears());
        candidate.setSkills(updatedCandidate.getSkills());
        
        return candidateRepository.save(candidate);
    }

    /**
     * Delete candidate
     */
    public void deleteCandidate(Long candidateId, Long hrId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }
        
        Candidate candidate = candidateOpt.get();
        
        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to delete this candidate");
        }
        
        candidateRepository.delete(candidate);
    }

    /**
     * Save candidate information with file uploads
     * Automatically assigns candidate to the first available HR
     * Sends candidate details as PDF to HR email
     */
    public Map<String, Object> saveCandidateInfo(
            String username,
            String candidateName,
            String mailId,
            String phoneNumber,
            String location,
            String currentCtc,
            String hrMailId,
            String position,
            Integer experienceYears,
            String skills,
            MultipartFile photo,
            MultipartFile cv,
            MultipartFile gvtId) {
        
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Check if user is a candidate
        if (!"CANDIDATE".equals(user.getRole())) {
            throw new RuntimeException("Only candidates can save their information");
        }
        
        // Find or create candidate record
        Optional<Candidate> candidateOpt = candidateRepository.findByEmail(mailId);
        Candidate candidate;
        boolean isNewCandidate = false;
        
        if (candidateOpt.isPresent()) {
            candidate = candidateOpt.get();
        } else {
            candidate = new Candidate();
            candidate.setStatus("APPLIED");
            isNewCandidate = true;
        }
        
        // Update candidate information
        candidate.setName(candidateName);
        candidate.setEmail(mailId);
        candidate.setPhone(phoneNumber);
        candidate.setLocation(location);
        
        // Set current CTC if provided
        if (currentCtc != null && !currentCtc.isEmpty()) {
            try {
                candidate.setCurrentCtc(new BigDecimal(currentCtc));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid CTC format");
            }
        }
        
        // Set HR Mail ID if provided
        if (hrMailId != null && !hrMailId.isEmpty()) {
            candidate.setHrMailId(hrMailId);
        }
        
        // Set Position if provided
        if (position != null && !position.isEmpty()) {
            candidate.setPosition(position);
        }
        
        // Set Experience Years if provided
        if (experienceYears != null) {
            candidate.setExperienceYears(experienceYears);
        }
        
        // Set Skills if provided (up to 200 words)
        if (skills != null && !skills.isEmpty()) {
            candidate.setSkills(skills);
        }
        
        // Auto-assign to HR if not already assigned
        if (candidate.getHr() == null) {
            // Find first available HR user
            List<User> hrUsers = userRepository.findByRole("HR");
            if (!hrUsers.isEmpty()) {
                // Assign to first HR found
                candidate.setHr(hrUsers.get(0));
                System.out.println("Auto-assigned candidate " + candidateName + " to HR: " + hrUsers.get(0).getUsername());
            } else {
                System.err.println("WARNING: No HR users found in system. Candidate will not be assigned to any HR.");
            }
        }
        
        // Handle file uploads
        Map<String, String> uploadedFiles = new HashMap<>();
        
        try {
            // Create upload directory if it doesn't exist
            String uploadDir = "uploads/candidates/" + user.getId();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save photo
            if (photo != null && !photo.isEmpty()) {
                String photoFileName = saveFile(photo, uploadPath, "photo");
                uploadedFiles.put("photo", photoFileName);
            }
            
            // Save CV
            if (cv != null && !cv.isEmpty()) {
                String cvFileName = saveFile(cv, uploadPath, "cv");
                uploadedFiles.put("cv", cvFileName);
            }
            
            // Save Government ID
            if (gvtId != null && !gvtId.isEmpty()) {
                String gvtIdFileName = saveFile(gvtId, uploadPath, "gvtId");
                uploadedFiles.put("gvtId", gvtIdFileName);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload files: " + e.getMessage());
        }
        
        // Save candidate
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        // Generate PDF and send email to HR if HR email is provided
        if (hrMailId != null && !hrMailId.isEmpty()) {
            try {
                byte[] pdfContent = pdfGenerationService.generateCandidatePdf(savedCandidate);
                emailService.sendCandidateDetailsToHR(hrMailId, candidateName, pdfContent);
            } catch (Exception e) {
                System.err.println("Failed to send email to HR: " + e.getMessage());
                // Don't fail the entire operation if email fails
            }
        }
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("candidateId", savedCandidate.getId());
        response.put("name", savedCandidate.getName());
        response.put("email", savedCandidate.getEmail());
        response.put("phone", savedCandidate.getPhone());
        response.put("uploadedFiles", uploadedFiles);
        response.put("emailSent", hrMailId != null && !hrMailId.isEmpty());
        
        return response;
    }
    
    /**
     * Helper method to save uploaded files
     */
    private String saveFile(MultipartFile file, Path uploadPath, String fileType) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = fileType + "_" + UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFileName;
    }
}

// Made with Bob