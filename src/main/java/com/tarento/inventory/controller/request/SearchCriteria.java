package com.tarento.inventory.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    @NotBlank(message = "Field name must not be empty")
    private String field;
    @NotNull(message = "Search operation is required")
    private SearchOperation operation;
    @NotNull(message = "Search value is required")
    @NotEmpty(message = "search value should not be empty")
    private Object value;
}
