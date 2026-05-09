package com.erp.manufacturing.common.enums;

public enum InventoryTransactionType {
    StockIn("Stock In"),
    StockOut("Stock Out");

    private final String value;

    InventoryTransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
