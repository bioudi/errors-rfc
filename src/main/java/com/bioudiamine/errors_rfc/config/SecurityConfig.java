package com.bioudiamine.errors_rfc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.net.URI;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/sales/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {})
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(problemDetailAuthenticationEntryPoint())
            .accessDeniedHandler(problemDetailAccessDeniedHandler())
        )
        .build();
  }

  private AuthenticationEntryPoint problemDetailAuthenticationEntryPoint() {
    return (request, response, authException) -> {
      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
          HttpStatus.UNAUTHORIZED, authException.getMessage());
      problemDetail.setTitle("Unauthorized");
      problemDetail.setType(URI.create("https://api.example.com/errors/unauthorized"));
      problemDetail.setInstance(URI.create(request.getRequestURI()));

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      objectMapper.writeValue(response.getOutputStream(), problemDetail);
    };
  }

  @Bean
  public UserDetailsService userDetailsService() {
    var user = User.withUsername("user")
        .password("{noop}password")
        .roles("USER")
        .build();
    var admin = User.withUsername("admin")
        .password("{noop}password")
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(user, admin);
  }

  private AccessDeniedHandler problemDetailAccessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
          HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
      problemDetail.setTitle("Forbidden");
      problemDetail.setType(URI.create("https://api.example.com/errors/forbidden"));
      problemDetail.setInstance(URI.create(request.getRequestURI()));

      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      objectMapper.writeValue(response.getOutputStream(), problemDetail);
    };
  }
}
