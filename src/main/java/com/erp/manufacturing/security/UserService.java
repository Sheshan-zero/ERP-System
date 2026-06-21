package com.erp.manufacturing.security;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.security.dto.UserCreateRequest;
import com.erp.manufacturing.security.dto.UserUpdateRequest;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<AppUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public AppUser createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .permissionCodes(PermissionCatalog.flatten(request.getModulePermissions()))
                .enabled(request.isEnabled())
                .employee(resolveEmployee(request.getEmployeeId()))
                .build();

        return userRepository.save(user);
    }

    public AppUser updateUser(Long id, UserUpdateRequest request) {
        AppUser user = getUserById(id);
        java.util.Set<String> requestedPermissions = PermissionCatalog.flatten(request.getModulePermissions());

        protectLastAccessAdministrator(id, request.isEnabled(), requestedPermissions);

        userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            if (!existing.getUserId().equals(id)) throw new BusinessException("Username already exists");
        });
        user.setUsername(request.getUsername());
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }
        user.setPermissionCodes(requestedPermissions);
        user.setEnabled(request.isEnabled());
        user.setEmployee(resolveEmployee(request.getEmployeeId()));

        return userRepository.save(user);
    }

    private Employee resolveEmployee(Long employeeId) {
        if (employeeId == null) return null;
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
    }

    private void protectLastAccessAdministrator(Long editedUserId, boolean enabled, java.util.Set<String> requested) {
        for (String required : java.util.List.of("USERS:CREATE", "USERS:EDIT")) {
            if (enabled && requested.contains(required)) continue;
            boolean anotherAdministrator = userRepository.findAll().stream()
                    .anyMatch(candidate -> !candidate.getUserId().equals(editedUserId)
                            && Boolean.TRUE.equals(candidate.getEnabled())
                            && candidate.getPermissionCodes().contains(required));
            if (!anotherAdministrator) {
                throw new BusinessException("At least one enabled user must retain " + required + " permission");
            }
        }
    }
}
