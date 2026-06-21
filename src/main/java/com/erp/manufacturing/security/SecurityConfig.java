package com.erp.manufacturing.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                        "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/dashboard/**").hasAuthority("DASHBOARD:VIEW")
                        .requestMatchers("/api/reports/**").hasAuthority("REPORTS:VIEW")
                        .requestMatchers(HttpMethod.GET, "/api/audit-logs/**").hasAuthority("AUDIT_LOGS:VIEW")
                        .requestMatchers(HttpMethod.GET, "/api/inventory-transactions/stock-by-warehouse").hasAuthority("WAREHOUSE_STOCK:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/*/approve").hasAuthority("PURCHASE_ORDERS:APPROVE")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/*/receive").hasAuthority("PURCHASE_ORDERS:RECEIVE")
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/*/deliver").hasAuthority("SALES_ORDERS:DELIVER")
                        .requestMatchers(HttpMethod.POST, "/api/production-orders/*/complete").hasAuthority("PRODUCTION_ORDERS:COMPLETE")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/*/sent").hasAuthority("NOTIFICATIONS:EDIT")
                        .requestMatchers(HttpMethod.GET, "/api/items/**").hasAuthority("ITEMS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/items/**").hasAuthority("ITEMS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/items/**").hasAuthority("ITEMS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/items/**").hasAuthority("ITEMS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**").hasAuthority("WAREHOUSES:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/**").hasAuthority("WAREHOUSES:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/warehouses/**").hasAuthority("WAREHOUSES:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**").hasAuthority("WAREHOUSES:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAuthority("EMPLOYEES:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/employees/**").hasAuthority("EMPLOYEES:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAuthority("EMPLOYEES:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAuthority("EMPLOYEES:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").hasAuthority("SUPPLIERS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**").hasAuthority("SUPPLIERS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasAuthority("SUPPLIERS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasAuthority("SUPPLIERS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAuthority("CUSTOMERS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/customers/**").hasAuthority("CUSTOMERS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAuthority("CUSTOMERS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasAuthority("CUSTOMERS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/**").hasAuthority("PURCHASE_ORDERS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/**").hasAuthority("PURCHASE_ORDERS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/purchase-orders/**").hasAuthority("PURCHASE_ORDERS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/purchase-orders/**").hasAuthority("PURCHASE_ORDERS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders/**").hasAuthority("SALES_ORDERS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/**").hasAuthority("SALES_ORDERS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/sales-orders/**").hasAuthority("SALES_ORDERS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/sales-orders/**").hasAuthority("SALES_ORDERS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/production-orders/**").hasAuthority("PRODUCTION_ORDERS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/production-orders/**").hasAuthority("PRODUCTION_ORDERS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/production-orders/**").hasAuthority("PRODUCTION_ORDERS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/production-orders/**").hasAuthority("PRODUCTION_ORDERS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/boms/**").hasAuthority("BOMS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/boms/**").hasAuthority("BOMS:CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/boms/**").hasAuthority("BOMS:EDIT")
                        .requestMatchers(HttpMethod.DELETE, "/api/boms/**").hasAuthority("BOMS:DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/inventory-transactions/**").hasAuthority("INVENTORY_TRANSACTIONS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/inventory-transactions/**").hasAuthority("INVENTORY_TRANSACTIONS:CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/general-ledger/**").hasAuthority("GENERAL_LEDGER:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/general-ledger/**").hasAuthority("GENERAL_LEDGER:CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").hasAuthority("NOTIFICATIONS:VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasAuthority("NOTIFICATIONS:CREATE")
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/**").denyAll()
                        .anyRequest().denyAll()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
