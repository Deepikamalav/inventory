package com.tarento.inventory.util;

import co.elastic.clients.elasticsearch._types.FieldValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeUtil {

    public static FieldValue convertToFieldValue(Object value) {
        if (value instanceof Integer) {
            return FieldValue.of((Integer) value);
        } else if (value instanceof Long) {
            return FieldValue.of((Long) value);
        } else if (value instanceof Double) {
            return FieldValue.of((Double) value);
        } else if (value instanceof Boolean) {
            return FieldValue.of((Boolean) value);
        } else if (value instanceof Date) {
            String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .format((Date) value);
            return FieldValue.of(isoDate);
        } else {
            return FieldValue.of(value.toString());
        }
    }
}
