package com.example.englishmaster_be.domain.mock_test.dto.response;

import com.example.englishmaster_be.domain.mock_test_result.dto.response.MockTestResultResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestResponse {

    UUID mockTestId;

    Integer totalScoreParts;

    Integer totalScoreFinish;

    Integer totalQuestionsWork;

    Integer totalQuestionsChoose;

    Integer totalQuestionsSkip;

    Integer totalAnswersCorrect;

    Integer totalAnswersWrong;

    Float answersCorrectPercent;

    LocalTime workTime;

    LocalTime finishTime;

    MockTestTopicResponse topic;

    List<MockTestResultResponse> mockTestResults;

}
