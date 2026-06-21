package com.erp.manufacturing.security;

import com.erp.manufacturing.security.dto.UserCreateRequest;
import com.erp.manufacturing.security.dto.UserResponse;
import com.erp.manufacturing.security.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USERS:VIEW')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable)
                .map(user -> UserResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .enabled(user.getEnabled())
                        .employeeId(user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null)
                        .modulePermissions(PermissionCatalog.group(user.getPermissionCodes()))
                        .build());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USERS:CREATE')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        AppUser user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(user));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USERS:EDIT')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        AppUser user = userService.updateUser(id, request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    private UserResponse mapToResponse(AppUser user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .enabled(user.getEnabled())
                .employeeId(user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null)
                .modulePermissions(PermissionCatalog.group(user.getPermissionCodes()))
                .build();
    }

    @GetMapping("/permission-catalog")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USERS:VIEW')")
    public ResponseEntity<Map<String, List<String>>> getPermissionCatalog() {
        return ResponseEntity.ok(PermissionCatalog.MODULE_ACTIONS);
    }
}
