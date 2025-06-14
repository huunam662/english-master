package com.example.englishmaster_be.domain.mock_test.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTest1Response {

    UUID mockTestId;

    Integer totalScore;

    Integer totalQuestionsFinish;

    Integer totalQuestionsSkip;

    Integer totalAnswersCorrect;

    Integer totalAnswersWrong;

    Float answersCorrectPercent;

    LocalTime workTime;

    LocalTime finishTime;

    MockTestTopicResponse topic;

}
