package com.tarento.inventory.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InventoryRequestDto {

    @NotNull(message = "Name cannot be null")
    private String name;

    private String description;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    @Positive(message = "Price must be greater than 0")
    private Double price;

}
