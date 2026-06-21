package com.erp.manufacturing.auditlog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long logId;
    private Long employeeId;
    private String employeeName;
    private String tableName;
    private String actionType;
    private Long recordId;
    private LocalDateTime actionDate;
    private String description;
}
