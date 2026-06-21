package com.erp.manufacturing.dashboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Top selling finished product summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDto {
    private Long itemId;
    private String itemName;
    private BigDecimal totalQuantitySold;
}
