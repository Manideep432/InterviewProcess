package com.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides a simple health check endpoint for frontend to verify backend availability
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    /**
     * Simple health check endpoint
     * Returns 200 OK if backend is running
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "login-microservice");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Detailed health check with system info
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "login-microservice");
        
        // System info
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        system.put("freeMemory", Runtime.getRuntime().freeMemory());
        system.put("totalMemory", Runtime.getRuntime().totalMemory());
        
        response.put("system", system);
        
        return ResponseEntity.ok(response);
    }
}

// Made with Bob