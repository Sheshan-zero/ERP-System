package com.erp.manufacturing.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + appUser.getRole().name()));

        if (appUser.getPermissionCodes() != null) {
            appUser.getPermissionCodes().forEach(permission -> authorities.add(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority(permission)
            ));
        }

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .authorities(authorities)
                .disabled(Boolean.FALSE.equals(appUser.getEnabled()))
                .build();
    }
}
