package com.erp.manufacturing.salesorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderItemResponse {

    private Long salesOrderItemId;

    private Long finishedProductId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}