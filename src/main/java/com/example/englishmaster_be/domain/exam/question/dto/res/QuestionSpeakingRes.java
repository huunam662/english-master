package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class QuestionSpeakingRes {

    private UUID questionId;

    private UUID partId;

    private String questionContent;

    private String contentImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private QuestionType questionType;

}
