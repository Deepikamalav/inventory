package com.tarento.inventory.controller.request;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class SimplifiedSearchFilter {
    private Map<String, String> filters;
    private List<String> fields;
}
