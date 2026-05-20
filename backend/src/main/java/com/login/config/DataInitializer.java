package com.login.config;

import com.login.model.Candidate;
import com.login.model.HRProfile;
import com.login.model.Interview;
import com.login.model.InterviewFeedback;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.repository.CandidateRepository;
import com.login.repository.HRProfileRepository;
import com.login.repository.InterviewFeedbackRepository;
import com.login.repository.InterviewRepository;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Initializer - Creates test users on application startup
 *
 * @author Bob
 */
@Component
@org.springframework.core.annotation.Order(2) // Run after DatabaseSchemaValidator
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanelistRepository panelistRepository;

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private HRProfileRepository hrProfileRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private InterviewFeedbackRepository feedbackRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userRepository.count() == 0) {
            System.out.println("=================================================");
            System.out.println("Creating comprehensive test users for all roles...");
            System.out.println("=================================================");
            
            // ========== HR USERS ==========
            System.out.println("\n📋 Creating HR Users...");
            
            User hr1 = new User();
            hr1.setUsername("admin");
            hr1.setEmail("admin@example.com");
            hr1.setPassword(passwordEncoder.encode("admin123"));
            hr1.setRole("HR");
            userRepository.save(hr1);
            System.out.println("✅ Created HR user: admin / admin123 (admin@example.com)");
            
            User hr2 = new User();
            hr2.setUsername("hr_manager");
            hr2.setEmail("hr.manager@example.com");
            hr2.setPassword(passwordEncoder.encode("hr123"));
            hr2.setRole("HR");
            userRepository.save(hr2);
            System.out.println("✅ Created HR user: hr_manager / hr123 (hr.manager@example.com)");
            
            User hr3 = new User();
            hr3.setUsername("recruiter1");
            hr3.setEmail("recruiter1@example.com");
            hr3.setPassword(passwordEncoder.encode("recruiter123"));
            hr3.setRole("HR");
            userRepository.save(hr3);
            System.out.println("✅ Created HR user: recruiter1 / recruiter123 (recruiter1@example.com)");
            
            // ========== PANELIST USERS ==========
            System.out.println("\n👨‍💼 Creating Panelist Users...");
            
            User panelist1 = new User();
            panelist1.setUsername("panelist1");
            panelist1.setEmail("panelist1@example.com");
            panelist1.setPassword(passwordEncoder.encode("panelist123"));
            panelist1.setRole("PANELIST");
            userRepository.save(panelist1);
            System.out.println("✅ Created PANELIST user: panelist1 / panelist123 (panelist1@example.com)");
            
            User panelist2 = new User();
            panelist2.setUsername("panelist2");
            panelist2.setEmail("panelist2@example.com");
            panelist2.setPassword(passwordEncoder.encode("panelist123"));
            panelist2.setRole("PANELIST");
            userRepository.save(panelist2);
            System.out.println("✅ Created PANELIST user: panelist2 / panelist123 (panelist2@example.com)");
            
            User panelist3 = new User();
            panelist3.setUsername("tech_expert");
            panelist3.setEmail("tech.expert@example.com");
            panelist3.setPassword(passwordEncoder.encode("expert123"));
            panelist3.setRole("PANELIST");
            userRepository.save(panelist3);
            System.out.println("✅ Created PANELIST user: tech_expert / expert123 (tech.expert@example.com)");
            
            User panelist4 = new User();
            panelist4.setUsername("senior_dev");
            panelist4.setEmail("senior.dev@example.com");
            panelist4.setPassword(passwordEncoder.encode("senior123"));
            panelist4.setRole("PANELIST");
            userRepository.save(panelist4);
            System.out.println("✅ Created PANELIST user: senior_dev / senior123 (senior.dev@example.com)");
            
            // ========== CANDIDATE USERS ==========
            System.out.println("\n👤 Creating Candidate Users...");
            
            User candidate1 = new User();
            candidate1.setUsername("Manideep");
            candidate1.setEmail("manideep@example.com");
            candidate1.setPassword(passwordEncoder.encode("password123"));
            candidate1.setRole("CANDIDATE");
            userRepository.save(candidate1);
            System.out.println("✅ Created CANDIDATE user: Manideep / password123 (manideep@example.com)");
            
            User candidate2 = new User();
            candidate2.setUsername("Manideep1");
            candidate2.setEmail("manideep1@example.com");
            candidate2.setPassword(passwordEncoder.encode("password123"));
            candidate2.setRole("CANDIDATE");
            userRepository.save(candidate2);
            System.out.println("✅ Created CANDIDATE user: Manideep1 / password123 (manideep1@example.com)");
            
            User candidate3 = new User();
            candidate3.setUsername("testuser");
            candidate3.setEmail("test@example.com");
            candidate3.setPassword(passwordEncoder.encode("test123"));
            candidate3.setRole("CANDIDATE");
            userRepository.save(candidate3);
            System.out.println("✅ Created CANDIDATE user: testuser / test123 (test@example.com)");
            
            User candidate4 = new User();
            candidate4.setUsername("john_doe");
            candidate4.setEmail("john.doe@example.com");
            candidate4.setPassword(passwordEncoder.encode("john123"));
            candidate4.setRole("CANDIDATE");
            userRepository.save(candidate4);
            System.out.println("✅ Created CANDIDATE user: john_doe / john123 (john.doe@example.com)");
            
            User candidate5 = new User();
            candidate5.setUsername("jane_smith");
            candidate5.setEmail("jane.smith@example.com");
            candidate5.setPassword(passwordEncoder.encode("jane123"));
            candidate5.setRole("CANDIDATE");
            userRepository.save(candidate5);
            System.out.println("✅ Created CANDIDATE user: jane_smith / jane123 (jane.smith@example.com)");
            
            User candidate6 = new User();
            candidate6.setUsername("bob_johnson");
            candidate6.setEmail("bob.johnson@example.com");
            candidate6.setPassword(passwordEncoder.encode("bob123"));
            candidate6.setRole("CANDIDATE");
            userRepository.save(candidate6);
            System.out.println("✅ Created CANDIDATE user: bob_johnson / bob123 (bob.johnson@example.com)");
            
            // ========== PANELIST RECORDS ==========
            System.out.println("\n📝 Creating Panelist Records...");
            
            Panelist panelistRecord1 = new Panelist();
            panelistRecord1.setUser(panelist1);
            panelistRecord1.setAssignedHr(hr1);
            panelistRecord1.setSpecialization("Java Development");
            panelistRecord1.setExperienceYears(5);
            panelistRecord1.setExpertise("Spring Boot, Microservices, REST APIs");
            panelistRecord1.setActive(true);
            panelistRepository.save(panelistRecord1);
            System.out.println("✅ Created Panelist record for panelist1 (Java Development)");
            
            Panelist panelistRecord2 = new Panelist();
            panelistRecord2.setUser(panelist2);
            panelistRecord2.setAssignedHr(hr1);
            panelistRecord2.setSpecialization("Frontend Development");
            panelistRecord2.setExperienceYears(4);
            panelistRecord2.setExpertise("React, Angular, JavaScript, TypeScript");
            panelistRecord2.setActive(true);
            panelistRepository.save(panelistRecord2);
            System.out.println("✅ Created Panelist record for panelist2 (Frontend Development)");
            
            Panelist panelistRecord3 = new Panelist();
            panelistRecord3.setUser(panelist3);
            panelistRecord3.setAssignedHr(hr2);
            panelistRecord3.setSpecialization("Cloud Architecture");
            panelistRecord3.setExperienceYears(8);
            panelistRecord3.setExpertise("AWS, Azure, Kubernetes, Docker");
            panelistRecord3.setActive(true);
            panelistRepository.save(panelistRecord3);
            System.out.println("✅ Created Panelist record for tech_expert (Cloud Architecture)");
            
            Panelist panelistRecord4 = new Panelist();
            panelistRecord4.setUser(panelist4);
            panelistRecord4.setAssignedHr(hr3);
            panelistRecord4.setSpecialization("Database & Backend");
            panelistRecord4.setExperienceYears(7);
            panelistRecord4.setExpertise("PostgreSQL, MongoDB, Node.js, Python");
            panelistRecord4.setActive(true);
            panelistRepository.save(panelistRecord4);
            System.out.println("✅ Created Panelist record for senior_dev (Database & Backend)");
            
            // ========== CANDIDATE RECORDS WITH COMPLETE DATA ==========
            System.out.println("\n📄 Creating Candidate Records with Complete Information...");
            
            Candidate candidateRecord1 = new Candidate();
            candidateRecord1.setName("John Doe");
            candidateRecord1.setEmail("john.doe@example.com");
            candidateRecord1.setPhone("+91-9876543210");
            candidateRecord1.setPosition("Senior Java Developer");
            candidateRecord1.setJrs("JR-2024-001");
            candidateRecord1.setCandidateType("EXTERNAL");
            candidateRecord1.setExperienceYears(6);
            candidateRecord1.setSkills("Java, Spring Boot, Microservices, Docker, Kubernetes");
            candidateRecord1.setStatus("INTERVIEW");
            candidateRecord1.setHr(hr1);
            candidateRecord1.setAssignedPanelist(panelist1);
            candidateRecord1.setJdDetails("Looking for experienced Java developer with strong Spring Boot and microservices background. Must have hands-on experience with Docker and Kubernetes.");
            candidateRecord1.setJoiningDate(java.time.LocalDate.now().plusMonths(1));
            candidateRecord1.setCurrentCtc(new java.math.BigDecimal("1200000"));
            candidateRecord1.setOldCtc(new java.math.BigDecimal("1200000"));
            candidateRecord1.setNewCtc(new java.math.BigDecimal("1800000"));
            candidateRecord1.setEmploymentType("FULL_TIME");
            candidateRecord1.setLocation("Bangalore, India");
            candidateRecord1.setHrMailId("admin@example.com");
            candidateRecord1.setIsLoggedIn(true);
            candidateRecord1.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(15));
            candidateRepository.save(candidateRecord1);
            System.out.println("✅ Created Candidate: John Doe (INTERVIEW, Logged In)");
            
            Candidate candidateRecord2 = new Candidate();
            candidateRecord2.setName("Jane Smith");
            candidateRecord2.setEmail("jane.smith@example.com");
            candidateRecord2.setPhone("+91-9876543211");
            candidateRecord2.setPosition("React Developer");
            candidateRecord2.setJrs("JR-2024-002");
            candidateRecord2.setCandidateType("REFERRAL");
            candidateRecord2.setExperienceYears(3);
            candidateRecord2.setSkills("React, Redux, JavaScript, TypeScript, CSS, HTML5");
            candidateRecord2.setStatus("SCREENING");
            candidateRecord2.setHr(hr1);
            candidateRecord2.setAssignedPanelist(panelist2);
            candidateRecord2.setJdDetails("Frontend developer with expertise in React and modern JavaScript frameworks. Experience with state management and responsive design required.");
            candidateRecord2.setJoiningDate(java.time.LocalDate.now().plusMonths(2));
            candidateRecord2.setCurrentCtc(new java.math.BigDecimal("800000"));
            candidateRecord2.setOldCtc(new java.math.BigDecimal("800000"));
            candidateRecord2.setNewCtc(new java.math.BigDecimal("1200000"));
            candidateRecord2.setEmploymentType("FULL_TIME");
            candidateRecord2.setLocation("Hyderabad, India");
            candidateRecord2.setHrMailId("admin@example.com");
            candidateRecord2.setIsLoggedIn(true);
            candidateRecord2.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(5));
            candidateRepository.save(candidateRecord2);
            System.out.println("✅ Created Candidate: Jane Smith (SCREENING, Logged In)");
            
            Candidate candidateRecord3 = new Candidate();
            candidateRecord3.setName("Bob Johnson");
            candidateRecord3.setEmail("bob.johnson@example.com");
            candidateRecord3.setPhone("+91-9876543212");
            candidateRecord3.setPosition("Full Stack Developer");
            candidateRecord3.setJrs("JR-2024-003");
            candidateRecord3.setCandidateType("EXTERNAL");
            candidateRecord3.setExperienceYears(5);
            candidateRecord3.setSkills("Java, Spring, React, PostgreSQL, MongoDB, REST APIs");
            candidateRecord3.setStatus("SELECTED");
            candidateRecord3.setHr(hr1);
            candidateRecord3.setAssignedPanelist(panelist1);
            candidateRecord3.setJdDetails("Full stack developer proficient in both backend (Java/Spring) and frontend (React). Database experience with SQL and NoSQL required.");
            candidateRecord3.setJoiningDate(java.time.LocalDate.now().plusWeeks(3));
            candidateRecord3.setCurrentCtc(new java.math.BigDecimal("1500000"));
            candidateRecord3.setOldCtc(new java.math.BigDecimal("1500000"));
            candidateRecord3.setNewCtc(new java.math.BigDecimal("2200000"));
            candidateRecord3.setEmploymentType("FULL_TIME");
            candidateRecord3.setLocation("Pune, India");
            candidateRecord3.setHrMailId("admin@example.com");
            candidateRecord3.setIsLoggedIn(true);
            candidateRecord3.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(30));
            candidateRepository.save(candidateRecord3);
            System.out.println("✅ Created Candidate: Bob Johnson (SELECTED, Logged In)");
            
            Candidate candidateRecord4 = new Candidate();
            candidateRecord4.setName("Alice Williams");
            candidateRecord4.setEmail("alice.williams@example.com");
            candidateRecord4.setPhone("+91-9876543213");
            candidateRecord4.setPosition("DevOps Engineer");
            candidateRecord4.setJrs("JR-2024-004");
            candidateRecord4.setCandidateType("AGENCY");
            candidateRecord4.setExperienceYears(4);
            candidateRecord4.setSkills("AWS, Docker, Kubernetes, Jenkins, Terraform, CI/CD");
            candidateRecord4.setStatus("APPLIED");
            candidateRecord4.setHr(hr1);
            candidateRecord4.setJdDetails("DevOps engineer with strong cloud infrastructure experience. AWS certification preferred. Must have experience with containerization and CI/CD pipelines.");
            candidateRecord4.setJoiningDate(java.time.LocalDate.now().plusMonths(1));
            candidateRecord4.setCurrentCtc(new java.math.BigDecimal("1000000"));
            candidateRecord4.setOldCtc(new java.math.BigDecimal("1000000"));
            candidateRecord4.setNewCtc(new java.math.BigDecimal("1500000"));
            candidateRecord4.setEmploymentType("FULL_TIME");
            candidateRecord4.setLocation("Mumbai, India");
            candidateRecord4.setHrMailId("admin@example.com");
            candidateRecord4.setIsLoggedIn(true);
            candidateRecord4.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(10));
            candidateRepository.save(candidateRecord4);
            System.out.println("✅ Created Candidate: Alice Williams (APPLIED, Logged In)");
            
            Candidate candidateRecord5 = new Candidate();
            candidateRecord5.setName("Michael Brown");
            candidateRecord5.setEmail("michael.brown@example.com");
            candidateRecord5.setPhone("+91-9876543214");
            candidateRecord5.setPosition("Python Developer");
            candidateRecord5.setJrs("JR-2024-005");
            candidateRecord5.setCandidateType("INTERNAL");
            candidateRecord5.setExperienceYears(3);
            candidateRecord5.setSkills("Python, Django, Flask, PostgreSQL, REST APIs, Machine Learning");
            candidateRecord5.setStatus("SCREENING");
            candidateRecord5.setHr(hr1);
            candidateRecord5.setAssignedPanelist(panelist4);
            candidateRecord5.setJdDetails("Python developer with web framework experience. Knowledge of Django/Flask required. ML/AI experience is a plus.");
            candidateRecord5.setJoiningDate(java.time.LocalDate.now().plusMonths(2));
            candidateRecord5.setCurrentCtc(new java.math.BigDecimal("900000"));
            candidateRecord5.setOldCtc(new java.math.BigDecimal("900000"));
            candidateRecord5.setNewCtc(new java.math.BigDecimal("1400000"));
            candidateRecord5.setEmploymentType("FULL_TIME");
            candidateRecord5.setLocation("Chennai, India");
            candidateRecord5.setHrMailId("admin@example.com");
            candidateRecord5.setIsLoggedIn(true);
            candidateRecord5.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(20));
            candidateRepository.save(candidateRecord5);
            System.out.println("✅ Created Candidate: Michael Brown (SCREENING, Logged In)");
            
            // Additional candidates for variety
            Candidate candidateRecord6 = new Candidate();
            candidateRecord6.setName("Sarah Davis");
            candidateRecord6.setEmail("sarah.davis@example.com");
            candidateRecord6.setPhone("+91-9876543215");
            candidateRecord6.setPosition("UI/UX Designer");
            candidateRecord6.setJrs("JR-2024-006");
            candidateRecord6.setCandidateType("REFERRAL");
            candidateRecord6.setExperienceYears(4);
            candidateRecord6.setSkills("Figma, Adobe XD, Sketch, User Research, Prototyping");
            candidateRecord6.setStatus("INTERVIEW");
            candidateRecord6.setHr(hr1);
            candidateRecord6.setAssignedPanelist(panelist2);
            candidateRecord6.setJdDetails("Creative UI/UX designer with strong portfolio. Experience in user research and creating intuitive interfaces for web and mobile applications.");
            candidateRecord6.setJoiningDate(java.time.LocalDate.now().plusWeeks(6));
            candidateRecord6.setCurrentCtc(new java.math.BigDecimal("700000"));
            candidateRecord6.setOldCtc(new java.math.BigDecimal("700000"));
            candidateRecord6.setNewCtc(new java.math.BigDecimal("1100000"));
            candidateRecord6.setEmploymentType("FULL_TIME");
            candidateRecord6.setLocation("Bangalore, India");
            candidateRecord6.setHrMailId("admin@example.com");
            candidateRecord6.setIsLoggedIn(true);
            candidateRecord6.setLastLoginAt(java.time.LocalDateTime.now().minusMinutes(8));
            candidateRepository.save(candidateRecord6);
            System.out.println("✅ Created Candidate: Sarah Davis (INTERVIEW, Logged In)");
            
            // ========== HR PROFILES ==========
            System.out.println("\n👔 Creating HR Profiles...");
            
            HRProfile hrProfile1 = new HRProfile();
            hrProfile1.setUser(hr1);
            hrProfile1.setFullName("Admin HR Manager");
            hrProfile1.setPhone("+91-9999888877");
            hrProfile1.setDesignation("Senior HR Manager");
            hrProfile1.setDepartment("Human Resources");
            hrProfile1.setEmployeeId("HR001");
            hrProfile1.setExperienceYears(10);
            hrProfile1.setCompany("Tech Solutions Inc.");
            hrProfile1.setLocation("Bangalore, India");
            hrProfile1.setEmail("admin@example.com");
            hrProfile1.setBio("Experienced HR professional with 10+ years in talent acquisition and employee management.");
            hrProfile1.setSkills("Recruitment, Employee Relations, Performance Management, HR Analytics");
            hrProfile1.setActive(true);
            hrProfileRepository.save(hrProfile1);
            System.out.println("✅ Created HR Profile for admin");
            
            HRProfile hrProfile2 = new HRProfile();
            hrProfile2.setUser(hr2);
            hrProfile2.setFullName("Sarah Johnson");
            hrProfile2.setPhone("+91-9999888866");
            hrProfile2.setDesignation("HR Manager");
            hrProfile2.setDepartment("Human Resources");
            hrProfile2.setEmployeeId("HR002");
            hrProfile2.setExperienceYears(7);
            hrProfile2.setCompany("Tech Solutions Inc.");
            hrProfile2.setLocation("Hyderabad, India");
            hrProfile2.setEmail("hr.manager@example.com");
            hrProfile2.setBio("Passionate about building great teams and fostering positive work culture.");
            hrProfile2.setSkills("Talent Acquisition, Onboarding, Training & Development");
            hrProfile2.setActive(true);
            hrProfileRepository.save(hrProfile2);
            System.out.println("✅ Created HR Profile for hr_manager");
            
            HRProfile hrProfile3 = new HRProfile();
            hrProfile3.setUser(hr3);
            hrProfile3.setFullName("Michael Roberts");
            hrProfile3.setPhone("+91-9999888855");
            hrProfile3.setDesignation("Technical Recruiter");
            hrProfile3.setDepartment("Human Resources");
            hrProfile3.setEmployeeId("HR003");
            hrProfile3.setExperienceYears(5);
            hrProfile3.setCompany("Tech Solutions Inc.");
            hrProfile3.setLocation("Pune, India");
            hrProfile3.setEmail("recruiter1@example.com");
            hrProfile3.setBio("Specialized in technical recruitment with focus on software engineering roles.");
            hrProfile3.setSkills("Technical Screening, Candidate Sourcing, Interview Coordination");
            hrProfile3.setActive(true);
            hrProfileRepository.save(hrProfile3);
            System.out.println("✅ Created HR Profile for recruiter1");
            
            // ========== INTERVIEW SCHEDULES ==========
            System.out.println("\n📅 Creating Interview Schedules...");
            
            // Interview 1 - John Doe (Scheduled for tomorrow)
            Interview interview1 = new Interview();
            interview1.setHrId(hr1.getId());
            interview1.setPanelistId(panelistRecord1.getId());
            interview1.setCandidateId(candidateRecord1.getId());
            interview1.setCandidateName("John Doe");
            interview1.setCandidateEmail("john.doe@example.com");
            interview1.setInterviewDate(LocalDate.now().plusDays(1));
            interview1.setInterviewTimeFrom(LocalTime.of(10, 0));
            interview1.setInterviewTimeTo(LocalTime.of(11, 30));
            interview1.setPosition("Senior Java Developer");
            interview1.setNotes("Technical round focusing on Spring Boot, Microservices, and system design. Please prepare code samples.");
            interview1.setStatus(Interview.InterviewStatus.SCHEDULED);
            interview1.setMeetingLink("https://meet.google.com/abc-defg-hij");
            interview1.setMeetingRoomId("ROOM-001");
            interviewRepository.save(interview1);
            System.out.println("✅ Created Interview for John Doe (Tomorrow, 10:00 AM)");
            
            // Interview 2 - Jane Smith (Scheduled for day after tomorrow)
            Interview interview2 = new Interview();
            interview2.setHrId(hr1.getId());
            interview2.setPanelistId(panelistRecord2.getId());
            interview2.setCandidateId(candidateRecord2.getId());
            interview2.setCandidateName("Jane Smith");
            interview2.setCandidateEmail("jane.smith@example.com");
            interview2.setInterviewDate(LocalDate.now().plusDays(2));
            interview2.setInterviewTimeFrom(LocalTime.of(14, 0));
            interview2.setInterviewTimeTo(LocalTime.of(15, 30));
            interview2.setPosition("React Developer");
            interview2.setNotes("Frontend technical assessment. Focus on React, Redux, and component architecture. Live coding session included.");
            interview2.setStatus(Interview.InterviewStatus.SCHEDULED);
            interview2.setMeetingLink("https://meet.google.com/xyz-uvwx-rst");
            interview2.setMeetingRoomId("ROOM-002");
            interviewRepository.save(interview2);
            System.out.println("✅ Created Interview for Jane Smith (Day After Tomorrow, 2:00 PM)");
            
            // Interview 3 - Bob Johnson (Completed)
            Interview interview3 = new Interview();
            interview3.setHrId(hr1.getId());
            interview3.setPanelistId(panelistRecord1.getId());
            interview3.setCandidateId(candidateRecord3.getId());
            interview3.setCandidateName("Bob Johnson");
            interview3.setCandidateEmail("bob.johnson@example.com");
            interview3.setInterviewDate(LocalDate.now().minusDays(3));
            interview3.setInterviewTimeFrom(LocalTime.of(11, 0));
            interview3.setInterviewTimeTo(LocalTime.of(12, 30));
            interview3.setPosition("Full Stack Developer");
            interview3.setNotes("Full stack assessment covering both frontend and backend technologies.");
            interview3.setStatus(Interview.InterviewStatus.COMPLETED);
            interview3.setFeedback("Excellent performance! Strong technical skills in both Java and React. Good problem-solving approach. Recommended for hire.");
            interview3.setMeetingLink("https://meet.google.com/completed-001");
            interview3.setMeetingRoomId("ROOM-003");
            interviewRepository.save(interview3);
            System.out.println("✅ Created Interview for Bob Johnson (Completed - 3 days ago)");
            
            // Interview 4 - Sarah Davis (Scheduled for next week)
            Interview interview4 = new Interview();
            interview4.setHrId(hr2.getId());
            interview4.setPanelistId(panelistRecord2.getId());
            interview4.setCandidateId(candidateRecord6.getId());
            interview4.setCandidateName("Sarah Davis");
            interview4.setCandidateEmail("sarah.davis@example.com");
            interview4.setInterviewDate(LocalDate.now().plusDays(7));
            interview4.setInterviewTimeFrom(LocalTime.of(15, 30));
            interview4.setInterviewTimeTo(LocalTime.of(17, 0));
            interview4.setPosition("UI/UX Designer");
            interview4.setNotes("Portfolio review and design thinking assessment. Please bring your best work samples and be ready to discuss your design process.");
            interview4.setStatus(Interview.InterviewStatus.SCHEDULED);
            interview4.setMeetingLink("https://meet.google.com/design-review-001");
            interview4.setMeetingRoomId("ROOM-004");
            interviewRepository.save(interview4);
            System.out.println("✅ Created Interview for Sarah Davis (Next Week, 3:30 PM)");
            
            // Interview 5 - Michael Brown (Scheduled for 3 days from now)
            Interview interview5 = new Interview();
            interview5.setHrId(hr3.getId());
            interview5.setPanelistId(panelistRecord4.getId());
            interview5.setCandidateId(candidateRecord5.getId());
            interview5.setCandidateName("Michael Brown");
            interview5.setCandidateEmail("michael.brown@example.com");
            interview5.setInterviewDate(LocalDate.now().plusDays(3));
            interview5.setInterviewTimeFrom(LocalTime.of(9, 30));
            interview5.setInterviewTimeTo(LocalTime.of(11, 0));
            interview5.setPosition("Python Developer");
            interview5.setNotes("Python technical round. Focus on Django/Flask frameworks, REST API design, and database optimization.");
            interview5.setStatus(Interview.InterviewStatus.SCHEDULED);
            interview5.setMeetingLink("https://meet.google.com/python-tech-001");
            interview5.setMeetingRoomId("ROOM-005");
            interviewRepository.save(interview5);
            System.out.println("✅ Created Interview for Michael Brown (3 Days from Now, 9:30 AM)");
            
            // Interview 6 - John Doe (Another interview - Rescheduled)
            Interview interview6 = new Interview();
            interview6.setHrId(hr1.getId());
            interview6.setPanelistId(panelistRecord3.getId());
            interview6.setCandidateId(candidateRecord1.getId());
            interview6.setCandidateName("John Doe");
            interview6.setCandidateEmail("john.doe@example.com");
            interview6.setInterviewDate(LocalDate.now().plusDays(5));
            interview6.setInterviewTimeFrom(LocalTime.of(16, 0));
            interview6.setInterviewTimeTo(LocalTime.of(17, 0));
            interview6.setPosition("Senior Java Developer");
            interview6.setNotes("System design and architecture discussion. Cloud deployment strategies.");
            interview6.setStatus(Interview.InterviewStatus.RESCHEDULED);
            interview6.setMeetingLink("https://meet.google.com/system-design-001");
            interview6.setMeetingRoomId("ROOM-006");
            interviewRepository.save(interview6);
            System.out.println("✅ Created Interview for John Doe (Rescheduled - 5 Days from Now, 4:00 PM)");
            
            // ========== INTERVIEW FEEDBACK RECORDS ==========
            System.out.println("\n📋 Creating Interview Feedback Records...");
            
            try {
                // Feedback 1 - Bob Johnson (Complete feedback with all fields)
                InterviewFeedback feedback1 = new InterviewFeedback();
            feedback1.setInterviewId(interview3.getId());
            feedback1.setPanelistId(panelistRecord1.getId());
            feedback1.setCandidateId(candidateRecord3.getId());
            feedback1.setCandidateName("Bob Johnson");
            feedback1.setSource("External Hire");
            feedback1.setYearsOfExperience(5.0);
            feedback1.setYearsOfExperienceInTech(4.0);
            feedback1.setEvaluationType("VENDOR");
            feedback1.setEvaluatorNames("panelist1");
            feedback1.setEvaluationDate(LocalDate.now().minusDays(3));
            feedback1.setJobRoleSpecification("Full Stack Developer");
            feedback1.setJobDescription("Full stack developer proficient in both backend (Java/Spring) and frontend (React)");
            feedback1.setAccountName("Tech Solutions Inc.");
            feedback1.setJobLevel("SSE");
            feedback1.setCommunicationRating(8.5);
            feedback1.setAwsNativeServicesRating(7.5);
            feedback1.setAwsIntegrationServicesRating(7.0);
            feedback1.setAwsComputeServicesRating(8.0);
            feedback1.setProgrammingLanguageRating(9.0);
            feedback1.setAwsDevOpsServicesRating(7.5);
            feedback1.setAwsStorageRating(7.0);
            feedback1.setAgileScrumRating(8.0);
            feedback1.setAwsCliRating(6.5);
            feedback1.setDeploymentManagementRating(7.5);
            feedback1.setContainerOrchestrationRating(8.0);
            feedback1.setMicroservicesDesignPatternsRating(8.5);
            feedback1.setMicroservicesCommunicationRating(8.0);
            feedback1.setDisasterRecoveryRating(7.0);
            feedback1.setContainerizationRating(8.5);
            feedback1.setIacRating(7.0);
            feedback1.setFrontendStackRating(9.0);
            feedback1.setHtmlCssRating(8.5);
            feedback1.setSpringCloudAwsRating(7.5);
            feedback1.setSqlTuningRating(8.0);
            feedback1.setSetupPackagingRating(7.5);
            feedback1.setCertifications("AWS Certified Developer - Associate");
            feedback1.setOverallRating(8.0);
            feedback1.setToolRecommendation("SELECTED");
            feedback1.setTechPanelRecommendation("SELECTED");
            feedback1.setOverallFeedback("Excellent candidate with strong full-stack capabilities. Demonstrated good understanding of both frontend and backend technologies. Communication skills are strong and problem-solving approach is methodical.");
            feedback1.setSuitabilityForRequirement("Highly suitable for the Full Stack Developer role. Has the right mix of technical skills and experience.");
            feedback1.setImprovementFocusArea("Could improve knowledge in AWS native services and infrastructure as code tools like Terraform.");
            feedback1.setDeclarationAccepted(true);
            feedback1.setEvaluatorSignature("panelist1");
            feedback1.setStatus("SUBMITTED");
            feedback1.setSentToHR(true);
            feedback1.setSentToHRAt(LocalDateTime.now().minusDays(3));
            feedbackRepository.save(feedback1);
            System.out.println("✅ Created Feedback for Bob Johnson (SELECTED)");
            
            // Feedback 2 - John Doe (Partial feedback - some fields empty)
            InterviewFeedback feedback2 = new InterviewFeedback();
            feedback2.setInterviewId(interview1.getId());
            feedback2.setPanelistId(panelistRecord1.getId());
            feedback2.setCandidateId(candidateRecord1.getId());
            feedback2.setCandidateName("John Doe");
            feedback2.setSource("External Hire");
            feedback2.setYearsOfExperience(6.0);
            feedback2.setYearsOfExperienceInTech(5.0);
            feedback2.setEvaluationType("SELF");
            feedback2.setEvaluatorNames("panelist1");
            feedback2.setEvaluationDate(LocalDate.now().minusDays(1));
            feedback2.setJobRoleSpecification("Senior Java Developer");
            feedback2.setJobDescription("Looking for experienced Java developer with strong Spring Boot and microservices background");
            feedback2.setAccountName("Tech Solutions Inc.");
            feedback2.setJobLevel("TL");
            feedback2.setCommunicationRating(9.0);
            feedback2.setProgrammingLanguageRating(9.5);
            feedback2.setMicroservicesDesignPatternsRating(9.0);
            feedback2.setContainerizationRating(8.5);
            feedback2.setOverallRating(9.0);
            feedback2.setToolRecommendation("SELECTED");
            feedback2.setTechPanelRecommendation("SELECTED");
            feedback2.setOverallFeedback("Outstanding Java developer with deep expertise in Spring Boot and microservices architecture. Strong technical leadership potential.");
            feedback2.setSuitabilityForRequirement("Perfect fit for Senior Java Developer role.");
            feedback2.setImprovementFocusArea("None significant. Ready for immediate onboarding.");
            feedback2.setDeclarationAccepted(true);
            feedback2.setEvaluatorSignature("panelist1");
            feedback2.setStatus("SUBMITTED");
            feedback2.setSentToHR(true);
            feedback2.setSentToHRAt(LocalDateTime.now().minusDays(1));
            feedbackRepository.save(feedback2);
            System.out.println("✅ Created Feedback for John Doe (SELECTED - Partial Data)");
            
            // Feedback 3 - Jane Smith (Minimal feedback - mostly empty fields)
            InterviewFeedback feedback3 = new InterviewFeedback();
            feedback3.setInterviewId(interview2.getId());
            feedback3.setPanelistId(panelistRecord2.getId());
            feedback3.setCandidateId(candidateRecord2.getId());
            feedback3.setCandidateName("Jane Smith");
            feedback3.setSource("External Hire");
            feedback3.setYearsOfExperience(3.0);
            feedback3.setYearsOfExperienceInTech(3.0);
            feedback3.setEvaluationType("VENDOR");
            feedback3.setEvaluatorNames("panelist2");
            feedback3.setEvaluationDate(LocalDate.now());
            feedback3.setJobRoleSpecification("React Developer");
            feedback3.setJobDescription("Frontend developer with expertise in React");
            feedback3.setAccountName("Tech Solutions Inc.");
            feedback3.setJobLevel("SE");
            feedback3.setOverallRating(7.5);
            feedback3.setToolRecommendation("HOLD");
            feedback3.setTechPanelRecommendation("HOLD");
            feedback3.setOverallFeedback("Candidate demonstrated solid React fundamentals and acceptable communication skills. More depth is needed in advanced state management and frontend architecture before final selection.");
            feedback3.setSuitabilityForRequirement("Moderately suitable for the role, pending one more technical review.");
            feedback3.setImprovementFocusArea("Needs improvement in Redux patterns, performance optimization, and reusable component design.");
            feedback3.setDeclarationAccepted(true);
            feedback3.setEvaluatorSignature("panelist2");
            feedback3.setStatus("SUBMITTED");
            feedback3.setSentToHR(false);
            feedbackRepository.save(feedback3);
            System.out.println("✅ Created Feedback for Jane Smith (HOLD - Minimal Data)");
            
            // Feedback 4 - Sarah Davis (Rejected candidate)
            InterviewFeedback feedback4 = new InterviewFeedback();
            feedback4.setInterviewId(interview4.getId());
            feedback4.setPanelistId(panelistRecord2.getId());
            feedback4.setCandidateId(candidateRecord6.getId());
            feedback4.setCandidateName("Sarah Davis");
            feedback4.setSource("Internal");
            feedback4.setYearsOfExperience(4.0);
            feedback4.setYearsOfExperienceInTech(4.0);
            feedback4.setEvaluationType("IBM REFERAL");
            feedback4.setEvaluatorNames("panelist2");
            feedback4.setEvaluationDate(LocalDate.now().minusDays(2));
            feedback4.setJobRoleSpecification("UI/UX Designer");
            feedback4.setJobDescription("Creative UI/UX designer with strong portfolio");
            feedback4.setAccountName("Tech Solutions Inc.");
            feedback4.setJobLevel("SE");
            feedback4.setCommunicationRating(6.0);
            feedback4.setFrontendStackRating(5.5);
            feedback4.setHtmlCssRating(6.0);
            feedback4.setOverallRating(5.5);
            feedback4.setToolRecommendation("REJECTED");
            feedback4.setTechPanelRecommendation("REJECTED");
            feedback4.setOverallFeedback("Candidate lacks depth in UI/UX principles. Portfolio shows limited variety. Not meeting the required standards for this role.");
            feedback4.setSuitabilityForRequirement("Not suitable for current opening.");
            feedback4.setImprovementFocusArea("Needs to work on design fundamentals, user research methodologies, and build a stronger portfolio.");
            feedback4.setDeclarationAccepted(true);
            feedback4.setEvaluatorSignature("panelist2");
            feedback4.setStatus("REVIEWED");
            feedback4.setSentToHR(true);
            feedback4.setSentToHRAt(LocalDateTime.now().minusDays(2));
                feedbackRepository.save(feedback4);
                System.out.println("✅ Created Feedback for Sarah Davis (REJECTED)");
                
            } catch (Exception e) {
                System.err.println("❌ Error creating interview feedback records: " + e.getMessage());
                e.printStackTrace();
                System.err.println("⚠️  Feedback data will not be available. Please check database configuration.");
            }
            
            System.out.println("\n=================================================");
            System.out.println("✅ ALL TEST DATA CREATED SUCCESSFULLY!");
            System.out.println("=================================================");
            System.out.println("\n📊 SUMMARY:");
            System.out.println("  - HR Users: 3");
            System.out.println("  - Panelist Users: 4");
            System.out.println("  - Candidate Users: 6");
            System.out.println("  - Panelist Records: 4");
            System.out.println("  - Candidate Records: 6 (All Logged In with Complete Data)");
            System.out.println("  - HR Profiles: 3");
            System.out.println("  - Interview Schedules: 6");
            System.out.println("  - Interview Feedbacks: 4 (with varying data completeness)");
            System.out.println("\n🔐 LOGIN CREDENTIALS:");
            System.out.println("\n  HR USERS:");
            System.out.println("    • admin / admin123 (admin@example.com)");
            System.out.println("    • hr_manager / hr123 (hr.manager@example.com)");
            System.out.println("    • recruiter1 / recruiter123 (recruiter1@example.com)");
            System.out.println("\n  PANELIST USERS:");
            System.out.println("    • panelist1 / panelist123 (panelist1@example.com)");
            System.out.println("    • panelist2 / panelist123 (panelist2@example.com)");
            System.out.println("    • tech_expert / expert123 (tech.expert@example.com)");
            System.out.println("    • senior_dev / senior123 (senior.dev@example.com)");
            System.out.println("\n  CANDIDATE USERS:");
            System.out.println("    • Manideep / password123 (manideep@example.com)");
            System.out.println("    • Manideep1 / password123 (manideep1@example.com)");
            System.out.println("    • testuser / test123 (test@example.com)");
            System.out.println("    • john_doe / john123 (john.doe@example.com)");
            System.out.println("    • jane_smith / jane123 (jane.smith@example.com)");
            System.out.println("    • bob_johnson / bob123 (bob.johnson@example.com)");
            System.out.println("=================================================");
        } else {
            System.out.println("=================================================");
            System.out.println("Users already exist in database. Skipping initialization.");
            System.out.println("Total users: " + userRepository.count());
            System.out.println("=================================================");
        }
    }
}

// Made with Bob