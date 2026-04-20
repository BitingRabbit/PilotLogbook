package de.dhbwravensburg.webeng.pilotlogbook.security;

import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
/**
 * Central Spring Security configuration for stateless JWT authentication
 */
public class SecurityConfig {

    private final PilotRepository pilotRepository;

    public SecurityConfig(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    /**
     * Configures request authorization, CORS, CSRF and JWT filter ordering
     *
     * @param http spring security HTTP configurer
     * @param jwtAuthFilter JWT authentication filter
     * @return configured security filter chain
     * @throws Exception when filter chain creation fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Loads application users by email for Spring Security authentication
     *
     * @return user details service implementation
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> pilotRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Pilot not found: " + email));
    }

    /**
     * @param authenticationConfiguration spring authentication configuration
     * @return authentication manager bean
     * @throws Exception when initialization fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Password encoder for hashing passwords
     *
     * @return password encoder based on BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines cross-origin settings used by Spring Security
     *
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        return request -> config;
    }
}

