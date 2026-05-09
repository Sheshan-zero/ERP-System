package com.erp.manufacturing.auditlog;

import com.erp.manufacturing.employee.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDITLOG")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID", nullable = false)
    private Long logId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;

    @Size(max = 100, message = "Table name must not exceed 100 characters")
    @Column(name = "TABLE_NAME", length = 100)
    private String tableName;

    @Size(max = 30, message = "Action type must not exceed 30 characters")
    @Column(name = "ACTION_TYPE", length = 30)
    private String actionType;

    @Column(name = "RECORD_ID")
    private Long recordId;

    @Column(name = "ACTION_DATE")
    private LocalDateTime actionDate;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
