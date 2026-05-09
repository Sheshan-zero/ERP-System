package com.erp.manufacturing.item;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    }

    public Item createItem(Item item) {
        if (itemRepository.existsById(item.getItemId())) {
            throw new IllegalArgumentException("Item already exists with id: " + item.getItemId());
        }

        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item item) {
        Item existingItem = getItemById(id);

        existingItem.setItemName(item.getItemName());
        existingItem.setItemType(item.getItemType());
        existingItem.setUnitOfMeasure(item.getUnitOfMeasure());
        existingItem.setCurrentStock(item.getCurrentStock());
        existingItem.setReorderLevel(item.getReorderLevel());
        existingItem.setItemStatus(item.getItemStatus());
        existingItem.setDescription(item.getDescription());
        existingItem.setCreatedDate(item.getCreatedDate());

        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found with id: " + id);
        }

        itemRepository.deleteById(id);
    }
}
