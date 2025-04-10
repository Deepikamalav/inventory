package com.tarento.inventory.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
@Builder
public class InventoryResponseDto implements ResponseDto {
    String id;
    String name;
    String description;
    Integer quantity;
    Double price;
    Date createdAt;
    Date updatedAt;
}
