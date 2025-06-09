package com.example.englishmaster_be.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ImportExcelType {

    TOEIC("TOEIC"),
    IELTs("IELTs"),
    TOEFT("TOEFT"),
    FUNNY_TEST("Funny-test-intern");

    String type;

    ImportExcelType(final String type) {
        this.type = type;
    }

    public static final ImportExcelType DEFAULT = TOEIC;

    public static ImportExcelType fromType(final String type) {

        if(type == null || type.isEmpty()) return DEFAULT;

        return Arrays.stream(values()).filter(
                t -> t.type.equalsIgnoreCase(type)
        ).findFirst().orElse(DEFAULT);
    }

    @Override
    public String toString() {
        return type;
    }
}
