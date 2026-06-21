package com.erp.manufacturing.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long itemId;
    private @NotBlank @Size(max = 100) String itemName;
    private @NotBlank @Size(max = 30) String itemType;
    private @NotBlank @Size(max = 20) String unitOfMeasure;
    private @DecimalMin("0.00") BigDecimal currentStock;
    private @DecimalMin("0.00") BigDecimal reorderLevel;
    private @Size(max = 20) String itemStatus;
    private @Size(max = 255) String description;
    private LocalDateTime createdDate;
}
