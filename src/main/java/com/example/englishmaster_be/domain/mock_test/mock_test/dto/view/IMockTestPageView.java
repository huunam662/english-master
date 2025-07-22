package com.example.englishmaster_be.domain.mock_test.mock_test.dto.view;

import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IMockTestPageView {

    MockTestEntity getMockTest();
    Long getCountQuestionReadingOrListening();
    Long getCountQuestionSpeaking();
    Long getCountQuestionEssay();

    @Data
    @NoArgsConstructor
    class MockTestPageView implements IMockTestPageView{
        private MockTestEntity mockTest;
        private Long countQuestionReadingOrListening;
        private Long countQuestionSpeaking;
        private Long countQuestionEssay;
    }

}









