package com.gradlemedium100.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gradlemedium100.security.filter.JwtAuthenticationFilter;
import com.gradlemedium100.security.service.impl.UserDetailsServiceImpl;

import java.util.Arrays;

/**
 * Main security configuration class for the application.
 * This class sets up Spring Security, defines the security filter chain,
 * authentication provider, password encoder, and security rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * Configures HTTP security settings with the new approach (replacing WebSecurityConfigurerAdapter).
     * - Disables CSRF protection as we use JWT
     * - Sets up authorization for different endpoints
     * - Configures session management as stateless
     * - Adds JWT filter before the standard authentication filter
     * 
     * @param http HttpSecurity object to configure
     * @return The configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()  // Public authentication endpoints
                .antMatchers("/api/public/**").permitAll() // Public API endpoints
                .antMatchers("/actuator/**").permitAll()   // Actuator endpoints for monitoring
                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll() // Swagger UI
                .antMatchers("/api/admin/**").hasRole("ADMIN")  // Admin only endpoints
                .anyRequest().authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // TODO: Consider adding custom authentication entry point for better error messages
        
        return http.build();
    }

    /**
     * Configures authentication manager to use our custom user details service
     * and password encoder.
     */
    @Autowired
    void configureAuthenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Creates a password encoder bean for the application.
     * Uses BCrypt as the encoding algorithm with default strength.
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // FIXME: Consider configuring strength parameter based on environment
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an authentication manager bean that can be injected elsewhere.
     * This is required for programmatic authentication in services.
     * 
     * @param authenticationConfiguration the Spring Security authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if bean creation fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures CORS settings for the application.
     * Allows cross-origin requests from specified origins.
     * 
     * @return CorsConfigurationSource instance
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://gradlemedium100.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}