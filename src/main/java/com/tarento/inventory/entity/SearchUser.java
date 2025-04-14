package com.tarento.inventory.entity;

import co.elastic.clients.json.JsonpDeserializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonpDeserializable
public class SearchUser {
    private String name;
    private Integer age;
    private String email;
    private Boolean isAdmin;
    private Date dob;
    private Double salary;
}
