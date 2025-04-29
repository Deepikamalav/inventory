package com.tarento.inventory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tarento.inventory.controller.request.*;
import com.tarento.inventory.controller.response.PaginatedResponse;
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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


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

    private Object getFieldValue(ElasticSearchUser user, String fieldName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(ElasticSearchUser.class);
            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                if (propertyDesc.getName().equalsIgnoreCase(fieldName)) {
                    Method getter = propertyDesc.getReadMethod();
                    if (getter != null) {
                        return getter.invoke(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SearchFilter convertToSearchFilter(SimplifiedSearchFilter simplified) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        for (Map.Entry<String, String> entry : simplified.getFilters().entrySet()) {
            String field = entry.getKey();
            String opAndValue = entry.getValue();

            boolean matched = false;
            for (SearchOperation op : SearchOperation.values()) {
                if (opAndValue.startsWith(op.name())) {
                    String rawValue = opAndValue.substring(op.name().length());
                    Object value = parseValue(rawValue);
                    criteriaList.add(new SearchCriteria(field, op, value));
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                Object value = parseValue(opAndValue);
                SearchOperation defaultOp = (value instanceof String) ? SearchOperation.PREFIX : SearchOperation.EQ;
                criteriaList.add(new SearchCriteria(field, defaultOp, value));
            }
        }

        SearchFilter filter = new SearchFilter();
        filter.setFilters(criteriaList);
        return filter;
    }


    private void validateCriteria(SearchCriteria criteria) {
        if (criteria.getOperation() == null) {
            throw new InvalidSearchCriteriaException("Search operation must not be null.");
        }

        if (criteria.getField() == null || criteria.getField().isBlank()) {
            throw new InvalidSearchCriteriaException("Search field must not be empty.");
        }

        if (criteria.getValue() == null) {
            throw new InvalidSearchCriteriaException("Search value must not be null.");
        }
    }


    private Object parseValue(String str) {
        try {

            if (str.matches("^\\d+$")) {
                return Integer.parseInt(str);
            }


            if (str.matches("^\\d+\\.\\d+$")) {
                return Double.parseDouble(str);
            }


            if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) {
                return Boolean.parseBoolean(str);
            }


            if (str.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")) {
                LocalDateTime dateTime = LocalDateTime.parse(str);
                return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }


    public PaginatedResponse searchUsers(SimplifiedSearchFilter simplifiedFilter, int page, int size) throws IOException {
        SearchFilter filter = convertToSearchFilter(simplifiedFilter);
        List<Map<String, Object>> users = new ArrayList<>();

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
                .from(page * size)
                .size(size)
                .build();

        System.out.println(searchRequest + "searchRequest");

        SearchResponse<ElasticSearchUser> response = elasticsearchClient.search(searchRequest, ElasticSearchUser.class);

        System.out.println(response + "response");

        long totalElements = response.hits().total().value();
        for (Hit<ElasticSearchUser> hit : response.hits().hits()) {
            ElasticSearchUser user = hit.source();
            if (user != null) {
                user.setId(hit.id());
                Map<String, Object> userMap = new HashMap<>();

                for (String field : simplifiedFilter.getFields()) {
                    Object value = getFieldValue(user, field);
                    userMap.put(field, value);
                }

                users.add(userMap);
            }
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PaginatedResponse(totalElements, totalPages, users);
    }


    public Map<String, Object> performAggregations(
            String field,
            List<String> aggTypes,
            String interval
    ) throws IOException {

        Map<String, Aggregation> aggs = new HashMap<>();

        for (String type : aggTypes) {
            switch (type.toLowerCase()) {
                case "avg":
                    aggs.put("avg", Aggregation.of(a -> a.avg(v -> v.field(field))));
                    break;
                case "min":
                    aggs.put("min", Aggregation.of(a -> a.min(v -> v.field(field))));
                    break;
                case "max":
                    aggs.put("max", Aggregation.of(a -> a.max(v -> v.field(field))));
                    break;
                case "sum":
                    aggs.put("sum", Aggregation.of(a -> a.sum(v -> v.field(field))));
                    break;
                case "date_histogram":
                    CalendarInterval calendarInterval = resolveCalendarInterval(interval);
                    String format = resolveDateFormat(interval);
                    aggs.put("date_histogram", Aggregation.of(a -> a
                            .dateHistogram(dh -> dh
                                    .field(field)
                                    .calendarInterval(calendarInterval)
                                    .format(format)
                                    .minDocCount(0)
                            )
                    ));
                    break;
            }
            System.out.println(aggs + "aggs");
        }

        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("users")
                        .size(0)
                        .aggregations(aggs),
                Void.class
        );

        Map<String, Object> result = new LinkedHashMap<>();

        for (String key : aggTypes) {
            Aggregate agg = response.aggregations().get(key);
            switch (key) {
                case "avg":
                    result.put("avg", agg.avg().value());
                    break;
                case "min":
                    result.put("min", agg.min().value());
                    break;
                case "max":
                    result.put("max", agg.max().value());
                    break;
                case "sum":
                    result.put("sum", agg.sum().value());
                    break;
                case "date_histogram":
                    Map<String, Long> buckets = new LinkedHashMap<>();
                    agg.dateHistogram().buckets().array().forEach(bucket -> {
                        long timestamp = bucket.key();
                        Instant instant = Instant.ofEpochMilli(timestamp);
                        String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                .withZone(ZoneOffset.UTC)
                                .format(instant);
                        buckets.put(formattedDate, bucket.docCount());
                    });
                    result.put("date_histogram", buckets);
                    break;
            }
        }
        System.out.println(result);
        return result;
    }


    private CalendarInterval resolveCalendarInterval(String interval) {
        if (interval == null) return CalendarInterval.Month;

        switch (interval.toLowerCase()) {
            case "day":
                return CalendarInterval.Day;
            case "week":
                return CalendarInterval.Week;
            case "month":
                return CalendarInterval.Month;
            case "quarter":
                return CalendarInterval.Quarter;
            case "year":
                return CalendarInterval.Year;
            default:
                throw new IllegalArgumentException("Invalid calendar interval: " + interval);
        }
    }

    private String resolveDateFormat(String interval) {
        if (interval == null) return "yyyy-MM";

        switch (interval.toLowerCase()) {
            case "day":
                return "yyyy-MM-dd";
            case "week":
            case "month":
                return "yyyy-MM";
            case "quarter":
            case "year":
                return "yyyy";
            default:
                return "yyyy-MM";
        }
    }


}
