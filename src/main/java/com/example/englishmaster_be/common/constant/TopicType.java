package com.example.englishmaster_be.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TopicType {

    LISTENING_READING("Reading & Listening"),
    SPEAKING("Speaking"),
    READING("Reading");

    String type;

    TopicType(final String type) {
        this.type = type;
    }

    public static final TopicType DEFAULT = LISTENING_READING;

    public static TopicType fromType(final String type) {

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
