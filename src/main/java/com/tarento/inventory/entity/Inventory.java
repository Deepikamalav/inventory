package com.tarento.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@Document("inventory")
@Getter
@Setter
@Builder
public class Inventory {

    @Id
    private String id;

    private String name;

    private String description;

    private Integer quantity;

    private Double price;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private boolean isDeleted;
}
