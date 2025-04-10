package com.tarento.inventory.service;

import com.tarento.inventory.controller.request.InventoryRequestDto;
import com.tarento.inventory.entity.Inventory;
import com.tarento.inventory.exception.InventoryNotFoundException;
import com.tarento.inventory.repo.InventoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {

    @Autowired
    private InventoryRepo inventoryRepo;

    public Inventory createInventory(InventoryRequestDto inventoryRequestDto) {
        Inventory inventory = Inventory.builder()
                .name(inventoryRequestDto.getName())
                .price(inventoryRequestDto.getPrice())
                .description(inventoryRequestDto.getDescription())
                .quantity(inventoryRequestDto.getQuantity())
                .build();
        return inventoryRepo.save(inventory);
    }

    public Inventory getInventory(String inventoryId) {
        return inventoryRepo.findByIdAndIsDeleted(inventoryId, false).orElseThrow(() -> new InventoryNotFoundException("Inventory not found by id " + inventoryId));
    }

    public Page<Inventory> listInventory(Integer page, Integer size) {
        return inventoryRepo.findAllByIsDeleted(false, PageRequest.of(page - 1, size));
    }

    public Inventory updateInventory(String inventoryId, InventoryRequestDto inventoryRequestDto) {
        Inventory inventory = inventoryRepo.findByIdAndIsDeleted(inventoryId, false).orElseThrow(() -> new InventoryNotFoundException("Inventory not found by id " + inventoryId));
        inventory.setName(inventoryRequestDto.getName());
        inventory.setDescription(inventoryRequestDto.getDescription());
        inventory.setQuantity(inventoryRequestDto.getQuantity());
        inventory.setPrice(inventoryRequestDto.getPrice());
        return inventoryRepo.save(inventory);
    }

    public void deleteInventory(String inventoryId) {
        Inventory inventory = inventoryRepo.findByIdAndIsDeleted(inventoryId, false).orElseThrow(() -> new InventoryNotFoundException("Inventory not found by id " + inventoryId));
        inventory.setDeleted(true);
        inventoryRepo.save(inventory);
    }
}
