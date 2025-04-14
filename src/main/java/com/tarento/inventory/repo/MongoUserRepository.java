package com.tarento.inventory.repo;

import com.tarento.inventory.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<MongoUser,String > {
    Optional<MongoUser> findByIdAndIsDeleted(String id, Boolean isDeleted);
}
