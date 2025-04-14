package com.tarento.inventory.controller;

import com.tarento.inventory.controller.request.InventoryRequestDto;
import com.tarento.inventory.controller.response.ErrorResponseDto;
import com.tarento.inventory.controller.response.InventoryResponseDto;
import com.tarento.inventory.controller.response.ListInventoryResponseDto;
import com.tarento.inventory.controller.response.ResponseDto;
import com.tarento.inventory.entity.Inventory;
import com.tarento.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ResponseDto> createInventory(@RequestBody @Valid InventoryRequestDto inventoryRequestDto) {
        Inventory inventory = inventoryService.createInventory(inventoryRequestDto);
        return ResponseEntity.ok(makeInventoryResponseDto(inventory));
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<ResponseDto> getInventory(@PathVariable("inventoryId") String inventoryId) {
        Inventory inventory = inventoryService.getInventory(inventoryId);
        return ResponseEntity.ok(makeInventoryResponseDto(inventory));
    }

    @GetMapping
    public ResponseEntity<ResponseDto> listInventory(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        if (page == null || page < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Page must be greater than zero"));
        }
        if (size == null || size < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("size must be greater than zero"));
        }
        Page<Inventory> inventoryPage = inventoryService.listInventory(page, size);
        ListInventoryResponseDto listInventoryResponseDto = new ListInventoryResponseDto(inventoryPage.getTotalPages(),
                inventoryPage.getTotalElements(), inventoryPage.stream().map(this::makeInventoryResponseDto).toList());
        return ResponseEntity.ok(listInventoryResponseDto);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<ResponseDto> updateInventory(@PathVariable("inventoryId") String inventoryId, @RequestBody @javax.validation.Valid InventoryRequestDto inventoryRequestDto) {
        Inventory inventory = inventoryService.updateInventory(inventoryId, inventoryRequestDto);
        return ResponseEntity.ok(makeInventoryResponseDto(inventory));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("inventoryId") String inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.ok().build();
    }

    private InventoryResponseDto makeInventoryResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .description(inventory.getDescription())
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

}
