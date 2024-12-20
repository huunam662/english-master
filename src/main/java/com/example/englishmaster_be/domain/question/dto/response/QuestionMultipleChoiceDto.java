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
public class QuestionMultipleChoiceDto {
    UUID questionId;
    String question;

    int numberOfChoices;

    List<String> answers;
}
