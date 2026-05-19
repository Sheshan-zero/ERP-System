package com.erp.manufacturing.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    public Customer createCustomer(Customer customer) {
        if (customer.getCustomerId() != null && customerRepository.existsById(customer.getCustomerId())) {
            throw new IllegalArgumentException("Customer already exists with id: " + customer.getCustomerId());
        }

        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setCustomerName(customer.getCustomerName());
        existingCustomer.setContactNo(customer.getContactNo());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setCustomerType(customer.getCustomerType());
        existingCustomer.setRegistrationDate(customer.getRegistrationDate());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
    }
}
