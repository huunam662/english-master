package com.example.englishmaster_be.common.constant;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PartEnum {

    PART_1("PART 1", "Picture Description"),
    PART_2("PART 2", "Question - template"),
    PART_3("PART 3", "Short Conversations"),
    PART_4("PART 4", "Short Talks"),
    PART_5("PART 5", "Incomplete Sentences"),
    PART_6("PART 6", "Text Completion"),
    PART_7("PART 7", "Reading Comprehension");

    String name;
    String description;

    PartEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}