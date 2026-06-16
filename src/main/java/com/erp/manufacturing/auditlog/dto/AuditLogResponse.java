package com.erp.manufacturing.auditlog.dto;

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