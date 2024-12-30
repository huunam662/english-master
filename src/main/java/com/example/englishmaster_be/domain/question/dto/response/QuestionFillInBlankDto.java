package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionFillInBlankDto {
    UUID questionId;

    String content;

    int countBlank;

    QuestionTypeEnum type;
}
