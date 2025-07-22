package com.example.englishmaster_be.domain.mock_test.mock_test.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MockTestPageRes {

    private MockTestFullRes mockTest;
    private Long countQuestionReadingOrListening;
    private Long countQuestionSpeaking;
    private Long countQuestionEssay;

}
