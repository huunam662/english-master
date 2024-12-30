package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
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
public class QuestionMultipleChoiceDto {
    UUID questionId;
    String content;

    QuestionTypeEnum type;

    int numberOfChoices;

    List<String> answers;
}
