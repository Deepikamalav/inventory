package com.tarento.inventory.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestDto {
    private String name;
    private int age;
    private String email;
    private Boolean isAdmin;
    private Date dob;
    private Double salary;
}
