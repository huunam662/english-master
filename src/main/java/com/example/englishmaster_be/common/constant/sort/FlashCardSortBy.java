package com.example.englishmaster_be.common.constant.sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FlashCardSortBy {

    UPDATE_AT("latest", "updateAt"),
    TITLE("title", "flashCardTitle"),
    DESCRIPTION("description", "flashCardDescription"),;

    String value;
    String field;

    FlashCardSortBy(String value, String field) {
        this.value = value;
        this.field = field;
    }

    public static final FlashCardSortBy DEFAULT = UPDATE_AT;

    public static FlashCardSortBy fromValue(String value) {

        if(value == null) return DEFAULT;
        return Arrays.stream(values()).filter(
                flashCard -> flashCard.value.equalsIgnoreCase(value)
        ).findFirst().orElse(DEFAULT);
    }

    @Override
    public String toString() {
        return value;
    }
}
