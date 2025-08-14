package com.gradlemedium100.security.filter;

import com.gradlemedium100.security.service.JwtService;
import com.gradlemedium100.security.service.UserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that intercepts each HTTP request, extracts JWT token, validates it,
 * and sets authentication in the security context if valid.
 * 
 * This filter extends OncePerRequestFilter to guarantee a single execution per request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    // Paths that don't require authentication
    private static final List<String> AUTH_WHITELIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/public/",
            "/swagger-ui/",
            "/v3/api-docs/"
    );
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    /**
     * Main filter method that processes each request.
     * Extracts JWT from request, validates it, and sets authentication if valid.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain
     * @throws ServletException If a servlet error occurs
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Skip authentication for whitelisted paths
            if (shouldSkipAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract JWT token from request
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null) {
                // Extract username from token
                String username = jwtService.extractUsername(jwt);
                
                // Validate token and set authentication if valid
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        setAuthenticationContext(jwt, userDetails);
                        logger.debug("Authentication set for user: {}", username);
                    } else {
                        logger.warn("Invalid JWT token");
                    }
                }
            } else {
                logger.debug("No JWT token found in request");
            }
            
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT token has expired: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        } catch (JwtException ex) {
            logger.error("JWT token error: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token error");
        } catch (Exception ex) {
            logger.error("Authentication error: {}", ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    /**
     * Extracts JWT token from the Authorization header.
     * Expected format: "Bearer token"
     *
     * @param request The HTTP request
     * @return The JWT token or null if not present
     */
    protected String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Extract token without "Bearer " prefix
            return bearerToken.substring(7);
        }
        
        // Check for token in request parameter as fallback
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }
    
    /**
     * Determines if authentication should be skipped for certain paths.
     *
     * @param request The HTTP request
     * @return True if authentication should be skipped, false otherwise
     */
    protected boolean shouldSkipAuthentication(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        
        // Skip authentication for whitelisted paths
        return AUTH_WHITELIST.stream().anyMatch(path -> 
                requestPath.startsWith(path) || requestPath.equals(path));
    }
    
    /**
     * Sets authentication in the security context.
     *
     * @param token The JWT token
     * @param userDetails The user details
     */
    protected void setAuthenticationContext(String token, UserDetails userDetails) {
        // Create authentication token with user details and authorities
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        // Set details from web request
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                (HttpServletRequest) SecurityContextHolder.getContext()));
        
        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        // TODO: Consider adding token to request attributes for reuse in controllers
    }
}