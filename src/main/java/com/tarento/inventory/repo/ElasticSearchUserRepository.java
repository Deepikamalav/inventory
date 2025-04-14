package com.tarento.inventory.repo;

import com.tarento.inventory.entity.ElasticSearchUser;
import com.tarento.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElasticSearchUserRepository extends ElasticsearchRepository<ElasticSearchUser, String> {
    Optional<ElasticSearchUser> findByIdAndIsDeleted(String id, Boolean isDeleted);
    Page<ElasticSearchUser> findAllByIsDeleted(Boolean isDeleted, Pageable pageable);
}
