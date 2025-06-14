package com.example.englishmaster_be.domain.mock_test.dto.response;

import com.example.englishmaster_be.common.constant.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestQuestionResponse {

    UUID questionId;

    String questionTitle;

    String questionContent;

    Integer questionScore;

    String contentAudio;

    String contentImage;

    String questionResult;

    @Enumerated(EnumType.STRING)
    QuestionType questionType;

}
