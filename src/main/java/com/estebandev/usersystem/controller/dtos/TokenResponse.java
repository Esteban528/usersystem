package com.estebandev.usersystem.controller.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TokenResponse
 */
public record TokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken) {
}
