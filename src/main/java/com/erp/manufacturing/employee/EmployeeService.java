package com.erp.manufacturing.employee;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    public Employee createEmployee(Employee employee) {
        if (employee.getEmployeeId() != null && employeeRepository.existsById(employee.getEmployeeId())) {
            throw new IllegalArgumentException("Employee already exists with id: " + employee.getEmployeeId());
        }

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Employee existingEmployee = getEmployeeById(id);

        existingEmployee.setEmployeeName(employee.getEmployeeName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setContactNo(employee.getContactNo());
        existingEmployee.setHireDate(employee.getHireDate());
        existingEmployee.setSalary(employee.getSalary());
        existingEmployee.setEmployeeType(employee.getEmployeeType());

        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
    }
}
