package com.erp.manufacturing.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long itemId;

    private String itemName;

    private String itemType;

    private String unitOfMeasure;

    private BigDecimal currentStock;

    private BigDecimal reorderLevel;

    private String itemStatus;

    private String description;

    private LocalDateTime createdDate;

    private Long version;
}