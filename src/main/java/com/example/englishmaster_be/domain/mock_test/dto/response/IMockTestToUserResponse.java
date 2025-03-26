package com.example.englishmaster_be.domain.mock_test.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IMockTestToUserResponse {
    UUID getMockTestId();
    UUID getTopicId();
    String getTestName();
    int getTotalScore();
    String getTestType();
    LocalDateTime getExamStartTime();
}
