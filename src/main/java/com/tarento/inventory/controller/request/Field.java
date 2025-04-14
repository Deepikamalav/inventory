package com.tarento.inventory.controller.request;

public enum Field {
    Name(DataType.String), Age(DataType.Integer), Email(DataType.String);

    private DataType dataType;

    Field(DataType dataType) {
        this.dataType = dataType;
    }
}
