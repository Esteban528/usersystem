package com.estebandev.usersystem.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.estebandev.usersystem.controller.dtos.LoginRequest;
import com.estebandev.usersystem.controller.dtos.RegisterRequest;
import com.estebandev.usersystem.controller.dtos.TokenResponse;
import com.estebandev.usersystem.entities.Token;
import com.estebandev.usersystem.entities.User;
import com.estebandev.usersystem.repository.TokenRepository;
import com.estebandev.usersystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final TokenRepository tokenRepository;
  private final AuthenticationProvider authenticationProvider;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  Logger logger = org.slf4j.LoggerFactory.getLogger(AuthService.class);

  public TokenResponse register(RegisterRequest registerRequest) {
    User user = User.builder()
        .name(registerRequest.name())
        .email(registerRequest.email())
        .address(registerRequest.address())
        .password(passwordEncoder.encode(registerRequest.password()))
        .build();

    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    userRepository.save(user);

    logger.debug("Registry OK");
    return new TokenResponse(jwtToken, refreshToken);
  }

  public TokenResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.email())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    authenticationProvider.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

    logger.debug("All Ok");
    return new TokenResponse(jwtToken, refreshToken);
  }

  private void revokeAllUserTokens(User user) {
    List<Token> tokenList = tokenRepository.findAllValidIsFalseOrRevokedIsFalseByUserId(user.getId());

    if (!tokenList.isEmpty()) {
      tokenList.forEach(token -> {
        token.setExpired(true);
        token.setRevoked(true);
      });
      tokenRepository.saveAll(tokenList);
    }

  }

  public TokenResponse refreshToken(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      throw new IllegalArgumentException("Invalid bearer token");

    String refreshToken = authHeader.substring(7);
    String userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail == null)
      throw new IllegalArgumentException("Invalid refresh token");

    User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

    if (jwtService.isTokenValid(refreshToken, user))
      throw new IllegalArgumentException("Invalid refresh token");

    final String accessToken = jwtService.generateToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, accessToken);

    return new TokenResponse(accessToken, refreshToken);
  }

  private void saveUserToken(User user, String jwtToken) {
    Token token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(Token.TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    tokenRepository.save(token);
  }
}
