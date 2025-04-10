package com.tarento.inventory.repo;

import com.tarento.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepo extends MongoRepository<Inventory, String> {

    Optional<Inventory> findByIdAndIsDeleted(String id, Boolean isDeleted);

    Page<Inventory> findAllByIsDeleted(Boolean isDeleted, Pageable pageable);
}
