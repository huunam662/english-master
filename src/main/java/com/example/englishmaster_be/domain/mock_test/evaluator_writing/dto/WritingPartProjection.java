package com.example.englishmaster_be.domain.mock_test.evaluator_writing.dto;

import java.util.UUID;

public interface WritingPartProjection {
    UUID getPartId();
    String getPartName();
    String getQuestionContent();
    String getQuestionType();
    String getEssayFeedback();
}