package com.login.service;

import com.login.model.Candidate;
import com.login.model.Interview;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.model.PasswordHistory;
import com.login.model.HRProfile;
import com.login.dto.HRProfileDTO;
import com.login.repository.CandidateRepository;
import com.login.repository.InterviewRepository;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import com.login.repository.PasswordHistoryRepository;
import com.login.repository.HRProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HR Service - Business logic for HR operations
 * Provides access to both Panelist and Candidate data
 * 
 * @author Bob
 */
@Service
public class HRService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanelistRepository panelistRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private HRProfileRepository hrProfileRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    /**
     * Get HR dashboard data with comprehensive information including interviews
     */
    public Map<String, Object> getHRDashboard(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();
        Map<String, Object> dashboard = new HashMap<>();

        // Get ALL panelists in the system (not just assigned to this HR)
        List<Panelist> panelists = panelistRepository.findAll();
        dashboard.put("panelists", panelists);
        dashboard.put("totalPanelists", panelists.size());
        dashboard.put("activePanelists", panelists.stream().filter(Panelist::isActive).count());

        // Get ALL candidates in the system (not just managed by this HR)
        List<Candidate> candidates = candidateRepository.findAll();
        dashboard.put("candidates", candidates);
        dashboard.put("totalCandidates", candidates.size());

        // Candidate statistics by status
        Map<String, Long> candidatesByStatus = new HashMap<>();
        candidatesByStatus.put("APPLIED", candidates.stream().filter(c -> "APPLIED".equals(c.getStatus())).count());
        candidatesByStatus.put("SCREENING", candidates.stream().filter(c -> "SCREENING".equals(c.getStatus())).count());
        candidatesByStatus.put("INTERVIEW", candidates.stream().filter(c -> "INTERVIEW".equals(c.getStatus())).count());
        candidatesByStatus.put("SELECTED", candidates.stream().filter(c -> "SELECTED".equals(c.getStatus())).count());
        candidatesByStatus.put("REJECTED", candidates.stream().filter(c -> "REJECTED".equals(c.getStatus())).count());
        dashboard.put("candidatesByStatus", candidatesByStatus);

        // Get all interviews for panelists under this HR
        List<Long> panelistIds = panelists.stream()
                .map(p -> p.getUser().getId())
                .collect(Collectors.toList());
        
        List<Interview> allInterviews = new ArrayList<>();
        for (Long panelistId : panelistIds) {
            allInterviews.addAll(interviewRepository.findByPanelistId(panelistId));
        }
        dashboard.put("interviews", allInterviews);
        dashboard.put("totalInterviews", allInterviews.size());

        // Build comprehensive dashboard data with all required fields
        List<Map<String, Object>> dashboardRecords = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Map<String, Object> record = new HashMap<>();
            record.put("candidateId", candidate.getId());
            record.put("candidateName", candidate.getName());
            record.put("candidateEmail", candidate.getEmail());
            record.put("candidatePhone", candidate.getPhone());
            
            // Add login tracking information
            record.put("isLoggedIn", candidate.getIsLoggedIn() != null ? candidate.getIsLoggedIn() : false);
            record.put("lastLoginAt", candidate.getLastLoginAt());
            
            // Panelist information
            if (candidate.getAssignedPanelist() != null) {
                record.put("panelistName", candidate.getAssignedPanelist().getUsername());
                record.put("panelistEmail", candidate.getAssignedPanelist().getEmail());
            } else {
                record.put("panelistName", "Not Assigned");
                record.put("panelistEmail", "N/A");
            }
            
            // JD and position details
            record.put("jdDetails", candidate.getJdDetails());
            record.put("position", candidate.getPosition());
            
            // Interview details - find interview for this candidate
            Interview candidateInterview = allInterviews.stream()
                    .filter(i -> i.getCandidateEmail().equals(candidate.getEmail()))
                    .findFirst()
                    .orElse(null);
            
            if (candidateInterview != null) {
                record.put("interviewDate", candidateInterview.getInterviewDate());
                record.put("interviewTime", candidateInterview.getInterviewTimeFrom());
                record.put("interviewStatus", candidateInterview.getStatus());
            } else {
                record.put("interviewDate", null);
                record.put("interviewTime", null);
                record.put("interviewStatus", "Not Scheduled");
            }
            
            // Status and dates
            record.put("status", candidate.getStatus());
            record.put("joiningDate", candidate.getJoiningDate());
            
            // CTC information
            record.put("oldCtc", candidate.getOldCtc());
            record.put("newCtc", candidate.getNewCtc());
            
            // Employment details
            record.put("employmentType", candidate.getEmploymentType());
            record.put("location", candidate.getLocation());
            record.put("experienceYears", candidate.getExperienceYears());
            record.put("skills", candidate.getSkills());
            
            dashboardRecords.add(record);
        }
        
        dashboard.put("dashboardRecords", dashboardRecords);

        return dashboard;
    }


    /**
     * Assign panelist to candidate
     */
    public Candidate assignPanelistToCandidate(Long hrId, Long candidateId, Long panelistUserId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOpt.get();

        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to assign panelist to this candidate");
        }

        Optional<User> panelistUserOpt = userRepository.findById(panelistUserId);
        if (panelistUserOpt.isEmpty() || !"PANELIST".equals(panelistUserOpt.get().getRole())) {
            throw new RuntimeException("Invalid Panelist");
        }

        // Verify panelist is assigned to this HR
        Optional<Panelist> panelistOpt = panelistRepository.findByUser(panelistUserOpt.get());
        if (panelistOpt.isEmpty() || !panelistOpt.get().getAssignedHr().getId().equals(hrId)) {
            throw new RuntimeException("This panelist is not assigned to you");
        }

        candidate.setAssignedPanelist(panelistUserOpt.get());
        candidate.setStatus("SCREENING");
        return candidateRepository.save(candidate);
    }

    /**
     * Get statistics for HR
     */
    public Map<String, Object> getStatistics(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();
        Map<String, Object> stats = new HashMap<>();

        List<Panelist> panelists = panelistRepository.findByAssignedHr(hr);
        List<Candidate> candidates = candidateRepository.findByHr(hr);

        stats.put("totalPanelists", panelists.size());
        stats.put("activePanelists", panelists.stream().filter(Panelist::isActive).count());
        stats.put("totalCandidates", candidates.size());
        stats.put("assignedCandidates", candidates.stream().filter(c -> c.getAssignedPanelist() != null).count());
        stats.put("unassignedCandidates", candidates.stream().filter(c -> c.getAssignedPanelist() == null).count());

        return stats;
    }

    /**
     * Get all panelists with detailed information including login status
     * Returns ALL panelists in the system
     */
    public List<Map<String, Object>> getAllPanelistsWithDetails(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        // Get ALL panelists in the system
        List<Panelist> panelists = panelistRepository.findAll();
        
        List<Map<String, Object>> panelistDetails = new ArrayList<>();
        for (Panelist panelist : panelists) {
            Map<String, Object> details = new HashMap<>();
            User panelistUser = panelist.getUser();
            
            details.put("id", panelist.getId());
            details.put("userId", panelistUser.getId());
            details.put("username", panelistUser.getUsername());
            details.put("email", panelistUser.getEmail());
            details.put("specialization", panelist.getSpecialization());
            details.put("experience", panelist.getExperienceYears());
            details.put("active", panelist.isActive());
            details.put("createdAt", panelist.getCreatedAt());
            
            // Count assigned candidates
            long assignedCandidates = candidateRepository.findByAssignedPanelist(panelistUser).size();
            details.put("assignedCandidates", assignedCandidates);
            
            // Count interviews
            List<Interview> interviews = interviewRepository.findByPanelistId(panelistUser.getId());
            details.put("totalInterviews", interviews.size());
            details.put("completedInterviews", interviews.stream()
                .filter(i -> "COMPLETED".equals(i.getStatus().toString()))
                .count());
            
            panelistDetails.add(details);
        }
        
        return panelistDetails;
    }

    /**
     * Get all candidates with detailed information including login status
     * Returns ALL candidates in the system, not just those assigned to this HR
     */
    public List<Map<String, Object>> getAllCandidatesWithDetails(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        // Get ALL candidates in the system
        List<Candidate> candidates = candidateRepository.findAll();
        
        List<Map<String, Object>> candidateDetails = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Map<String, Object> details = new HashMap<>();
            
            details.put("id", candidate.getId());
            details.put("name", candidate.getName());
            details.put("email", candidate.getEmail());
            details.put("phone", candidate.getPhone());
            details.put("position", candidate.getPosition());
            details.put("status", candidate.getStatus());
            details.put("experienceYears", candidate.getExperienceYears());
            details.put("skills", candidate.getSkills());
            details.put("jdDetails", candidate.getJdDetails());
            details.put("joiningDate", candidate.getJoiningDate());
            details.put("oldCtc", candidate.getOldCtc());
            details.put("newCtc", candidate.getNewCtc());
            details.put("employmentType", candidate.getEmploymentType());
            details.put("location", candidate.getLocation());
            details.put("createdAt", candidate.getCreatedAt());
            
            // Assigned panelist info
            if (candidate.getAssignedPanelist() != null) {
                Map<String, Object> panelistInfo = new HashMap<>();
                panelistInfo.put("id", candidate.getAssignedPanelist().getId());
                panelistInfo.put("username", candidate.getAssignedPanelist().getUsername());
                panelistInfo.put("email", candidate.getAssignedPanelist().getEmail());
                details.put("assignedPanelist", panelistInfo);
            } else {
                details.put("assignedPanelist", null);
            }
            
            // Interview info
            List<Interview> interviews = interviewRepository.findByCandidateEmail(candidate.getEmail());
            if (!interviews.isEmpty()) {
                Interview latestInterview = interviews.get(interviews.size() - 1);
                Map<String, Object> interviewInfo = new HashMap<>();
                interviewInfo.put("date", latestInterview.getInterviewDate());
                interviewInfo.put("time", latestInterview.getInterviewTimeFrom());
                interviewInfo.put("status", latestInterview.getStatus());
                details.put("interview", interviewInfo);
            } else {
                details.put("interview", null);
            }
            
            candidateDetails.add(details);
        }
        
        return candidateDetails;
    }

    /**
     * Get real-time dashboard updates for polling
     * Returns counts for ALL panelists and candidates in the system
     */
    public Map<String, Object> getDashboardUpdates(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        Map<String, Object> updates = new HashMap<>();
        
        // Get counts for ALL panelists and candidates
        List<Panelist> panelists = panelistRepository.findAll();
        List<Candidate> candidates = candidateRepository.findAll();
        
        updates.put("totalPanelists", panelists.size());
        updates.put("totalCandidates", candidates.size());
        updates.put("activePanelists", panelists.stream().filter(Panelist::isActive).count());
        
        // Get recently added (last 24 hours)
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        
        long newPanelists = panelists.stream()
            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(yesterday))
            .count();
        long newCandidates = candidates.stream()
            .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(yesterday))
            .count();
            
        updates.put("newPanelists", newPanelists);
        updates.put("newCandidates", newCandidates);
        updates.put("timestamp", new Date());
        
        return updates;
    }

    /**
    }

    /**
     * Create a new candidate with login credentials
     * This method creates both a User account and a Candidate profile
     */
    @Transactional
    public Map<String, Object> createCandidateWithLogin(Long hrId, Map<String, Object> request) {
        // Verify HR exists and has HR role
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();

        // Extract and validate required fields
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        String phone = (String) request.get("phone");
        String position = (String) request.get("position");
        String username = (String) request.get("username");
        String password = (String) request.get("password");

        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("Phone is required");
        }
        if (position == null || position.trim().isEmpty()) {
            throw new RuntimeException("Position is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }

        // Check if username already exists
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Check if candidate email already exists
        if (candidateRepository.existsByEmail(email)) {
            throw new RuntimeException("Candidate with this email already exists");
        }

        // Create User account
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CANDIDATE");
        user.setActive(true);
        user.setMfaEnabled(false);

        User savedUser = userRepository.saveAndFlush(user);

        // Add password to history
        try {
            passwordHistoryService.addPasswordToHistory(savedUser, savedUser.getPassword());
        } catch (Exception e) {
            System.err.println("Failed to add password to history: " + e.getMessage());
        }

        // Create Candidate profile
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPhone(phone);
        candidate.setPosition(position);
        candidate.setStatus("APPLIED");
        candidate.setHr(hr);

        // Set optional fields
        if (request.get("experienceYears") != null) {
            candidate.setExperienceYears(Integer.parseInt(request.get("experienceYears").toString()));
        }
        if (request.get("skills") != null) {
            candidate.setSkills((String) request.get("skills"));
        }
        if (request.get("currentCtc") != null) {
            candidate.setCurrentCtc(new BigDecimal(request.get("currentCtc").toString()));
        }
        if (request.get("hrMailId") != null) {
            candidate.setHrMailId((String) request.get("hrMailId"));
        }
        if (request.get("jdDetails") != null) {
            candidate.setJdDetails((String) request.get("jdDetails"));
        }
        if (request.get("employmentType") != null) {
            candidate.setEmploymentType((String) request.get("employmentType"));
        }
        if (request.get("location") != null) {
            candidate.setLocation((String) request.get("location"));
        }

        Candidate savedCandidate = candidateRepository.saveAndFlush(candidate);

        // Prepare response
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", savedUser.getId());
        userInfo.put("username", savedUser.getUsername());
        userInfo.put("email", savedUser.getEmail());
        userInfo.put("role", savedUser.getRole());
        
        Map<String, Object> candidateInfo = new HashMap<>();
        candidateInfo.put("id", savedCandidate.getId());
        candidateInfo.put("name", savedCandidate.getName());
        candidateInfo.put("email", savedCandidate.getEmail());
        candidateInfo.put("phone", savedCandidate.getPhone());
        candidateInfo.put("position", savedCandidate.getPosition());
        candidateInfo.put("status", savedCandidate.getStatus());
        
        result.put("user", userInfo);
        result.put("candidate", candidateInfo);
        
        // Generate and send OTP to candidate's email for login
        try {
            String otp = otpService.generateAndSendLoginOtp(email, username);
            System.out.println("Login OTP sent to candidate: " + email + " | OTP: " + otp);
            result.put("otpSent", true);
            result.put("message", "Candidate created successfully. Login credentials sent via email.");
        } catch (Exception e) {
            System.err.println("Failed to send OTP: " + e.getMessage());
            result.put("otpSent", false);
            result.put("message", "Candidate created but failed to send login OTP. Please try manual login.");
        }
        
        return result;
    }

    /**
     * Get all candidates created by this HR with full details
     */
    public List<Map<String, Object>> getHRCreatedCandidates(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();
        
        // Get candidates created by this HR
        List<Candidate> candidates = candidateRepository.findByHr(hr);
        
        List<Map<String, Object>> candidateList = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Map<String, Object> details = new HashMap<>();
            
            details.put("id", candidate.getId());
            details.put("name", candidate.getName());
            details.put("email", candidate.getEmail());
            details.put("phone", candidate.getPhone());
            details.put("position", candidate.getPosition());
            details.put("status", candidate.getStatus());
            details.put("experienceYears", candidate.getExperienceYears());
            details.put("skills", candidate.getSkills());
            details.put("currentCtc", candidate.getCurrentCtc());
            details.put("jdDetails", candidate.getJdDetails());
            details.put("employmentType", candidate.getEmploymentType());
            details.put("location", candidate.getLocation());
            details.put("hrMailId", candidate.getHrMailId());
            details.put("joiningDate", candidate.getJoiningDate());
            details.put("oldCtc", candidate.getOldCtc());
            details.put("newCtc", candidate.getNewCtc());
            details.put("isLoggedIn", candidate.getIsLoggedIn());
            details.put("lastLoginAt", candidate.getLastLoginAt());
            details.put("createdAt", candidate.getCreatedAt());
            
            // Assigned panelist info
            if (candidate.getAssignedPanelist() != null) {
                Map<String, Object> panelistInfo = new HashMap<>();
                panelistInfo.put("id", candidate.getAssignedPanelist().getId());
                panelistInfo.put("username", candidate.getAssignedPanelist().getUsername());
                panelistInfo.put("email", candidate.getAssignedPanelist().getEmail());
                details.put("assignedPanelist", panelistInfo);
            } else {
                details.put("assignedPanelist", null);
            }
            
            candidateList.add(details);
        }
        
        return candidateList;
    }

    /**
     * Update candidate details
     */
    @Transactional
    public Map<String, Object> updateCandidate(Long hrId, Long candidateId, Map<String, Object> updates) {
        // Verify HR exists and has HR role
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();

        // Get candidate
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOpt.get();

        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to update this candidate");
        }

        // Update fields
        if (updates.containsKey("name")) {
            candidate.setName((String) updates.get("name"));
        }
        if (updates.containsKey("phone")) {
            candidate.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("position")) {
            candidate.setPosition((String) updates.get("position"));
        }
        if (updates.containsKey("status")) {
            candidate.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("experienceYears")) {
            candidate.setExperienceYears(Integer.parseInt(updates.get("experienceYears").toString()));
        }
        if (updates.containsKey("skills")) {
            candidate.setSkills((String) updates.get("skills"));
        }
        if (updates.containsKey("currentCtc")) {
            candidate.setCurrentCtc(new BigDecimal(updates.get("currentCtc").toString()));
        }
        if (updates.containsKey("jdDetails")) {
            candidate.setJdDetails((String) updates.get("jdDetails"));
        }
        if (updates.containsKey("employmentType")) {
            candidate.setEmploymentType((String) updates.get("employmentType"));
        }
        if (updates.containsKey("location")) {
            candidate.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("hrMailId")) {
            candidate.setHrMailId((String) updates.get("hrMailId"));
        }

        Candidate savedCandidate = candidateRepository.save(candidate);

        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedCandidate.getId());
        result.put("name", savedCandidate.getName());
        result.put("email", savedCandidate.getEmail());
        result.put("phone", savedCandidate.getPhone());
        result.put("position", savedCandidate.getPosition());
        result.put("status", savedCandidate.getStatus());
        result.put("message", "Candidate updated successfully");

        return result;
    }

    /**
     * Delete candidate
     * Handles cascading deletion of related records including password history
     */
    @Transactional
    public void deleteCandidate(Long hrId, Long candidateId) {
        // Verify HR exists and has HR role
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();

        // Get candidate
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOpt.get();

        // Verify HR owns this candidate
        if (!candidate.getHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to delete this candidate");
        }

        // Get the candidate's email to find the associated user account
        String candidateEmail = candidate.getEmail();
        
        // Find the associated user account first
        Optional<User> userOpt = userRepository.findByEmail(candidateEmail);
        
        // Delete the candidate record first
        candidateRepository.delete(candidate);
        candidateRepository.flush();
        System.out.println("Deleted candidate record: " + candidateEmail);
        
        // Now handle user account deletion with password history cleanup
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Only delete if it's a CANDIDATE role to avoid accidentally deleting other users
            if ("CANDIDATE".equals(user.getRole())) {
                // Delete password history records first to avoid foreign key constraint violation
                List<PasswordHistory> passwordHistories = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user);
                if (!passwordHistories.isEmpty()) {
                    passwordHistoryRepository.deleteAll(passwordHistories);
                    passwordHistoryRepository.flush();
                    System.out.println("Deleted " + passwordHistories.size() + " password history records for user: " + candidateEmail);
                }
                
                // Now safe to delete the user
                userRepository.delete(user);
                userRepository.flush();
                System.out.println("Deleted user account for candidate: " + candidateEmail);
            }
        }
        
        System.out.println("Candidate deleted successfully: " + candidateEmail);
    }

    /**
     * Get HR profile by user ID
     */
    public HRProfileDTO getHRProfile(Long hrId) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();
        Optional<HRProfile> profileOpt = hrProfileRepository.findByUser(hr);

        if (profileOpt.isEmpty()) {
            // Return empty profile if not exists
            HRProfileDTO dto = new HRProfileDTO();
            dto.setUserId(hr.getId());
            dto.setUsername(hr.getUsername());
            dto.setEmail(hr.getEmail());
            dto.setActive(true);
            return dto;
        }

        return convertToDTO(profileOpt.get());
    }

    /**
     * Create or update HR profile
     */
    @Transactional
    public HRProfileDTO createOrUpdateHRProfile(Long hrId, Map<String, Object> profileData) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        User hr = hrOpt.get();
        HRProfile profile = hrProfileRepository.findByUser(hr)
                .orElse(new HRProfile(hr, (String) profileData.get("fullName")));

        // Update fields
        if (profileData.containsKey("fullName")) {
            profile.setFullName((String) profileData.get("fullName"));
        }
        if (profileData.containsKey("phone")) {
            profile.setPhone((String) profileData.get("phone"));
        }
        if (profileData.containsKey("location")) {
            profile.setLocation((String) profileData.get("location"));
        }
        if (profileData.containsKey("address")) {
            profile.setAddress((String) profileData.get("address"));
        }
        if (profileData.containsKey("designation")) {
            profile.setDesignation((String) profileData.get("designation"));
        }
        if (profileData.containsKey("department")) {
            profile.setDepartment((String) profileData.get("department"));
        }
        if (profileData.containsKey("employeeId")) {
            profile.setEmployeeId((String) profileData.get("employeeId"));
        }
        if (profileData.containsKey("experienceYears")) {
            profile.setExperienceYears(Integer.parseInt(profileData.get("experienceYears").toString()));
        }
        if (profileData.containsKey("company")) {
            profile.setCompany((String) profileData.get("company"));
        }
        if (profileData.containsKey("bio")) {
            profile.setBio((String) profileData.get("bio"));
        }
        if (profileData.containsKey("linkedinUrl")) {
            profile.setLinkedinUrl((String) profileData.get("linkedinUrl"));
        }
        if (profileData.containsKey("slackHandle")) {
            profile.setSlackHandle((String) profileData.get("slackHandle"));
        }
        if (profileData.containsKey("emergencyContact")) {
            profile.setEmergencyContact((String) profileData.get("emergencyContact"));
        }
        if (profileData.containsKey("emergencyPhone")) {
            profile.setEmergencyPhone((String) profileData.get("emergencyPhone"));
        }
        if (profileData.containsKey("skills")) {
            profile.setSkills((String) profileData.get("skills"));
        }
        if (profileData.containsKey("certifications")) {
            profile.setCertifications((String) profileData.get("certifications"));
        }
        if (profileData.containsKey("education")) {
            profile.setEducation((String) profileData.get("education"));
        }
        if (profileData.containsKey("workType")) {
            profile.setWorkType((String) profileData.get("workType"));
        }
        if (profileData.containsKey("teamName")) {
            profile.setTeamName((String) profileData.get("teamName"));
        }
        if (profileData.containsKey("reportingManager")) {
            profile.setReportingManager((String) profileData.get("reportingManager"));
        }
        if (profileData.containsKey("hrSpecialization")) {
            profile.setHrSpecialization((String) profileData.get("hrSpecialization"));
        }
        if (profileData.containsKey("region")) {
            profile.setRegion((String) profileData.get("region"));
        }

        HRProfile savedProfile = hrProfileRepository.save(profile);
        return convertToDTO(savedProfile);
    }

    /**
     * Convert HRProfile entity to DTO
     */
    private HRProfileDTO convertToDTO(HRProfile profile) {
        HRProfileDTO dto = new HRProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setUsername(profile.getUser().getUsername());
        dto.setEmail(profile.getUser().getEmail());
        dto.setFullName(profile.getFullName());
        dto.setPhone(profile.getPhone());
        dto.setLocation(profile.getLocation());
        dto.setAddress(profile.getAddress());
        dto.setDesignation(profile.getDesignation());
        dto.setDepartment(profile.getDepartment());
        dto.setEmployeeId(profile.getEmployeeId());
        dto.setExperienceYears(profile.getExperienceYears());
        dto.setCompany(profile.getCompany());
        dto.setBio(profile.getBio());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setSlackHandle(profile.getSlackHandle());
        dto.setEmergencyContact(profile.getEmergencyContact());
        dto.setEmergencyPhone(profile.getEmergencyPhone());
        dto.setSkills(profile.getSkills());
        dto.setCertifications(profile.getCertifications());
        dto.setEducation(profile.getEducation());
        dto.setWorkType(profile.getWorkType());
        dto.setTeamName(profile.getTeamName());
        dto.setReportingManager(profile.getReportingManager());
        dto.setHrSpecialization(profile.getHrSpecialization());
        dto.setRegion(profile.getRegion());
        dto.setTotalCandidatesManaged(profile.getTotalCandidatesManaged());
        dto.setTotalPanelistsManaged(profile.getTotalPanelistsManaged());
        dto.setActive(profile.isActive());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }

    /**
     * Search panelist by email
     */
    public Map<String, Object> searchPanelistByEmail(Long hrId, String email) {
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        // Search for user with PANELIST role and matching email
        Optional<User> panelistUserOpt = userRepository.findByEmail(email);
        
        if (panelistUserOpt.isEmpty() || !"PANELIST".equals(panelistUserOpt.get().getRole())) {
            return null; // Panelist not found
        }

        User panelistUser = panelistUserOpt.get();
        
        // Get panelist profile
        Optional<Panelist> panelistOpt = panelistRepository.findByUser(panelistUser);
        
        if (panelistOpt.isEmpty()) {
            return null; // Panelist profile not found
        }

        Panelist panelist = panelistOpt.get();
        
        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("id", panelist.getId());
        result.put("userId", panelistUser.getId());
        result.put("username", panelistUser.getUsername());
        result.put("email", panelistUser.getEmail());
        result.put("specialization", panelist.getSpecialization());
        result.put("experienceYears", panelist.getExperienceYears());
        result.put("phone", panelist.getPhone());
        result.put("designation", panelist.getDesignation());
        result.put("company", panelist.getCompany());
        result.put("active", panelist.isActive());
        
        return result;
    }

    /**
     * Schedule interview for a candidate
     * Creates an interview record and sends email notifications to both candidate and panelist
     */
    @Transactional(timeout = 60)
    public Map<String, Object> scheduleInterview(Long hrId, Map<String, Object> request) {
        // Verify HR exists and has HR role
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }

        // Extract and validate required fields
        Long candidateId = Long.valueOf(request.get("candidateId").toString());
        String panelistEmail = (String) request.get("panelistEmail");
        String interviewDate = (String) request.get("interviewDate");
        String interviewTimeFrom = (String) request.get("interviewTimeFrom");
        String interviewTimeTo = (String) request.get("interviewTimeTo");

        if (candidateId == null) {
            throw new RuntimeException("Candidate ID is required");
        }
        if (panelistEmail == null || panelistEmail.trim().isEmpty()) {
            throw new RuntimeException("Panelist email is required");
        }
        if (interviewDate == null || interviewDate.trim().isEmpty()) {
            throw new RuntimeException("Interview date is required");
        }
        if (interviewTimeFrom == null || interviewTimeFrom.trim().isEmpty()) {
            throw new RuntimeException("Interview start time is required");
        }
        if (interviewTimeTo == null || interviewTimeTo.trim().isEmpty()) {
            throw new RuntimeException("Interview end time is required");
        }

        // Get candidate
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }
        Candidate candidate = candidateOpt.get();

        // Get panelist by email
        Optional<User> panelistUserOpt = userRepository.findByEmail(panelistEmail);
        if (panelistUserOpt.isEmpty() || !"PANELIST".equals(panelistUserOpt.get().getRole())) {
            throw new RuntimeException("Panelist not found with email: " + panelistEmail);
        }
        User panelistUser = panelistUserOpt.get();

        // Get panelist profile
        Optional<Panelist> panelistOpt = panelistRepository.findByUser(panelistUser);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist profile not found");
        }
        Panelist panelist = panelistOpt.get();

        // Create interview record
        Interview interview = new Interview();
        interview.setHrId(hrId);
        interview.setPanelistId(panelistUser.getId());
        interview.setCandidateId(candidateId);
        interview.setCandidateName(candidate.getName());
        interview.setCandidateEmail(candidate.getEmail());
        interview.setInterviewDate(java.time.LocalDate.parse(interviewDate));
        interview.setInterviewTimeFrom(java.time.LocalTime.parse(interviewTimeFrom));
        interview.setInterviewTimeTo(java.time.LocalTime.parse(interviewTimeTo));
        interview.setPosition(candidate.getPosition());
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);
        
        if (request.containsKey("notes")) {
            interview.setNotes((String) request.get("notes"));
        }

        Interview savedInterview = interviewRepository.save(interview);

        // Assign panelist to candidate if not already assigned
        if (candidate.getAssignedPanelist() == null ||
            !candidate.getAssignedPanelist().getId().equals(panelistUser.getId())) {
            candidate.setAssignedPanelist(panelistUser);
            candidate.setStatus("INTERVIEW");
            candidateRepository.save(candidate);
        }

        // Send email notifications
        try {
            // Format time range for emails
            String timeRange = interviewTimeFrom + " - " + interviewTimeTo;
            
            // Send to candidate
            emailService.sendInterviewScheduleToCandidate(
                candidate.getEmail(),
                candidate.getName(),
                interviewDate,
                timeRange,
                candidate.getPosition(),
                panelistUser.getUsername()
            );

            // Send to panelist
            emailService.sendInterviewScheduleToPanelist(
                panelistUser.getEmail(),
                panelistUser.getUsername(),
                candidate.getName(),
                candidate.getEmail(),
                interviewDate,
                timeRange,
                candidate.getPosition()
            );

            System.out.println("Interview notifications sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send interview notifications: " + e.getMessage());
            // Don't throw exception - interview is already created
        }

        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedInterview.getId());
        result.put("candidateName", savedInterview.getCandidateName());
        result.put("candidateEmail", savedInterview.getCandidateEmail());
        result.put("panelistName", panelistUser.getUsername());
        result.put("panelistEmail", panelistUser.getEmail());
        result.put("interviewDate", savedInterview.getInterviewDate());
        result.put("interviewTimeFrom", savedInterview.getInterviewTimeFrom());
        result.put("interviewTimeTo", savedInterview.getInterviewTimeTo());
        result.put("position", savedInterview.getPosition());
        result.put("status", savedInterview.getStatus());
        result.put("notes", savedInterview.getNotes());

        return result;
    }
}

// Made with Bob