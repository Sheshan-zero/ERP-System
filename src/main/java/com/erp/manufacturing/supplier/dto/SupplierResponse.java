package com.erp.manufacturing.supplier.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long supplierId;
    private String supplierName;
    private String contactNo;
    private String email;
    private String address;
    private String supplierStatus;
    private String contactPerson;
    private String phone;
}
