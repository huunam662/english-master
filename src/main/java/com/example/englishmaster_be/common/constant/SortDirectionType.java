package com.example.englishmaster_be.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SortDirectionType {

    ASC("ASC"),
    DESC("DESC");

    String value;

    SortDirectionType(String value) {
        this.value = value;
    }

    public static SortDirectionType fromValue(String value) {
        for (SortDirectionType order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid OrderEnum value: " + value);
    }
}