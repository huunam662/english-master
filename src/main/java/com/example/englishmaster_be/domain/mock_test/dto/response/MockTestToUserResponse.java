package com.example.englishmaster_be.domain.mock_test.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class MockTestToUserResponse {
    private UUID mockTestId;
    private UUID topicId;
    private String testName;
    private String testType;
    private int totalScore;
    // Thời điểm làm bài thi
    private LocalDateTime examStartTime;

    public void setMockTestId(UUID mockTestId) {
        this.mockTestId = mockTestId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setExamStartTime(LocalDateTime examStartTime) {
        this.examStartTime = examStartTime;
    }

    public UUID getMockTestId() {
        return mockTestId;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public String getTestName() {
        return testName;
    }

    public String getTestType() {
        return testType;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public LocalDateTime getExamStartTime() {
        return examStartTime;
    }
}