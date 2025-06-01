package com.example.englishmaster_be.common.constant.sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TopicSortBy {

    UPDATE_AT("updateAt", "latest"),
    TOPIC_NAME("topicName", "name"),
    TOPIC_TYPE("topicType", "type"),
    WORK_TIME("workTime", "work-time"),
    NUMBER_QUESTIONS("numberQuestion", "number-questions");

    String name;
    String code;

    TopicSortBy(String name, String code){
        this.name = name;
        this.code = code;
    }

    public static final TopicSortBy DEFAULT = UPDATE_AT;

    public static TopicSortBy fromCode(String code){

        if(code == null) return DEFAULT;

        return Arrays.stream(TopicSortBy.values())
                .filter(t -> t.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(DEFAULT);
    }

    @Override
    public String toString() {
        return code;
    }
}
