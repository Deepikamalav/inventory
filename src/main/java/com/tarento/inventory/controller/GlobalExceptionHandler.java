package com.tarento.inventory.controller;

import com.tarento.inventory.controller.response.ErrorResponseDto;
import com.tarento.inventory.exception.InventoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InventoryNotFoundException.class})
    public ResponseEntity<Object> handleInventoryNotFoundException(InventoryNotFoundException inventoryNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(inventoryNotFoundException.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        ObjectError firstError = bindingResult.getAllErrors().stream().findFirst().orElse(null);
        String errorMessage = firstError != null ? firstError.getDefaultMessage() : "Unknown validation error";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(errorMessage));
    }
}
