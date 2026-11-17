package com.carrental.dto;

import com.carrental.entity.Role;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}