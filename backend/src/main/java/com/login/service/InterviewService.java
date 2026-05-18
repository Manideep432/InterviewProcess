package com.login.service;

import com.login.dto.CandidateInterviewDTO;
import com.login.model.Candidate;
import com.login.model.HRProfile;
import com.login.model.Interview;
import com.login.model.Interview.InterviewStatus;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.repository.CandidateRepository;
import com.login.repository.HRProfileRepository;
import com.login.repository.InterviewRepository;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interview Service - Business logic for interview management
 * 
 * @author Bob
 */
@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HRProfileRepository hrProfileRepository;
    
    @Autowired
    private PanelistRepository panelistRepository;

    /**
     * Schedule a new interview
     */
    @Transactional(timeout = 30)
    public Interview scheduleInterview(Long hrId, Long panelistId, Long candidateId,
                                      String candidateName, String candidateEmail,
                                      LocalDate interviewDate, LocalTime interviewTimeFrom, LocalTime interviewTimeTo,
                                      String position, String notes) {
        Interview interview = new Interview(
            hrId,
            panelistId,
            candidateId,
            candidateName,
            candidateEmail,
            interviewDate,
            interviewTimeFrom,
            interviewTimeTo,
            position,
            notes
        );
        return interviewRepository.save(interview);
    }
    
    /**
     * Schedule a new interview (backward compatibility)
     */
    @Transactional(timeout = 30)
    public Interview scheduleInterview(Long panelistId, String candidateName, String candidateEmail,
                                      LocalDate interviewDate, LocalTime interviewTimeFrom, LocalTime interviewTimeTo,
                                      String position, String notes) {
        // Use default values for hrId and candidateId for backward compatibility
        Interview interview = new Interview(
            1L, // Default HR ID
            panelistId,
            0L, // Default candidate ID
            candidateName,
            candidateEmail,
            interviewDate,
            interviewTimeFrom,
            interviewTimeTo,
            position,
            notes
        );
        return interviewRepository.save(interview);
    }

    /**
     * Get all interviews for a panelist
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByPanelist(Long panelistId) {
        return interviewRepository.findByPanelistId(panelistId);
    }

    /**
     * Get interviews by panelist and status
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByPanelistAndStatus(Long panelistId, InterviewStatus status) {
        return interviewRepository.findByPanelistIdAndStatus(panelistId, status);
    }

    /**
     * Get interview by ID
     */
    @Transactional(readOnly = true)
    public Optional<Interview> getInterviewById(Long id) {
        return interviewRepository.findById(id);
    }

    /**
     * Update interview status
     */
    @Transactional(timeout = 30)
    public Interview updateInterviewStatus(Long interviewId, InterviewStatus status) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with id: " + interviewId));
        interview.setStatus(status);
        return interviewRepository.save(interview);
    }

    /**
     * Update interview details
     */
    @Transactional(timeout = 30)
    public Interview updateInterview(Long interviewId, String candidateName, String candidateEmail,
                                    LocalDate interviewDate, LocalTime interviewTimeFrom, LocalTime interviewTimeTo,
                                    String position, String notes) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with id: " + interviewId));
        
        if (candidateName != null) interview.setCandidateName(candidateName);
        if (candidateEmail != null) interview.setCandidateEmail(candidateEmail);
        if (interviewDate != null) interview.setInterviewDate(interviewDate);
        if (interviewTimeFrom != null) interview.setInterviewTimeFrom(interviewTimeFrom);
        if (interviewTimeTo != null) interview.setInterviewTimeTo(interviewTimeTo);
        if (position != null) interview.setPosition(position);
        if (notes != null) interview.setNotes(notes);
        
        return interviewRepository.save(interview);
    }

    /**
     * Delete interview
     */
    @Transactional(timeout = 30)
    public void deleteInterview(Long interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new RuntimeException("Interview not found with id: " + interviewId);
        }
        interviewRepository.deleteById(interviewId);
    }

    /**
     * Get interview count by panelist
     */
    @Transactional(readOnly = true)
    public long getInterviewCountByPanelist(Long panelistId) {
        return interviewRepository.countByPanelistId(panelistId);
    }

    /**
     * Get interview count by panelist and status
     */
    @Transactional(readOnly = true)
    public long getInterviewCountByPanelistAndStatus(Long panelistId, InterviewStatus status) {
        return interviewRepository.countByPanelistIdAndStatus(panelistId, status);
    }

    /**
     * Get all interviews
     */
    @Transactional(readOnly = true)
    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll();
    }

    /**
     * Get interviews by candidate email
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByCandidateEmail(String candidateEmail) {
        return interviewRepository.findByCandidateEmail(candidateEmail);
    }

    /**
     * Get interviews by date for a panelist
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByPanelistAndDate(Long panelistId, LocalDate date) {
        return interviewRepository.findByPanelistIdAndInterviewDate(panelistId, date);
    }

    /**
     * Cancel interview
     */
    @Transactional(timeout = 30)
    public Interview cancelInterview(Long interviewId) {
        return updateInterviewStatus(interviewId, InterviewStatus.CANCELLED);
    }

    /**
     * Complete interview
     */
    @Transactional(timeout = 30)
    public Interview completeInterview(Long interviewId) {
        return updateInterviewStatus(interviewId, InterviewStatus.COMPLETED);
    }

    /**
     * Reschedule interview
     */
    @Transactional(timeout = 30)
    public Interview rescheduleInterview(Long interviewId, LocalDate newDate, LocalTime newTimeFrom, LocalTime newTimeTo) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with id: " + interviewId));
        
        interview.setInterviewDate(newDate);
        interview.setInterviewTimeFrom(newTimeFrom);
        interview.setInterviewTimeTo(newTimeTo);
        interview.setStatus(InterviewStatus.RESCHEDULED);
        
        return interviewRepository.save(interview);
    }

    /**
     * Submit feedback for an interview
     */
    @Transactional(timeout = 30)
    public Interview submitFeedback(Long interviewId, String feedback, String decision) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with id: " + interviewId));

        interview.setFeedback(feedback);
        interview.setStatus(InterviewStatus.COMPLETED);

        if (decision != null && !decision.trim().isEmpty()) {
            Candidate candidate = candidateRepository.findById(interview.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + interview.getCandidateId()));

            String normalizedDecision = decision.trim().toUpperCase();
            if ("SELECTED".equals(normalizedDecision) || "REJECTED".equals(normalizedDecision)) {
                candidate.setStatus(normalizedDecision);
                candidateRepository.save(candidate);
            }
        }

        return interviewRepository.save(interview);
    }
    
    /**
     * Get candidate interviews with HR and panelist details
     */
    @Transactional(readOnly = true)
    public List<CandidateInterviewDTO> getCandidateInterviewsWithDetails(Long candidateId) {
        List<Interview> interviews = interviewRepository.findByCandidateId(candidateId);
        List<CandidateInterviewDTO> interviewDTOs = new ArrayList<>();
        
        for (Interview interview : interviews) {
            CandidateInterviewDTO dto = new CandidateInterviewDTO();
            
            // Set interview details
            dto.setInterviewId(interview.getId());
            dto.setPosition(interview.getPosition());
            dto.setInterviewDate(interview.getInterviewDate());
            dto.setInterviewTimeFrom(interview.getInterviewTimeFrom());
            dto.setInterviewTimeTo(interview.getInterviewTimeTo());
            dto.setStatus(interview.getStatus().toString());
            dto.setNotes(interview.getNotes());
            dto.setFeedback(interview.getFeedback());
            dto.setMeetingLink(interview.getMeetingLink());
            dto.setMeetingRoomId(interview.getMeetingRoomId());
            
            // Fetch and set HR details
            Optional<User> hrUser = userRepository.findById(interview.getHrId());
            if (hrUser.isPresent()) {
                User hr = hrUser.get();
                dto.setHrId(hr.getId());
                dto.setHrEmail(hr.getEmail());
                
                // Try to get HR profile for additional details
                Optional<HRProfile> hrProfile = hrProfileRepository.findByUserId(hr.getId());
                if (hrProfile.isPresent()) {
                    HRProfile profile = hrProfile.get();
                    dto.setHrName(profile.getFullName());
                    dto.setHrPhone(profile.getPhone());
                    dto.setHrDesignation(profile.getDesignation());
                } else {
                    dto.setHrName(hr.getUsername());
                }
            }
            
            // Fetch and set Panelist details
            Optional<Panelist> panelist = panelistRepository.findById(interview.getPanelistId());
            if (panelist.isPresent()) {
                Panelist p = panelist.get();
                dto.setPanelistId(p.getId());
                dto.setPanelistName(p.getFullName());
                dto.setPanelistEmail(p.getEmail());
            }
            
            interviewDTOs.add(dto);
        }
        
        return interviewDTOs;
    }
    
    /**
     * Get candidate interviews by email with HR and panelist details
     */
    @Transactional(readOnly = true)
    public List<CandidateInterviewDTO> getCandidateInterviewsByEmailWithDetails(String candidateEmail) {
        List<Interview> interviews = interviewRepository.findByCandidateEmail(candidateEmail);
        List<CandidateInterviewDTO> interviewDTOs = new ArrayList<>();
        
        for (Interview interview : interviews) {
            CandidateInterviewDTO dto = new CandidateInterviewDTO();
            
            // Set interview details
            dto.setInterviewId(interview.getId());
            dto.setPosition(interview.getPosition());
            dto.setInterviewDate(interview.getInterviewDate());
            dto.setInterviewTimeFrom(interview.getInterviewTimeFrom());
            dto.setInterviewTimeTo(interview.getInterviewTimeTo());
            dto.setStatus(interview.getStatus().toString());
            dto.setNotes(interview.getNotes());
            dto.setFeedback(interview.getFeedback());
            dto.setMeetingLink(interview.getMeetingLink());
            dto.setMeetingRoomId(interview.getMeetingRoomId());
            
            // Fetch and set HR details
            Optional<User> hrUser = userRepository.findById(interview.getHrId());
            if (hrUser.isPresent()) {
                User hr = hrUser.get();
                dto.setHrId(hr.getId());
                dto.setHrEmail(hr.getEmail());
                
                // Try to get HR profile for additional details
                Optional<HRProfile> hrProfile = hrProfileRepository.findByUserId(hr.getId());
                if (hrProfile.isPresent()) {
                    HRProfile profile = hrProfile.get();
                    dto.setHrName(profile.getFullName());
                    dto.setHrPhone(profile.getPhone());
                    dto.setHrDesignation(profile.getDesignation());
                } else {
                    dto.setHrName(hr.getUsername());
                }
            }
            
            // Fetch and set Panelist details
            Optional<Panelist> panelist = panelistRepository.findById(interview.getPanelistId());
            if (panelist.isPresent()) {
                Panelist p = panelist.get();
                dto.setPanelistId(p.getId());
                dto.setPanelistName(p.getFullName());
                dto.setPanelistEmail(p.getEmail());
            }
            
            interviewDTOs.add(dto);
        }
        
        return interviewDTOs;
    }
    
    /**
     * Get all interviews for an HR
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByHrId(Long hrId) {
        return interviewRepository.findByHrId(hrId);
    }
    
    /**
     * Get interviews by HR ID and status
     */
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByHrIdAndStatus(Long hrId, InterviewStatus status) {
        return interviewRepository.findByHrIdAndStatus(hrId, status);
    }
}

// Made with Bob