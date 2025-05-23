package com.example.englishmaster_be.common.constant.sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SortByPackTypeFields {

    UPDATED_AT("latest"),
    TYPE_NAME("type_name");

    String value;

    SortByPackTypeFields(final String value) {
        this.value = value;
    }

    public static final SortByPackTypeFields DEFAULT = UPDATED_AT;

    public static SortByPackTypeFields fromValue(final String value) {

        if(value == null || value.isEmpty())
            return DEFAULT;

        return Arrays.stream(SortByPackTypeFields.values()).filter(
                v -> v.value.equalsIgnoreCase(value)
        ).findFirst()
        .orElse(DEFAULT);
    }

    @Override
    public String toString() {
        return value;
    }
}
