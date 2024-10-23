package com.estebandev.usersystem.config;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.estebandev.usersystem.entities.Token;
import com.estebandev.usersystem.entities.User;
import com.estebandev.usersystem.repository.TokenRepository;
import com.estebandev.usersystem.repository.UserRepository;
import com.estebandev.usersystem.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JwtAuthFilter
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthFilter.class);

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    if (request.getServletPath().contains("/auth")) {
      filterChain.doFilter(request, response);
      logger.error("1");
      return;
    }

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);

      logger.error("2");
      return;
    }

    String jwtToken = authHeader.substring(7);
    logger.info("authHeader: " + authHeader);

    final String userEmail = jwtService.extractUsername(jwtToken);
    if (userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
      logger.error("3");
      return;
    }

    logger.info("token: " + jwtToken);

    Token token = tokenRepository.findByToken(jwtToken).orElse(null);

    if (token == null || token.isExpired() || token.isRevoked()) {
      filterChain.doFilter(request, response);
      logger.error("4");
      logger.error("Error: " + (token == null ? "token null" : token.isExpired() ? "token expired" : "token revoked"));
      return;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    Optional<User> user = userRepository.findByEmail(userEmail);
    if (user.isEmpty()) {
      filterChain.doFilter(request, response);
      logger.error("5");
      return;
    }

    final boolean tokenValid = jwtService.isTokenValid(jwtToken, user.get());

    if (!tokenValid) {
      logger.error("Token invalid");
      return;
    }

    final var authToken = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
    filterChain.doFilter(request, response);
  }
}
