package com.tarento.inventory.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class SearchFilter {
    private List<SearchCriteria> filters;
}
