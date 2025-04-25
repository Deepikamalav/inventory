package com.tarento.inventory.controller.response;

import com.tarento.inventory.entity.ElasticSearchUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
    public class PaginatedResponse {
    private long totalElements;
    private int totalPages;
    private List<Map<String, Object>> users;
//    private Map<String, Object> aggregations;

}


