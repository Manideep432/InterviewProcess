package com.login.security;

import com.login.model.User;
import com.login.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT Authentication Filter - Validates JWT tokens on each request
 * Enhanced with better error handling and logging
 *
 * @author Bob
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.debug("Processing request: {} {}", request.getMethod(), requestURI);
        
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                logger.debug("JWT token found in request");
                
                try {
                    if (jwtUtil.validateToken(token)) {
                        String username = jwtUtil.extractUsername(token);
                        logger.debug("Token validated for user: {}", username);
                        
                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            // Get user from database to get role
                            Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username);
                            
                            if (userOpt.isPresent()) {
                                User user = userOpt.get();
                                String role = user.getRole();
                                
                                if (role == null || role.trim().isEmpty()) {
                                    logger.error("User {} has no role assigned", username);
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.getWriter().write("{\"error\":\"User has no role assigned\"}");
                                    return;
                                }
                                
                                // Normalize role to uppercase (HR, CANDIDATE, PANELIST)
                                String normalizedRole = role.trim().toUpperCase();
                                
                                // Create authority with ROLE_ prefix
                                String authority = "ROLE_" + normalizedRole;
                                logger.info("Authenticating user: {} with authority: {}", username, authority);

                                UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                                    );
                                
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                logger.info("✅ Successfully authenticated user: {} with authority: {}", username, authority);
                            } else {
                                logger.error("❌ User not found in database: {}", username);
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("{\"error\":\"User not found\"}");
                                return;
                            }
                        }
                    } else {
                        logger.warn("❌ Invalid JWT token for request: {}", requestURI);
                    }
                } catch (Exception e) {
                    logger.error("Error validating JWT token: {}", e.getMessage(), e);
                }
            } else {
                logger.debug("No JWT token found in Authorization header for: {}", requestURI);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication for {}: {}", requestURI, e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
}

// Made with Bob