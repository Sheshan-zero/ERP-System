package com.erp.manufacturing.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
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