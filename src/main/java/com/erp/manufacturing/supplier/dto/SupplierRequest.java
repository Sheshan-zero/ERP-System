package com.erp.manufacturing.supplier.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    private @NotBlank @Size(max = 100) String supplierName;
    private @Size(max = 20) String contactNo;
    private @Email @Size(max = 100) String email;
    private @Size(max = 255) String address;
    private @Size(max = 20) String supplierStatus;
    private @Size(max = 100) String contactPerson;
    private @Size(max = 20) String phone;
}
