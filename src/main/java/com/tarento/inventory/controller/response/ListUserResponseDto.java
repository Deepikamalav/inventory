package com.tarento.inventory.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ListUserResponseDto  implements ResponseDto{
    private int totalPages;
    private long totalElements;
    private List<UserResponseDto> users;
}
