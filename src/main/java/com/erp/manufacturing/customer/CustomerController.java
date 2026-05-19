package com.erp.manufacturing.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.erp.manufacturing.common.DtoMapper;
import com.erp.manufacturing.customer.dto.CustomerRequest;
import com.erp.manufacturing.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Manage customer master data")
public class CustomerController {

    private final CustomerService customerService;
    private final DtoMapper dtoMapper;

    @GetMapping
    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(dtoMapper.mapPage(customerService.getAllCustomers(pageable), CustomerResponse.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(dtoMapper.map(customerService.getCustomerById(id), CustomerResponse.class));
    }

    @PostMapping
    @Operation(summary = "Create customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid customer request")
    })
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = dtoMapper.map(request, Customer.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.map(customerService.createCustomer(customer), CustomerResponse.class));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        Customer customer = dtoMapper.map(request, Customer.class);
        return ResponseEntity.ok(dtoMapper.map(customerService.updateCustomer(id, customer), CustomerResponse.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
