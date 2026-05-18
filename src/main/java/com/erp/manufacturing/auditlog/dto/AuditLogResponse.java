package com.erp.manufacturing.auditlog.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long logId,
        Long employeeId,
        String employeeName,
        String tableName,
        String actionType,
        Long recordId,
        LocalDateTime actionDate,
        String description
) {
}
