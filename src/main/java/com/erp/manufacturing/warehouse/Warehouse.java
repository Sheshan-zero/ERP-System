package com.erp.manufacturing.warehouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "WAREHOUSE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAREHOUSE_ID", nullable = false)
    private Long warehouseId;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Warehouse name must not exceed 100 characters")
    @Column(name = "WAREHOUSE_NAME", length = 100)
    private String warehouseName;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    @Column(name = "LOCATION", length = 255)
    private String location;

    @DecimalMin(value = "0.00", message = "Capacity cannot be negative")
    @Column(name = "CAPACITY", precision = 12, scale = 2)
    private BigDecimal capacity;

    @Size(max = 100, message = "Manager name must not exceed 100 characters")
    @Column(name = "MANAGER_NAME", length = 100)
    private String managerName;
}
