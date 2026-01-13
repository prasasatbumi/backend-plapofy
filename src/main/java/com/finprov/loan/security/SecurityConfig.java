package com.finprov.loan.security;

import com.finprov.loan.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(
                "/api/auth/login", "/api/auth/register", "/api/auth/reset-password")
                .permitAll()
                .requestMatchers("/api/public/**", "/api/files/**", "/api/loans/simulate", "/api/branches")
                .permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/plafonds/**")
                .permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/plafonds/**")
                .hasRole("SUPER_ADMIN")
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
                .requestMatchers("/api/openai/**")
                .hasRole("SUPER_ADMIN")
                .anyRequest()
                .authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }

  @Bean
  public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
    configuration.setAllowedOriginPatterns(java.util.List.of("*")); // Allow all origins for dev/testing
    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("*"));
    configuration.setAllowCredentials(true);
    org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}
