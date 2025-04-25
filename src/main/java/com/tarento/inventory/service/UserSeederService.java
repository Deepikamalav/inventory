package com.tarento.inventory.service;

import com.tarento.inventory.entity.ElasticSearchUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserSeederService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public void insertUsers() {
        List<ElasticSearchUser> users = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            users.add(ElasticSearchUser.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Deepika" + i)
                    .age(ThreadLocalRandom.current().nextInt(18, 65))
                    .email("Deepika" + i + "@gmail.com")
                    .isAdmin(ThreadLocalRandom.current().nextBoolean())
                    .dob(new Date())
                    .salary(ThreadLocalRandom.current().nextDouble(30000, 120000))
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .isDeleted(false)
                    .build());
        }
        elasticsearchOperations.save(users);
        System.out.println("Inserted 5000 documents!");
    }
}
