package com.erp.manufacturing.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank @Size(max = 100) String supplierName,
        @Size(max = 20) String contactNo,
        @Email @Size(max = 100) String email,
        @Size(max = 255) String address,
        @Size(max = 20) String supplierStatus,
        @Size(max = 100) String contactPerson,
        @Size(max = 20) String phone
) {
}
