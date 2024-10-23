package com.estebandev.usersystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
  public enum TokenType {
    BEARER
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long id;

  String token;

  @Column(name = "token_type")
  TokenType tokenType;

  boolean expired;

  boolean revoked;

  @ManyToOne
  // @JoinColumn(name = "user_id")
  User user;
}
