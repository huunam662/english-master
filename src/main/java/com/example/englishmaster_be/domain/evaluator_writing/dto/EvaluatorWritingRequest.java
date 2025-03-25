package com.example.englishmaster_be.domain.evaluator_writing.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EvaluatorWritingRequest {
    public String essayQuestion;
    public String userEssay;
    public UUID questionId;
    public String questionType;
    public UUID topicId;
}
