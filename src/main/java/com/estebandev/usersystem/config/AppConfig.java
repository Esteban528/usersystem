package com.estebandev.usersystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.estebandev.usersystem.entities.User;
import com.estebandev.usersystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * AppConfig
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {
  private final UserRepository userRepository;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService() {
    return username -> {
      final User user = userRepository.findByEmail(username)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      return org.springframework.security.core.userdetails.User.builder()
          .username(user.getEmail())
          .password(user.getPassword())
          .build();
    };
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }
}
