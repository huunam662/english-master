package com.example.englishmaster_be.domain.evaluator_writing.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WritingPart {
    UUID partId;
    String partName;
    String questionContent;
    String questionType;
    String essayFeedback;

    public WritingPart() {}

    // Constructor for JPQL query
    public WritingPart(UUID partId, String partName, String questionContent, String questionType, String essayFeedback) {
        this.partId = partId;
        this.partName = partName;
        this.questionContent = questionContent;
        this.questionType = questionType;
        this.essayFeedback = essayFeedback;
    }
}
