package com.estebandev.usersystem.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estebandev.usersystem.controller.dtos.LoginRequest;
import com.estebandev.usersystem.controller.dtos.RegisterRequest;
import com.estebandev.usersystem.controller.dtos.TokenResponse;
import com.estebandev.usersystem.services.AuthService;

import ch.qos.logback.core.subst.Token;
import lombok.RequiredArgsConstructor;

/**
 * AuthController
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

}
