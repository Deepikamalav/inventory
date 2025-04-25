package com.tarento.inventory.controller.request;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.tarento.inventory.util.TypeUtil;

import java.time.Instant;

public enum SearchOperation {
    EQ {
        @Override
        public Query getQuery(String field, Object value) {
            return Query.of(q -> q.term(t -> t.field(field).value(TypeUtil.convertToFieldValue(value))));
        }
    },
    GTE {
        @Override
        public Query getQuery(String field, Object value) {
            return Query.of(q -> q.range(range -> {
                RangeQuery.Builder r = new RangeQuery.Builder()
                        .field(field);
                r.gte(JsonData.of(value));
                return r;
            }));
        }
    },
    GT {
        @Override
        public Query getQuery(String filed, Object value) {
            return Query.of(q -> q.range(range -> {
                RangeQuery.Builder r = new RangeQuery.Builder().field(filed);
                r.gt(JsonData.of(value));
                return r;
            }));
        }
    },
    LT {
        @Override
        public Query getQuery(String filed, Object value) {
            return Query.of(q -> q.range(range -> {
                RangeQuery.Builder r = new RangeQuery.Builder().field(filed);
                r.lt(JsonData.of(value));
                return r;
            }));
        }
    },
    LTE {
        @Override
        public Query getQuery(String field, Object value) {
            return Query.of(q -> q.range(range -> {
                RangeQuery.Builder r = new RangeQuery.Builder()
                        .field(field);
                r.lte(JsonData.of(value));
                return r;
            }));
        }
    },
    DATE_RANGE {
        @Override
        public Query getQuery(String field, Object value) {
            Instant dateValue = (Instant) value;
            return Query.of(q -> q.range(range -> {
                RangeQuery.Builder r = new RangeQuery.Builder()
                        .field(field);
                r.gte(JsonData.of(dateValue));
                return r;
            }));
        }
    },

    MATCH {
        @Override
        public Query getQuery(String field, Object value) {
            return Query.of(q -> q.match(t -> t.field(field).query(value.toString())));
        }
    },
    PREFIX {
        @Override
        public Query getQuery(String field, Object value) {
            return Query.of(q -> q.matchPhrasePrefix(t -> t.field(field).query(value.toString())));
        }
    };


    public abstract Query getQuery(String field, Object value);

}