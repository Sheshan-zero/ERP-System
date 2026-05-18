package com.erp.manufacturing.inventorybalance;

import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.warehouse.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "INVENTORYBALANCE",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_INVENTORY_BALANCE_ITEM_WH",
                columnNames = {"ITEM_ID", "WAREHOUSE_ID"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVENTORY_BALANCE_ID", nullable = false)
    private Long inventoryBalanceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    @NotNull
    @DecimalMin(value = "0.00", message = "Quantity on hand cannot be negative")
    @Column(name = "QUANTITY_ON_HAND", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityOnHand;

    @Version
    @Column(name = "VERSION")
    private Long version;
}
