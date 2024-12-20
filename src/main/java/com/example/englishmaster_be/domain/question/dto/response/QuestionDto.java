package com.example.englishmaster_be.domain.question.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionDto {
    List<QuestionMultipleChoiceDto> questionMultipleChoices;
    List<QuestionFillInBlankDto> questionFillInBlank;
    List<QuestionMultipleChoiceDto> questionTFNotgivens;
    List<QuestionMatchingDto> questionMatchings;
    List<QuestionLabelDto> questionLabels;
}
