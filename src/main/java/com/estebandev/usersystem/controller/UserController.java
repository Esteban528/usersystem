package com.estebandev.usersystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estebandev.usersystem.entities.User;
import com.estebandev.usersystem.services.UserService;
import org.slf4j.Logger;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UserController
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

  @GetMapping("/list")
  public ResponseEntity<List<User>> listUser() {
    logger.info("List users -->");

    List<User> users = userService.allUsers();
    return ResponseEntity.ok(users);
  }

}
