package com.example.englishmaster_be.domain.evaluator_writing.service;

import com.example.englishmaster_be.domain.evaluator_writing.dto.EvaluatorWritingRequest;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingFeedbackResponse;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingPart;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingPartProjection;
import com.example.englishmaster_be.domain.evaluator_writing.factory.WritingTaskPromptFactory;
import com.example.englishmaster_be.domain.evaluator_writing.prompt.WritingTaskPrompt;
import com.example.englishmaster_be.domain.user.service.UserService;
import com.example.englishmaster_be.domain.evaluator_writing.repository.jpa.EssaySubmissionRepository;
import com.example.englishmaster_be.domain.mock_test.repository.jdbc.MockTestJdbcRepository;
import com.example.englishmaster_be.shared.util.gemini.GeminiClient;
import com.example.englishmaster_be.shared.util.gemini.GenerationConfig;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluatorWritingService implements IEvaluatorWritingService {

    private final WritingTaskPromptFactory promptFactory;
    private final EssaySubmissionRepository essaySubmissionRepository;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(EvaluatorWritingService.class);
    private final MockTestJdbcRepository mockTestJdbcRepository;

    public EvaluatorWritingService(WritingTaskPromptFactory promptFactory, EssaySubmissionRepository essaySubmissionRepository, UserService userService, MockTestJdbcRepository mockTestJdbcRepository) {
        this.promptFactory = promptFactory;
        this.essaySubmissionRepository = essaySubmissionRepository;
        this.userService = userService;
        this.mockTestJdbcRepository = mockTestJdbcRepository;
    }

    @Override
    @Transactional
    public WritingFeedbackResponse evaluateEssay(EvaluatorWritingRequest evaluatorWritingRequest) {
        WritingTaskPrompt systemPrompt = this.promptFactory.createPrompt(
                evaluatorWritingRequest.getQuestionType());
        String userPrompt = "Đây là đề bài" + evaluatorWritingRequest.getEssayQuestion()
                + "Đây là bài làm của tôi: " + evaluatorWritingRequest.getUserEssay()
                + ".Hãy chấm cho tui theo format IELTS Writing Task 2.";
        GenerationConfig config = new GenerationConfig();
        config.setTemperature(0.7);
        config.setMaxOutputTokens(1000);
        config.setTopK(5);
        config.setTopP(0.3);
        config.setResponseMimeType("text/plain");
        String essayFeedback = GeminiClient
                .prompt(userPrompt)
                .systemInstruction("user", systemPrompt.getPrompt())
                .generationConfig(config)
                .call()
                .content();
        WritingFeedbackResponse response = new WritingFeedbackResponse(essayFeedback);

        UUID userId = userService.currentUser().getUserId();
        UUID mockTestId = UUID.randomUUID();

        // Create mock test
        mockTestJdbcRepository.insertMockTest(mockTestId, userId, evaluatorWritingRequest.getTopicId(),
                0, 0f);

        // Save into table essay_submission
        essaySubmissionRepository.createEssaySubmission(
                UUID.randomUUID(), evaluatorWritingRequest.getUserEssay(),
                essayFeedback, mockTestId
        );

        return response;
    }

    @Override
    public List<WritingPart> getEssaySubmissionResult(UUID mockTestId) {
        List<WritingPartProjection> projections = essaySubmissionRepository.getEssaySubmissionResultForUser(mockTestId);
        if (projections == null) return null;

        return projections.stream()
                .map(p -> {
                    WritingPart part = new WritingPart();
                    part.setPartId(p.getPartId());
                    part.setPartName(p.getPartName());
                    part.setQuestionContent(p.getQuestionContent());
                    part.setQuestionType(p.getQuestionType());
                    part.setEssayFeedback(p.getEssayFeedback());
                    return part;
                })
                .collect(Collectors.toList());
    }
}
