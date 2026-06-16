package com.erp.manufacturing.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

        private Long customerId;

        private String customerName;

        private String contactNo;

        private String email;

        private String address;

        private String customerType;

        private LocalDateTime registrationDate;
}