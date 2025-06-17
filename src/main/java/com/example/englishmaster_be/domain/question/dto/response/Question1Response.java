package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question1Response {

    UUID questionId;

    UUID partId;

    String questionContent;

    String contentImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    QuestionType questionType;

}
