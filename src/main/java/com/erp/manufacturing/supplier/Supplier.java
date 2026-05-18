package com.erp.manufacturing.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER")
@Schema(description = "Supplier master data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUPPLIER_ID", nullable = false)
    private Long supplierId;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must not exceed 100 characters")
    @Column(name = "SUPPLIER_NAME", length = 100)
    private String supplierName;

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

    @Size(max = 20, message = "Supplier status must not exceed 20 characters")
    @Column(name = "SUPPLIER_STATUS", length = 20)
    private String supplierStatus;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    @Column(name = "CONTACT_PERSON", length = 100)
    private String contactPerson;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "PHONE", length = 20)
    private String phone;
}
