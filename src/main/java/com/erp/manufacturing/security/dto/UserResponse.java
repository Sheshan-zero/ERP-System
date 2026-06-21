package com.erp.manufacturing.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private boolean enabled;
    private Long employeeId;
    private List<ModulePermissionRequest> modulePermissions;
}
