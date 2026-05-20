package com.login.service;

import com.login.model.Interview;
import com.login.model.Interview.InterviewStatus;
import com.login.repository.InterviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Interview Scheduler Service - Automatically updates interview statuses
 * Checks every minute for interviews that have passed their end time
 * 
 * @author Bob
 */
@Service
public class InterviewSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewSchedulerService.class);

    @Autowired
    private InterviewRepository interviewRepository;

    /**
     * Scheduled task that runs every minute to check and update interview statuses
     * Marks interviews as COMPLETED if their end time has passed
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    @Transactional
    public void checkAndUpdateInterviewStatuses() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalTime currentTime = now.toLocalTime();

            logger.debug("Running scheduled interview status check at {}", now);

            // Find all SCHEDULED interviews
            List<Interview> scheduledInterviews = interviewRepository.findByStatus(InterviewStatus.SCHEDULED);
            
            int updatedCount = 0;
            for (Interview interview : scheduledInterviews) {
                LocalDate interviewDate = interview.getInterviewDate();
                LocalTime interviewTimeTo = interview.getInterviewTimeTo();

                // Check if interview date is before today, or if it's today and end time has passed
                boolean shouldComplete = false;
                
                if (interviewDate.isBefore(today)) {
                    // Interview was on a past date
                    shouldComplete = true;
                } else if (interviewDate.isEqual(today) && interviewTimeTo != null && currentTime.isAfter(interviewTimeTo)) {
                    // Interview is today and end time has passed
                    shouldComplete = true;
                }

                if (shouldComplete) {
                    interview.setStatus(InterviewStatus.COMPLETED);
                    interviewRepository.save(interview);
                    updatedCount++;
                    logger.info("Auto-completed interview ID: {} for candidate: {} (Date: {}, End Time: {})",
                            interview.getId(), interview.getCandidateName(), interviewDate, interviewTimeTo);
                }
            }

            if (updatedCount > 0) {
                logger.info("Auto-completed {} interview(s)", updatedCount);
            }

        } catch (Exception e) {
            logger.error("Error in scheduled interview status check: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual trigger to update interview statuses
     * Can be called via API endpoint for immediate update
     */
    @Transactional
    public int updateExpiredInterviews() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalTime currentTime = now.toLocalTime();

            logger.info("Manual trigger: Checking for expired interviews at {}", now);

            List<Interview> scheduledInterviews = interviewRepository.findByStatus(InterviewStatus.SCHEDULED);
            
            int updatedCount = 0;
            for (Interview interview : scheduledInterviews) {
                LocalDate interviewDate = interview.getInterviewDate();
                LocalTime interviewTimeTo = interview.getInterviewTimeTo();

                boolean shouldComplete = false;
                
                if (interviewDate.isBefore(today)) {
                    shouldComplete = true;
                } else if (interviewDate.isEqual(today) && interviewTimeTo != null && currentTime.isAfter(interviewTimeTo)) {
                    shouldComplete = true;
                }

                if (shouldComplete) {
                    interview.setStatus(InterviewStatus.COMPLETED);
                    interviewRepository.save(interview);
                    updatedCount++;
                    logger.info("Manually completed interview ID: {} for candidate: {}",
                            interview.getId(), interview.getCandidateName());
                }
            }

            logger.info("Manual trigger completed: {} interview(s) updated", updatedCount);
            return updatedCount;

        } catch (Exception e) {
            logger.error("Error in manual interview status update: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update expired interviews: " + e.getMessage());
        }
    }
}

// Made with Bob