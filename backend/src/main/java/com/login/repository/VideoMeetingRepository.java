package com.login.repository;

import com.login.model.VideoMeeting;
import com.login.model.VideoMeeting.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * VideoMeeting Repository - Data access layer for video meetings
 * 
 * @author Bob
 */
@Repository
public interface VideoMeetingRepository extends JpaRepository<VideoMeeting, Long> {

    /**
     * Find meeting by room ID
     */
    Optional<VideoMeeting> findByRoomId(String roomId);

    /**
     * Find meeting by interview ID
     */
    Optional<VideoMeeting> findByInterviewId(Long interviewId);

    /**
     * Find all meetings by HR ID
     */
    List<VideoMeeting> findByHrId(Long hrId);

    /**
     * Find all meetings by panelist ID
     */
    List<VideoMeeting> findByPanelistId(Long panelistId);

    /**
     * Find all meetings by candidate ID
     */
    List<VideoMeeting> findByCandidateId(Long candidateId);

    /**
     * Find meetings by status
     */
    List<VideoMeeting> findByStatus(MeetingStatus status);

    /**
     * Find meetings by HR and status
     */
    List<VideoMeeting> findByHrIdAndStatus(Long hrId, MeetingStatus status);

    /**
     * Find meetings by panelist and status
     */
    List<VideoMeeting> findByPanelistIdAndStatus(Long panelistId, MeetingStatus status);

    /**
     * Find meetings by candidate and status
     */
    List<VideoMeeting> findByCandidateIdAndStatus(Long candidateId, MeetingStatus status);

    /**
     * Find meetings scheduled between dates
     */
    List<VideoMeeting> findByScheduledStartTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find active meetings (in progress or waiting)
     */
    List<VideoMeeting> findByStatusIn(List<MeetingStatus> statuses);

    /**
     * Check if room ID exists
     */
    boolean existsByRoomId(String roomId);

    /**
     * Count meetings by status
     */
    long countByStatus(MeetingStatus status);

    /**
     * Count meetings by HR and status
     */
    long countByHrIdAndStatus(Long hrId, MeetingStatus status);
}

// Made with Bob