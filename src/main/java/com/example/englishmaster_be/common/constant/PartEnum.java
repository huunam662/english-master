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
    PART_7("PART 7", "Reading Comprehension"),
    PART_8("PART 8", "Words Fill Completion"),
    PART_9("PART 9", "Matching Words"),
    PART_1_IELTS("PART 1", "Everyday conversation"),
    PART_2_IELTS("PART 2", "Monologue on a social topic"),
    PART_3_IELTS("PART 3", "Academic discussion"),
    PART_4_IELTS("PART 4", "Academic lecture"),
    PART_5_IELTS("PART 5", "General academic topic"),
    PART_6_IELTS("PART 6", "More complex text"),
    PART_7_IELTS("PART 7", "Advanced academic text");


    String name;
    String type;

    PartEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }
}