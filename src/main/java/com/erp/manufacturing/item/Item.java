package com.erp.manufacturing.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ITEM")
@Schema(description = "Inventory item, including raw materials and finished products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq_gen")
    @SequenceGenerator(
            name = "item_seq_gen",
            sequenceName = "item_seq",
            allocationSize = 1
    )
    @Column(name = "ITEM_ID", nullable = false)
    private Long itemId;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    @Column(name = "ITEM_NAME", length = 100)
    private String itemName;

    @NotBlank(message = "Item type is required")
    @Size(max = 30, message = "Item type must not exceed 30 characters")
    @Column(name = "ITEM_TYPE", length = 30)
    private String itemType;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20, message = "Unit of measure must not exceed 20 characters")
    @Column(name = "UNIT_OF_MEASURE", length = 20)
    private String unitOfMeasure;

    @DecimalMin(value = "0.00", message = "Current stock cannot be negative")
    @Column(name = "CURRENT_STOCK", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @DecimalMin(value = "0.00", message = "Reorder level cannot be negative")
    @Column(name = "REORDER_LEVEL", precision = 10, scale = 2)
    private BigDecimal reorderLevel;

    @Size(max = 20, message = "Item status must not exceed 20 characters")
    @Column(name = "ITEM_STATUS", length = 20)
    private String itemStatus;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Version
    @Column(name = "VERSION")
    private Long version;
}
