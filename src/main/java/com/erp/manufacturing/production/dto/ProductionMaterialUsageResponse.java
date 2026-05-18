package com.erp.manufacturing.production.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductionMaterialUsageResponse(
        Long usageId,
        Long rawMaterialId,
        BigDecimal quantityUsed,
        LocalDateTime usageDate
) {
}
