package com.erp.manufacturing.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private @NotBlank(message = "Username is required") String username;
    private @NotBlank(message = "Password is required") String password;
}
