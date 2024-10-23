package com.estebandev.usersystem.services;

import com.estebandev.usersystem.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** JwtService */
@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${spring.security.jwt.secret-key}")
  private String secretKey;

  @Value("${spring.security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${spring.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtService.class);

  /**
   * @param user Generate token with user information
   */
  public String generateToken(User user) {
    return buildToken(user, jwtExpiration);
  }

  public String generateRefreshToken(User user) {
    return buildToken(user, refreshExpiration);
  }

  public String extractUsername(String token) {
    return getTokenPayload(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return getTokenPayload(token).getExpiration();
  }

  public boolean isTokenValid(String token, User user) {
    String tokenUserName = extractUsername(token);
    logger.info("Token username: " + tokenUserName);

    return tokenUserName.equals(user.getEmail()) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    Date expiration = extractExpiration(token);
    return expiration.before(new Date());
  }

  private String buildToken(User user, long expiration) {
    return Jwts.builder()
        .id(Long.toString(user.getId()))
        .claims(Map.of("name", user.getName()))
        .subject(user.getEmail())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey())
        .compact();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims getTokenPayload(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }
}
