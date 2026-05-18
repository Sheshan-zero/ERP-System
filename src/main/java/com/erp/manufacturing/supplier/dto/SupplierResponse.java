package com.erp.manufacturing.supplier.dto;

public record SupplierResponse(
        Long supplierId,
        String supplierName,
        String contactNo,
        String email,
        String address,
        String supplierStatus,
        String contactPerson,
        String phone
) {
}
