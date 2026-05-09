package com.erp.manufacturing.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    @Column(name = "CUSTOMER_NAME", length = 100)
    private String customerName;

    @Size(max = 20, message = "Contact number must not exceed 20 characters")
    @Column(name = "CONTACT_NO", length = 20)
    private String contactNo;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "EMAIL", length = 100)
    private String email;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Size(max = 30, message = "Customer type must not exceed 30 characters")
    @Column(name = "CUSTOMER_TYPE", length = 30)
    private String customerType;

    @Column(name = "REGISTRATION_DATE")
    private LocalDateTime registrationDate;
}
