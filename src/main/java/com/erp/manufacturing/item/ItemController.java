package com.erp.manufacturing.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.erp.manufacturing.common.DtoMapper;
import com.erp.manufacturing.item.dto.ItemRequest;
import com.erp.manufacturing.item.dto.ItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Manage raw materials, finished products, and inventory item master data")
public class ItemController {

    private final ItemService itemService;
    private final DtoMapper dtoMapper;

    @GetMapping
    @Operation(summary = "Get all items", description = "Returns all inventory items.")
    @ApiResponse(responseCode = "200", description = "Items retrieved successfully")
    public ResponseEntity<Page<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(dtoMapper.mapPage(itemService.getAllItems(pageable), ItemResponse.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Returns one inventory item by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(dtoMapper.map(itemService.getItemById(id), ItemResponse.class));
    }

    @PostMapping
    @Operation(summary = "Create item", description = "Creates a new inventory item.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid item request")
    })
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        Item item = dtoMapper.map(request, Item.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.map(itemService.createItem(item), ItemResponse.class));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Updates an existing inventory item.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        Item item = dtoMapper.map(request, Item.class);
        return ResponseEntity.ok(dtoMapper.map(itemService.updateItem(id, item), ItemResponse.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Deletes an inventory item by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
