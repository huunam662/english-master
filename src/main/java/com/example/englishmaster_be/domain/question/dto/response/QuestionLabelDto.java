package com.example.englishmaster_be.domain.question.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionLabelDto {
    UUID questionId;
    String question;

    boolean hasHints;

    String image;

    List<String> labels;


}
