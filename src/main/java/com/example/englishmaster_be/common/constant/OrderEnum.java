package com.example.englishmaster_be.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum OrderEnum {

    ASC("ASC"),
    DESC("DESC");

    String value;

    OrderEnum(String value) {
        this.value = value;
    }

    public static OrderEnum fromValue(String value) {
        for (OrderEnum order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid OrderEnum value: " + value);
    }
}