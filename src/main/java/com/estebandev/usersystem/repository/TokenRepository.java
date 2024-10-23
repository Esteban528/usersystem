package com.estebandev.usersystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.estebandev.usersystem.entities.Token;

/**
 * TokenRepository
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
  List<Token> findAllValidIsFalseOrRevokedIsFalseByUserId(long userId);

  @Query("SELECT t FROM Token t WHERE t.token = :token")
  Optional<Token> findByToken(String token);
}
