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
@Document("users")
@Getter
@Setter
@Builder
public class MongoUser {
    @Id
    private String id;
    private String name;
    private int age;
    private String email;
    private Boolean isAdmin;
    private Date dob;
    private Double salary;
    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private boolean isDeleted;
}
