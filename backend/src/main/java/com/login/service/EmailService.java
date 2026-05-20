package com.login.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Service - Sends emails for OTP verification
 * 
 * @author Bob
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send OTP email
     */
    public void sendOtpEmail(String toEmail, String otp, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your Login OTP - Login Microservice");
            message.setText(buildOtpEmailBody(username, otp));
            
            mailSender.send(message);
            System.out.println("OTP email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }
    }

    /**
     * Build OTP email body
     */
    private String buildOtpEmailBody(String username, String otp) {
        return String.format(
            "Hello %s,\n\n" +
            "Your One-Time Password (OTP) for login is:\n\n" +
            "    %s\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "If you didn't request this OTP, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Login Microservice Team\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            username, otp
        );
    }

    /**
     * Send welcome email (optional)
     */
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Login Microservice!");
            message.setText(buildWelcomeEmailBody(username));
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
            // Don't throw exception for welcome email
        }
    }

    /**
     * Build welcome email body
     */
    private String buildWelcomeEmailBody(String username) {
        return String.format(
            "Hello %s,\n\n" +
            "Welcome to Login Microservice!\n\n" +
            "Your account has been successfully created.\n" +
            "You can now login using your credentials.\n\n" +
            "For security, we use Email OTP verification for all logins.\n" +
            "You will receive an OTP code via email each time you login.\n\n" +
            "Best regards,\n" +
            "Login Microservice Team",
            username
        );
    }

    /**
     * Send candidate details email with PDF attachment to HR
     */
    public void sendCandidateDetailsToHR(String hrEmail, String candidateName, byte[] pdfContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(hrEmail);
            helper.setSubject("New Candidate Registration - " + candidateName);
            helper.setText(buildCandidateEmailBody(candidateName));
            
            // Attach PDF
            helper.addAttachment("Candidate_Details_" + candidateName.replaceAll(" ", "_") + ".pdf",
                    new ByteArrayResource(pdfContent));
            
            mailSender.send(message);
            System.out.println("Candidate details email sent successfully to HR: " + hrEmail);
        } catch (Exception e) {
            System.err.println("Failed to send candidate details email: " + e.getMessage());
            throw new RuntimeException("Failed to send candidate details to HR. Please try again.");
        }
    }

    /**
     * Build candidate details email body
     */
    private String buildCandidateEmailBody(String candidateName) {
        return String.format(
            "Dear HR,\n\n" +
            "A new candidate has registered in the system.\n\n" +
            "Candidate Name: %s\n\n" +
            "Please find the complete candidate details in the attached PDF document.\n\n" +
            "You can also view this candidate's information in your HR Dashboard.\n\n" +
            "Best regards,\n" +
            "Login Microservice Team\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            candidateName
        );
    }

    /**
     * Send interview schedule notification to candidate
     */
    public void sendInterviewScheduleToCandidate(String candidateEmail, String candidateName,
                                                  String interviewDate, String interviewTime,
                                                  String position, String panelistName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(candidateEmail);
            message.setSubject("Interview Scheduled - " + position);
            message.setText(buildCandidateInterviewEmailBody(candidateName, interviewDate,
                                                             interviewTime, position, panelistName));
            
            mailSender.send(message);
            System.out.println("Interview schedule email sent successfully to candidate: " + candidateEmail);
        } catch (Exception e) {
            System.err.println("Failed to send interview schedule email to candidate: " + e.getMessage());
            throw new RuntimeException("Failed to send interview schedule email to candidate. Please try again.");
        }
    }

    /**
     * Send interview schedule notification to panelist
     */
    public void sendInterviewScheduleToPanelist(String panelistEmail, String panelistName,
                                                String candidateName, String candidateEmail,
                                                String interviewDate, String interviewTime,
                                                String position) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(panelistEmail);
            message.setSubject("New Interview Assigned - " + candidateName);
            message.setText(buildPanelistInterviewEmailBody(panelistName, candidateName,
                                                           candidateEmail, interviewDate,
                                                           interviewTime, position));
            
            mailSender.send(message);
            System.out.println("Interview schedule email sent successfully to panelist: " + panelistEmail);
        } catch (Exception e) {
            System.err.println("Failed to send interview schedule email to panelist: " + e.getMessage());
            throw new RuntimeException("Failed to send interview schedule email to panelist. Please try again.");
        }
    }

    /**
     * Build interview schedule email body for candidate
     */
    private String buildCandidateInterviewEmailBody(String candidateName, String interviewDate,
                                                    String interviewTime, String jrs,
                                                    String panelistName) {
        return String.format(
            "Dear %s,\n\n" +
            "Your interview has been scheduled!\n\n" +
            "Interview Details:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Position: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Interviewer: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Please be prepared and join on time.\n" +
            "Make sure you have:\n" +
            "  • A stable internet connection\n" +
            "  • Your resume and relevant documents\n" +
            "  • A quiet environment for the interview\n\n" +
            "If you need to reschedule, please contact HR immediately.\n\n" +
            "Best of luck!\n\n" +
            "Best regards,\n" +
            "HR Team\n" +
            "Login Microservice\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            candidateName, jrs, interviewDate, interviewTime, panelistName
        );
    }

    /**
     * Build interview schedule email body for panelist
     */
    private String buildPanelistInterviewEmailBody(String panelistName, String candidateName,
                                                   String candidateEmail, String interviewDate,
                                                   String interviewTime, String jrs) {
        return String.format(
            "Dear %s,\n\n" +
            "A new interview has been assigned to you!\n\n" +
            "Interview Details:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Candidate Name: %s\n" +
            "Candidate Email: %s\n" +
            "Position: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Please review the candidate's profile and prepare for the interview.\n" +
            "You can access the candidate's details from your Panelist Dashboard.\n\n" +
            "If you have any conflicts with this schedule, please contact HR immediately.\n\n" +
            "Best regards,\n" +
            "HR Team\n" +
            "Login Microservice\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            panelistName, candidateName, candidateEmail, jrs, interviewDate, interviewTime
        );
    }

    /**
     * Send Technical Interview Assessment Form to HR with PDF attachment
     */
    public void sendInterviewFeedbackToHR(String hrEmail, String candidateName,
                                          String panelistName, String jrs,
                                          String recommendation, byte[] pdfContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(hrEmail);
            helper.setSubject("Technical Interview Assessment - " + candidateName + " - " + recommendation);
            helper.setText(buildInterviewFeedbackEmailBody(candidateName, panelistName, jrs, recommendation));
            
            // Attach PDF
            String filename = "Technical_Interview_Assessment_" +
                            candidateName.replaceAll(" ", "_") + "_" +
                            System.currentTimeMillis() + ".pdf";
            helper.addAttachment(filename, new ByteArrayResource(pdfContent));
            
            mailSender.send(message);
            System.out.println("Interview feedback email sent successfully to HR: " + hrEmail);
        } catch (Exception e) {
            System.err.println("Failed to send interview feedback email: " + e.getMessage());
            throw new RuntimeException("Failed to send interview feedback to HR. Please try again.");
        }
    }

    /**
     * Build interview feedback email body for HR
     */
    private String buildInterviewFeedbackEmailBody(String candidateName, String panelistName,
                                                   String jrs, String recommendation) {
        return String.format(
            "Dear HR,\n\n" +
            "A Technical Interview Assessment Form has been submitted by the panelist.\n\n" +
            "Interview Details:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Candidate Name: %s\n" +
            "Position: %s\n" +
            "Panelist: %s\n" +
            "Recommendation: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Please find the complete Technical Interview Assessment Form in the attached PDF document.\n\n" +
            "The assessment includes:\n" +
            "  • Candidate's basic information and experience\n" +
            "  • Soft skills evaluation\n" +
            "  • Technical skills ratings (AWS, Programming, DevOps, etc.)\n" +
            "  • Certifications\n" +
            "  • Architecting and solutioning skills (if applicable)\n" +
            "  • Overall rating and recommendations\n" +
            "  • Detailed feedback and improvement areas\n" +
            "  • Panelist's declaration and signature\n\n" +
            "You can also view this assessment in your HR Dashboard.\n\n" +
            "Best regards,\n" +
            "Interview Management System\n" +
            "Login Microservice\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            candidateName, jrs, panelistName, recommendation
        );
    }
}

// Made with Bob