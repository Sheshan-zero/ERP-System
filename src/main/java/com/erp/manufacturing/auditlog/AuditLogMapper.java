package com.erp.manufacturing.auditlog;

import com.erp.manufacturing.auditlog.dto.AuditLogResponse;
import com.erp.manufacturing.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(AuditLog auditLog) {
        Employee employee = auditLog.getEmployee();

        return new AuditLogResponse(
                auditLog.getLogId(),
                employee == null ? null : employee.getEmployeeId(),
                employee == null ? null : employee.getEmployeeName(),
                auditLog.getTableName(),
                auditLog.getActionType(),
                auditLog.getRecordId(),
                auditLog.getActionDate(),
                auditLog.getDescription()
        );
    }

    public Page<AuditLogResponse> toResponsePage(Page<AuditLog> page) {
        return page.map(this::toResponse);
    }
}
