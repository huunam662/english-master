package com.example.englishmaster_be.Constant;

public enum Order {
    ASC("ASC"),
    DESC("DESC");

    private final String value;

    Order(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Order fromValue(String value) {
        for (Order order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid Order Value: " + value);
    }
}