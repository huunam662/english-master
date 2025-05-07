package com.example.englishmaster_be.common.constant;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PartType {

    PART_1_TOEIC("PART 1", "Picture Description"),
    PART_2_TOEIC("PART 2", "Question - Response"),
    PART_3_TOEIC("PART 3", "Short Conversations"),
    PART_4_TOEIC("PART 4", "Short Talks"),
    PART_5_TOEIC("PART 5", "Incomplete Sentences"),
    PART_6_TOEIC("PART 6", "Text Completion"),
    PART_7_TOEIC("PART 7", "Reading Comprehension"),
    PART_1_IELTS("PART 1", "Everyday conversation"),
    PART_2_IELTS("PART 2", "Monologue on a social topic"),
    PART_3_IELTS("PART 3", "Academic discussion"),
    PART_4_IELTS("PART 4", "Academic lecture"),
    PART_5_IELTS("PART 5", "General academic topic"),
    PART_6_IELTS("PART 6", "More complex text"),
    PART_7_IELTS("PART 7", "Advanced academic text");


    String name;
    String type;

    PartType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static PartType fromType(String type) {

        if(type == null) return null;

        return Arrays.stream(values())
                .filter(pType -> pType.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}