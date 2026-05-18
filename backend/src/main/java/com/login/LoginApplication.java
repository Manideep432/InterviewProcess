package com.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application for Login Microservice
 *
 * @author Bob
 */
@SpringBootApplication
@EnableScheduling
public class LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 Login Microservice Started Successfully!");
        System.out.println("📍 Server running on: http://localhost:8081");
        System.out.println("📊 H2 Console: http://localhost:8081/h2-console");
        System.out.println("📧 Email OTP Login: ENABLED");
        System.out.println("===========================================\n");
    }
}

// Made with Bob
