package com.estebandev.usersystem.controller.dtos;

/**
 * LoginRequest
 */
public record LoginRequest(
    String email, String password) {
}
