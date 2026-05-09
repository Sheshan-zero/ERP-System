package com.erp.manufacturing.security.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role
) {
}
