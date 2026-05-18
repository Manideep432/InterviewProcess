package com.login.config;

import com.login.model.Candidate;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.repository.CandidateRepository;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer - Creates test users on application startup
 * 
 * @author Bob
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanelistRepository panelistRepository;

    @Autowired
    private CandidateRepository candidateRepository;

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
            
            System.out.println("\n=================================================");
            System.out.println("✅ ALL TEST DATA CREATED SUCCESSFULLY!");
            System.out.println("=================================================");
            System.out.println("\n📊 SUMMARY:");
            System.out.println("  - HR Users: 3");
            System.out.println("  - Panelist Users: 4");
            System.out.println("  - Candidate Users: 6");
            System.out.println("  - Panelist Records: 4");
            System.out.println("  - Candidate Records: 6 (All Logged In with Complete Data)");
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