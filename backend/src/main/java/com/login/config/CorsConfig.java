package com.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Global CORS Configuration
 * Ensures all API endpoints are accessible from the frontend
 * 
 * @author Bob
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Allow requests from frontend
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        
        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Expose headers to frontend
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

// Made with Bob