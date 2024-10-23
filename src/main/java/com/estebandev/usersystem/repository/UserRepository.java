package com.estebandev.usersystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estebandev.usersystem.entities.User;
import java.util.List;

/**
 * UserRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> getUserById(long id);

  Optional<User> findByEmail(String email);

}
