package com.example.englishmaster_be.domain.mock_test.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestDetailResponse {

    UUID mockTestDetailId;

    String answerContent;

    String answerCorrectContent;

    Boolean isCorrectAnswer;

    Integer scoreAchieved;

    MockTestQuestionAnswersResponse questionChild;

    MockTestAnswerResponse answerChoice;

    MockTestQuestionResponse question;

}
