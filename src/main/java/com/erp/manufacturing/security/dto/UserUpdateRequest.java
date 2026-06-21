package com.erp.manufacturing.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    private String newPassword;
    private boolean enabled;
    private Long employeeId;
    private List<ModulePermissionRequest> modulePermissions;
}
