package com.example.englishmaster_be.domain.mock_test.mock_test.dto.view;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IMockTestToUserView {
    UUID getMockTestId();
    UUID getTopicId();
    String getTestName();
    int getTotalScore();
    String getTestType();
    LocalDateTime getExamStartTime();
}
