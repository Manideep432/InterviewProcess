package com.login.service;

import com.login.dto.PanelistProfileDTO;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Panelist Service - Business logic for panelist management
 * 
 * @author Bob
 */
@Service
@Transactional
public class PanelistService {

    @Autowired
    private PanelistRepository panelistRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new panelist (assigned by HR)
     */
    public Panelist createPanelist(Long userId, Long hrId, String specialization, Integer experienceYears, String expertise) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !"PANELIST".equals(userOpt.get().getRole())) {
            throw new RuntimeException("Invalid user or user is not a PANELIST");
        }
        
        Optional<User> hrOpt = userRepository.findById(hrId);
        if (hrOpt.isEmpty() || !"HR".equals(hrOpt.get().getRole())) {
            throw new RuntimeException("Invalid HR ID");
        }
        
        if (panelistRepository.existsByUser(userOpt.get())) {
            throw new RuntimeException("This user is already registered as a panelist");
        }
        
        Panelist panelist = new Panelist();
        panelist.setUser(userOpt.get());
        panelist.setAssignedHr(hrOpt.get());
        panelist.setSpecialization(specialization);
        panelist.setExperienceYears(experienceYears);
        panelist.setExpertise(expertise);
        panelist.setActive(true);
        
        return panelistRepository.save(panelist);
    }

    /**
     * Get all panelists
     */
    public List<Panelist> getAllPanelists() {
        return panelistRepository.findAll();
    }

    /**
     * Get panelist by ID
     */
    public Optional<Panelist> getPanelistById(Long id) {
        return panelistRepository.findById(id);
    }

    /**
     * Get panelist by user ID
     */
    public Optional<Panelist> getPanelistByUserId(Long userId) {
        return panelistRepository.findByUserId(userId);
    }

    /**
     * Get panelists assigned by a specific HR
     */
    public List<Panelist> getPanelistsByHr(Long hrId) {
        Optional<User> hr = userRepository.findById(hrId);
        if (hr.isEmpty()) {
            throw new RuntimeException("HR not found");
        }
        return panelistRepository.findByAssignedHr(hr.get());
    }

    /**
     * Get active panelists assigned by a specific HR
     */
    public List<Panelist> getActivePanelistsByHr(Long hrId) {
        Optional<User> hr = userRepository.findById(hrId);
        if (hr.isEmpty()) {
            throw new RuntimeException("HR not found");
        }
        return panelistRepository.findByAssignedHrAndIsActive(hr.get(), true);
    }

    /**
     * Get all active panelists
     */
    public List<Panelist> getActivePanelists() {
        return panelistRepository.findByIsActive(true);
    }

    /**
     * Update panelist details
     */
    public Panelist updatePanelist(Long panelistId, String specialization, Integer experienceYears, String expertise, Long hrId) {
        Optional<Panelist> panelistOpt = panelistRepository.findById(panelistId);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        
        Panelist panelist = panelistOpt.get();
        
        // Verify HR assigned this panelist
        if (!panelist.getAssignedHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to update this panelist");
        }
        
        panelist.setSpecialization(specialization);
        panelist.setExperienceYears(experienceYears);
        panelist.setExpertise(expertise);
        
        return panelistRepository.save(panelist);
    }

    /**
     * Activate/Deactivate panelist
     */
    public Panelist togglePanelistStatus(Long panelistId, Long hrId) {
        Optional<Panelist> panelistOpt = panelistRepository.findById(panelistId);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        
        Panelist panelist = panelistOpt.get();
        
        // Verify HR assigned this panelist
        if (!panelist.getAssignedHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to modify this panelist");
        }
        
        panelist.setActive(!panelist.isActive());
        return panelistRepository.save(panelist);
    }

    /**
     * Delete panelist
     */
    public void deletePanelist(Long panelistId, Long hrId) {
        Optional<Panelist> panelistOpt = panelistRepository.findById(panelistId);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        
        Panelist panelist = panelistOpt.get();
        
        // Verify HR assigned this panelist
        if (!panelist.getAssignedHr().getId().equals(hrId)) {
            throw new RuntimeException("You don't have permission to delete this panelist");
        }
        
        panelistRepository.delete(panelist);
    }

    /**
     * Get HR name for a panelist
     */
    public String getAssignedHrName(Long panelistUserId) {
        Optional<Panelist> panelistOpt = panelistRepository.findByUserId(panelistUserId);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        
        return panelistOpt.get().getAssignedHr().getUsername();
    }
    
    /**
     * Get panelist profile by user ID
     */
    public PanelistProfileDTO getPanelistProfile(Long userId) {
        Optional<Panelist> panelistOpt = panelistRepository.findByUserId(userId);
        if (panelistOpt.isEmpty()) {
            throw new RuntimeException("Panelist not found");
        }
        
        Panelist panelist = panelistOpt.get();
        return convertToProfileDTO(panelist);
    }
    
    /**
     * Update panelist profile (creates if doesn't exist)
     */
    public PanelistProfileDTO updatePanelistProfile(Long userId, PanelistProfileDTO profileDTO) {
        Optional<Panelist> panelistOpt = panelistRepository.findByUserId(userId);
        
        Panelist panelist;
        if (panelistOpt.isEmpty()) {
            // Create new panelist profile if it doesn't exist
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty() || !"PANELIST".equals(userOpt.get().getRole())) {
                throw new RuntimeException("Invalid user or user is not a PANELIST");
            }
            
            panelist = new Panelist();
            panelist.setUser(userOpt.get());
            panelist.setActive(true);
            
            // Set specialization as required field
            if (profileDTO.getSpecialization() == null || profileDTO.getSpecialization().trim().isEmpty()) {
                throw new RuntimeException("Specialization is required");
            }
        } else {
            panelist = panelistOpt.get();
        }
        
        // Update profile fields
        if (profileDTO.getSpecialization() != null) {
            panelist.setSpecialization(profileDTO.getSpecialization());
        }
        if (profileDTO.getExperienceYears() != null) {
            panelist.setExperienceYears(profileDTO.getExperienceYears());
        }
        if (profileDTO.getExpertise() != null) {
            panelist.setExpertise(profileDTO.getExpertise());
        }
        
        // Contact Information
        panelist.setPhone(profileDTO.getPhone());
        panelist.setLocation(profileDTO.getLocation());
        panelist.setLinkedinUrl(profileDTO.getLinkedinUrl());
        panelist.setSlackHandle(profileDTO.getSlackHandle());
        
        // Professional Details
        panelist.setDesignation(profileDTO.getDesignation());
        panelist.setCompany(profileDTO.getCompany());
        panelist.setBio(profileDTO.getBio());
        
        // Skills and Certifications
        panelist.setSkills(profileDTO.getSkills());
        panelist.setCertifications(profileDTO.getCertifications());
        panelist.setEducation(profileDTO.getEducation());
        
        // Business Information
        panelist.setDepartment(profileDTO.getDepartment());
        panelist.setEmployeeId(profileDTO.getEmployeeId());
        panelist.setWorkType(profileDTO.getWorkType());
        
        // Team Details
        panelist.setTeamName(profileDTO.getTeamName());
        panelist.setReportingManager(profileDTO.getReportingManager());
        
        Panelist updated = panelistRepository.save(panelist);
        return convertToProfileDTO(updated);
    }
    
    /**
     * Convert Panelist entity to PanelistProfileDTO
     */
    private PanelistProfileDTO convertToProfileDTO(Panelist panelist) {
        PanelistProfileDTO dto = new PanelistProfileDTO();
        
        // Basic Info
        dto.setId(panelist.getId());
        dto.setUsername(panelist.getUser().getUsername());
        dto.setEmail(panelist.getUser().getEmail());
        dto.setSpecialization(panelist.getSpecialization());
        dto.setExperienceYears(panelist.getExperienceYears());
        dto.setExpertise(panelist.getExpertise());
        
        // Contact Information
        dto.setPhone(panelist.getPhone());
        dto.setLocation(panelist.getLocation());
        dto.setLinkedinUrl(panelist.getLinkedinUrl());
        dto.setSlackHandle(panelist.getSlackHandle());
        
        // Professional Details
        dto.setDesignation(panelist.getDesignation());
        dto.setCompany(panelist.getCompany());
        dto.setBio(panelist.getBio());
        
        // Skills and Certifications
        dto.setSkills(panelist.getSkills());
        dto.setCertifications(panelist.getCertifications());
        dto.setEducation(panelist.getEducation());
        
        // Business Information
        dto.setDepartment(panelist.getDepartment());
        dto.setEmployeeId(panelist.getEmployeeId());
        dto.setWorkType(panelist.getWorkType());
        
        // Team Details
        dto.setTeamName(panelist.getTeamName());
        dto.setReportingManager(panelist.getReportingManager());
        
        // Status
        dto.setActive(panelist.isActive());
        dto.setAssignedHrName(panelist.getAssignedHr().getUsername());
        
        return dto;
    }
}

// Made with Bob