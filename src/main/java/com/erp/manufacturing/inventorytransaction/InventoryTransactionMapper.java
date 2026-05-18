package com.erp.manufacturing.inventorytransaction;

import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.dto.InventoryTransactionRequest;
import com.erp.manufacturing.inventorytransaction.dto.InventoryTransactionResponse;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.warehouse.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class InventoryTransactionMapper {

    public InventoryTransaction toEntity(InventoryTransactionRequest request) {
        return InventoryTransaction.builder()
                .item(Item.builder().itemId(request.itemId()).build())
                .warehouse(request.warehouseId() == null ? null : Warehouse.builder().warehouseId(request.warehouseId()).build())
                .employee(request.employeeId() == null ? null : Employee.builder().employeeId(request.employeeId()).build())
                .transactionType(request.transactionType())
                .quantity(request.quantity())
                .transactionDate(request.transactionDate())
                .remarks(request.remarks())
                .build();
    }

    public InventoryTransactionResponse toResponse(InventoryTransaction transaction) {
        Item item = transaction.getItem();
        Warehouse warehouse = transaction.getWarehouse();
        Employee employee = transaction.getEmployee();

        return new InventoryTransactionResponse(
                transaction.getTransactionId(),
                item == null ? null : item.getItemId(),
                item == null ? null : item.getItemName(),
                warehouse == null ? null : warehouse.getWarehouseId(),
                warehouse == null ? null : warehouse.getWarehouseName(),
                employee == null ? null : employee.getEmployeeId(),
                employee == null ? null : employee.getEmployeeName(),
                transaction.getTransactionType(),
                transaction.getQuantity(),
                transaction.getTransactionDate(),
                transaction.getRemarks()
        );
    }

    public Page<InventoryTransactionResponse> toResponsePage(Page<InventoryTransaction> page) {
        return page.map(this::toResponse);
    }
}
