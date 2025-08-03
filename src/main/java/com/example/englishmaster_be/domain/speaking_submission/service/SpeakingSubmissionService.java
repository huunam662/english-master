package com.example.englishmaster_be.domain.speaking_submission.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.SpeakingSendContent;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.mock_test.repository.jpa.MockTestRepository;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingQuestionAudioRequest;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingSubmitRequest;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.BotFeedbackErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.BotFeedbackResponse;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.speaking_submission.repository.jdbc.SpeakingErrorJdbcRepository;
import com.example.englishmaster_be.domain.speaking_submission.repository.jdbc.SpeakingSubmissionJdbcRepository;
import com.example.englishmaster_be.domain.speaking_submission.repository.jpa.SpeakingSubmissionRepository;
import com.example.englishmaster_be.domain.speaking_submission.util.bot.BotUtil;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.shared.util.gemini.SpringApplicationContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j(topic = "SPEAKING-SUBMISSION-SERVICE")
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SpeakingSubmissionService implements ISpeakingSubmissionService{

    SpeakingSubmissionRepository speakingSubmissionRepository;

    SpeakingSubmissionJdbcRepository speakingSubmissionJdbcRepository;

    SpeakingErrorJdbcRepository speakingErrorJdbcRepository;

    MockTestRepository mockTestRepository;

    QuestionRepository questionRepository;

    @Transactional
    @Override
    public MockTestKeyResponse speakingSubmitTest(SpeakingSubmitRequest speakingSubmitRequest) throws InterruptedException {
        Assert.notNull(speakingSubmitRequest, "Speaking contents to submit test is required.");
        Assert.notNull(speakingSubmitRequest.getMockTestId(), "Mock test id required.");
        if(speakingSubmitRequest.getContents() == null || speakingSubmitRequest.getContents().isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Speaking contents to submit test is empty.");

        UUID mockTestId = speakingSubmitRequest.getMockTestId();
        if(!mockTestRepository.isExistedMockTest(mockTestId))
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Mock test not found.");

        List<SpeakingSubmissionEntity> speakingSubmissions = new ArrayList<>();
        List<SpeakingQuestionAudioRequest> questionAudios = speakingSubmitRequest.getContents();
        List<UUID> questionIds = questionRepository.findQuestionIdsIn(questionAudios.stream().map(SpeakingQuestionAudioRequest::getQuestionId).toList());
        for(SpeakingQuestionAudioRequest questionAudioSubmit : speakingSubmitRequest.getContents()) {
            Assert.notNull(questionAudioSubmit, "Speaking content to submit test is required.");
            Assert.notNull(questionAudioSubmit.getQuestionId(), "Question id to submit test is required.");
            if(questionAudioSubmit.getAudioUrl() == null || questionAudioSubmit.getAudioUrl().isEmpty())
                continue;
            if(!questionIds.contains(questionAudioSubmit.getQuestionId()))
                continue;

            speakingSubmissions.add(
                    SpeakingSubmissionEntity.builder()
                            .id(UUID.randomUUID())
                            .audioUrl(questionAudioSubmit.getAudioUrl())
                            .mockTestId(mockTestId)
                            .questionId(questionAudioSubmit.getQuestionId())
                            .status(StatusSpeakingSubmission.Processing)
                            .build()
            );
        }

        speakingSubmissionJdbcRepository.batchInsertSpeakingSubmission(speakingSubmissions);

        CompletableFuture.runAsync(
                () -> evaluateSpeakingTest(speakingSubmissions)
        ).exceptionally((ex) -> {
            log.error(ex.getMessage());
            return null;
        }).thenRun(() -> {
            // Gửi mail
            System.out.println("--> Gửi mail");
        });

        return MockTestKeyResponse.builder()
                .mockTestId(mockTestId)
                .build();
    }

    @Transactional
    public void evaluateSpeakingTest(List<SpeakingSubmissionEntity> speakingSubmissions) {

        if(speakingSubmissions == null || speakingSubmissions.isEmpty())
            throw new ErrorHolder(Error.METHOD_NOT_ALLOWED, "Speaking submissions are required.");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(SpeakingSubmissionEntity speakingSubmission : speakingSubmissions){
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        String speakingText = BotUtil.speechToText(speakingSubmission.getAudioUrl());
                        String questionContent = questionRepository.findQuestionSpeakingContent(speakingSubmission.getQuestionId());
                        String questionContentClearHtmlTag = Jsoup.clean(questionContent, Safelist.none());
                        String sendContent = SpeakingSendContent.Content.getContent().replace(":question", questionContentClearHtmlTag)
                                .replace(":speakingText", speakingText);
                        BotFeedbackResponse feedbackTestResult = BotUtil.feedback4Speaking(sendContent);
                        if(feedbackTestResult != null){
                            speakingSubmission.setFeedback(feedbackTestResult.getFeedback());
                            float reachedPercent = BigDecimal.valueOf(feedbackTestResult.getReachedPercent() / 100)
                                            .setScale(2, RoundingMode.HALF_UP).floatValue();
                            speakingSubmission.setReachedPercent(reachedPercent);
                            speakingSubmission.setLevelSpeaker(SpeakingUtil.toLevelSpeaker(reachedPercent));
                            speakingSubmission.setSpeakingText(speakingText);
                            BotFeedbackErrorResponse feedbackError = feedbackTestResult.getErrors();
                            List<SpeakingErrorEntity> speakingErrors = SpeakingUtil.toSpeakingErrors(speakingSubmission.getId(), feedbackError);
                            speakingErrorJdbcRepository.batchInsertSpeakingError(speakingErrors);
                        }
                        return speakingSubmission;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, SpringApplicationContext.getBean("configTaskExecutor", ThreadPoolTaskExecutor.class)
            ).exceptionallyCompose((e) -> {
                log.error(e.getMessage());
                return CompletableFuture.failedFuture(e);
            }).thenAccept((speakingSubmissionApply) -> {
                speakingSubmissionApply.setStatus(StatusSpeakingSubmission.Completed);
                speakingSubmissionJdbcRepository.updateSpeakingSubmission(speakingSubmissionApply);
            });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

}
