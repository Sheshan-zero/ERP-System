package com.erp.manufacturing.security;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.security.dto.ModulePermissionRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

public final class PermissionCatalog {
    private PermissionCatalog() {}

    public static final Map<String, List<String>> MODULE_ACTIONS = createCatalog();

    private static Map<String, List<String>> createCatalog() {
        Map<String, List<String>> modules = new LinkedHashMap<>();
        modules.put("DASHBOARD", List.of("VIEW"));
        modules.put("ITEMS", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("WAREHOUSES", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("EMPLOYEES", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("SUPPLIERS", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("CUSTOMERS", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("PURCHASE_ORDERS", List.of("VIEW", "CREATE", "EDIT", "DELETE", "APPROVE", "RECEIVE"));
        modules.put("SALES_ORDERS", List.of("VIEW", "CREATE", "EDIT", "DELETE", "DELIVER"));
        modules.put("PRODUCTION_ORDERS", List.of("VIEW", "CREATE", "EDIT", "DELETE", "COMPLETE"));
        modules.put("BOMS", List.of("VIEW", "CREATE", "EDIT", "DELETE"));
        modules.put("INVENTORY_TRANSACTIONS", List.of("VIEW", "CREATE"));
        modules.put("WAREHOUSE_STOCK", List.of("VIEW"));
        modules.put("PAYMENTS", List.of("VIEW"));
        modules.put("GENERAL_LEDGER", List.of("VIEW", "CREATE"));
        modules.put("REPORTS", List.of("VIEW"));
        modules.put("NOTIFICATIONS", List.of("VIEW", "CREATE", "EDIT"));
        modules.put("AUDIT_LOGS", List.of("VIEW"));
        modules.put("USERS", List.of("VIEW", "CREATE", "EDIT"));
        return Collections.unmodifiableMap(modules);
    }

    public static Set<String> flatten(List<ModulePermissionRequest> rows) {
        Set<String> result = new LinkedHashSet<>();
        if (rows == null) return result;
        for (ModulePermissionRequest row : rows) {
            String module = normalize(row.getModule());
            List<String> allowed = MODULE_ACTIONS.get(module);
            if (allowed == null) throw new BusinessException("Unknown permission module: " + row.getModule());
            if (row.getActions() == null) continue;
            for (String value : row.getActions()) {
                String action = normalize(value);
                if (!allowed.contains(action)) {
                    throw new BusinessException("Action " + action + " is not valid for module " + module);
                }
                result.add(module + ":" + action);
            }
        }
        return result;
    }

    public static List<ModulePermissionRequest> group(Set<String> codes) {
        Map<String, Set<String>> grouped = new LinkedHashMap<>();
        if (codes != null) {
            for (String code : codes) {
                String[] parts = code.split(":", 2);
                if (parts.length == 2) grouped.computeIfAbsent(parts[0], key -> new LinkedHashSet<>()).add(parts[1]);
            }
        }
        List<ModulePermissionRequest> result = new ArrayList<>();
        grouped.forEach((module, actions) -> result.add(new ModulePermissionRequest(module, actions)));
        return result;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
