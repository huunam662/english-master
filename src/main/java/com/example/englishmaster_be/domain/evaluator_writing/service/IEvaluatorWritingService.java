package com.example.englishmaster_be.domain.evaluator_writing.service;

import com.example.englishmaster_be.domain.evaluator_writing.dto.EvaluatorWritingRequest;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingFeedbackResponse;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingPart;

import java.util.List;
import java.util.UUID;

public interface IEvaluatorWritingService {
    public WritingFeedbackResponse evaluateEssay(EvaluatorWritingRequest evaluatorWritingRequest);

    public List<WritingPart> getEssaySubmissionResult(UUID mockTestId);
}
