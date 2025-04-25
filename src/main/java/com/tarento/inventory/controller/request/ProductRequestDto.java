package com.tarento.inventory.controller.request;


import lombok.Data;

@Data
public class ProductRequestDto {
    private int id;
    private String name;
    private int quantity;
    private String category;
    private Double price;
    private long timeStamp;
    
}
