package com.tarento.inventory.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ListInventoryResponseDto implements ResponseDto {

    private int totalPages;
    private long totalElements;
    private List<InventoryResponseDto> inventories;
}
