package com.tarento.inventory.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@AllArgsConstructor
@Data
@Builder
public class UserResponseDto implements ResponseDto {
    String id;
    String name;
    int age;
    String email;
    Boolean isAdmin;
    Date dob;
    Double salary;
    Date createdAt;
    Date updatedAt;

}
