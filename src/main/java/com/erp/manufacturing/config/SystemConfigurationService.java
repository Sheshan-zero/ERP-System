package com.erp.manufacturing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SystemConfigurationService {

    public static final String PURCHASE_APPROVAL_THRESHOLD = "purchase.approval.threshold";

    private final SystemConfigurationRepository systemConfigurationRepository;

    @Transactional(readOnly = true)
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return systemConfigurationRepository.findById(key)
                .map(SystemConfiguration::getConfigValue)
                .map(BigDecimal::new)
                .orElse(defaultValue);
    }
}
