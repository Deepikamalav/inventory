package com.tarento.inventory.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponseDto implements ResponseDto {

    private String message;
}
