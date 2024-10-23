package com.estebandev.usersystem.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.estebandev.usersystem.entities.User;
import com.estebandev.usersystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * UserService
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public List<User> allUsers() {
    return userRepository.findAll();
  }
}
