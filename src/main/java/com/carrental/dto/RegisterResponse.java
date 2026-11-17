package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.entity.Role;

public record RegisterResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        LocalDateTime createdAt
) {
}