package com.tarento.inventory.controller.request;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.tarento.inventory.util.TypeUtil;

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