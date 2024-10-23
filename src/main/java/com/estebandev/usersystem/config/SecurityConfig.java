package com.estebandev.usersystem.config;

import com.estebandev.usersystem.entities.Token;
import com.estebandev.usersystem.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** SecurityConfig */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthFilter jwtAuthFilter;
  private final TokenRepository tokenRepository;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authz -> authz.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout -> {
              logout
                  .logoutUrl("/auth/logout")
                  .addLogoutHandler(
                      (request, response, authentication) -> {
                        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                        logout(authHeader);
                      })
                  .logoutSuccessHandler(
                      (request, response, authentication) -> SecurityContextHolder.clearContext());
            })
        .build();
  }

  private void logout(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid token");
    }

    final String jwtToken = token.substring(7);
    Token foundToken =
        tokenRepository
            .findByToken(jwtToken)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    foundToken.setExpired(true);
    foundToken.setRevoked(true);
    tokenRepository.save(foundToken);
  }
}
