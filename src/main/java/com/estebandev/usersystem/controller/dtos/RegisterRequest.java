package com.estebandev.usersystem.controller.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * RegisterRequest
 */

public record RegisterRequest(
    @NotBlank(message = "Name cannot be blank") String name,

    @NotBlank(message = "Email cannot be blank") @Email(message = "Email must be valid") String email,

    String address,

    @NotBlank(message = "Password cannot be blank") @Size(min = 6, message = "Password must be at least 6 characters long") String password) {
}
