package com.tarento.inventory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tarento.inventory.controller.request.SearchCriteria;
import com.tarento.inventory.controller.request.SearchFilter;
import com.tarento.inventory.controller.request.UserRequestDto;
import com.tarento.inventory.entity.ElasticSearchUser;
import com.tarento.inventory.entity.MongoUser;
import com.tarento.inventory.exception.InvalidSearchCriteriaException;
import com.tarento.inventory.exception.InventoryNotFoundException;
import com.tarento.inventory.exception.UserNotFoundException;
import com.tarento.inventory.repo.ElasticSearchUserRepository;
import com.tarento.inventory.repo.MongoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private ElasticSearchUserRepository userRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    public ElasticSearchUser createUser(UserRequestDto userRequestDto) {
        MongoUser mongoUser = MongoUser.builder()
                .name(userRequestDto.getName())
                .age(userRequestDto.getAge())
                .email(userRequestDto.getEmail())
                .salary(userRequestDto.getSalary())
                .dob(userRequestDto.getDob())
                .isAdmin(userRequestDto.getIsAdmin())
                .build();

        MongoUser savedMongoUser = mongoUserRepository.save(mongoUser);

        ElasticSearchUser user = ElasticSearchUser.builder()
                .id(savedMongoUser.getId())
                .name(savedMongoUser.getName())
                .age(savedMongoUser.getAge())
                .email(savedMongoUser.getEmail())
                .salary(savedMongoUser.getSalary())
                .isAdmin(savedMongoUser.getIsAdmin())
                .dob(savedMongoUser.getDob())
                .createdAt(savedMongoUser.getCreatedAt())
                .updatedAt(savedMongoUser.getUpdatedAt())
                .build();

        userRepository.save(user);

        return user;
    }

    public Page<ElasticSearchUser> listUser(Integer page, Integer size) {
        return userRepository.findAllByIsDeleted(false, PageRequest.of(page - 1, size));
    }

    public Optional<MongoUser> getUserById(String id) {
        return mongoUserRepository.findById(id);
    }


    public void deleteUser(String userId) {
        ElasticSearchUser esUser = userRepository.findByIdAndIsDeleted(userId, false).orElseThrow(() -> new UserNotFoundException("User not found by id " + userId));
        esUser.setDeleted(true);
        userRepository.save(esUser);
    }

    public ElasticSearchUser updateUser(String userId, UserRequestDto userRequestDto) {
        MongoUser existingMongoUser = mongoUserRepository.findByIdAndIsDeleted(userId, false)
                .orElseThrow(() -> new InventoryNotFoundException("Mongo user not found by id " + userId));


        existingMongoUser.setName(userRequestDto.getName());
        existingMongoUser.setAge(userRequestDto.getAge());
        existingMongoUser.setEmail(userRequestDto.getEmail());
        existingMongoUser.setAge(userRequestDto.getAge());
        existingMongoUser.setDob(userRequestDto.getDob());
        existingMongoUser.setSalary(userRequestDto.getSalary());

        MongoUser updatedMongoUser = mongoUserRepository.save(existingMongoUser);

        ElasticSearchUser updatedUser = ElasticSearchUser.builder()
                .id(updatedMongoUser.getId())
                .name(updatedMongoUser.getName())
                .age(updatedMongoUser.getAge())
                .email(updatedMongoUser.getEmail())
                .age(updatedMongoUser.getAge())
                .salary(updatedMongoUser.getSalary())
                .dob(updatedMongoUser.getDob())
                .createdAt(updatedMongoUser.getCreatedAt())
                .updatedAt(updatedMongoUser.getUpdatedAt())
                .build();

        return userRepository.save(updatedUser);
    }


    public List<ElasticSearchUser> searchUsers(SearchFilter filter) {
        List<ElasticSearchUser> users = new ArrayList<>();

        try {
            Query query = Query.of(q -> q.bool(b -> {
                List<Query> queries = new ArrayList<>();
                for (SearchCriteria criteria : filter.getFilters()) {
                    validateCriteria(criteria);
                    Query subQuery = criteria.getOperation().getQuery(criteria.getField(), criteria.getValue());
                    queries.add(subQuery);
                }
                b.must(queries);
                return b;
            }));

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("users")
                    .query(query)
                    .build();

            System.out.println("deepika malav query");
            System.out.println(searchRequest);

            SearchResponse<ElasticSearchUser> response =
                    elasticsearchClient.search(searchRequest, ElasticSearchUser.class);

            for (Hit<ElasticSearchUser> hit : response.hits().hits()) {
                ElasticSearchUser user = hit.source();
                if (user != null) {
                    user.setId(hit.id());
                    users.add(user);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }




    private void validateCriteria(SearchCriteria criteria) {
        if (criteria.getOperation() == null ) {
            throw new InvalidSearchCriteriaException("Search operation must not be null.");
        }

        if (criteria.getField() == null || criteria.getField().isBlank()) {
            throw new InvalidSearchCriteriaException("Search field must not be empty.");
        }

        if (criteria.getValue() == null) {
            throw new InvalidSearchCriteriaException("Search value must not be null.");
        }
    }

}
