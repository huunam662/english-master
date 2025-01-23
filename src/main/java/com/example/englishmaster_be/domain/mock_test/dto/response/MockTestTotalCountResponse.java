package com.example.englishmaster_be.domain.mock_test.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestTotalCountResponse {

    int totalScoreParts = 0;

    int totalScoreCorrect = 0;

    int totalAnswersWrong = 0;

    int totalAnswersCorrect = 0;

    int totalQuestionChildOfParts = 0;

    int totalQuestionsChildMockTest = 0;

}
