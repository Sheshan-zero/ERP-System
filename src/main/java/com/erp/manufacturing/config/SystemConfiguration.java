package com.erp.manufacturing.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SYSTEMCONFIGURATION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfiguration {

    @Id
    @Column(name = "CONFIG_KEY", nullable = false, length = 100)
    private String configKey;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 255)
    private String configValue;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
