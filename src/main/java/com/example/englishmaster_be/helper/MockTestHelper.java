package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.mock_test.dto.request.*;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestTotalCountResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailRepository;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultRepository;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.PartUtil;
import com.example.englishmaster_be.util.QuestionUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestHelper {

    IPartService partService;

    IAnswerService answerService;

    IQuestionService questionService;

    MockTestResultRepository mockTestResultRepository;

    MockTestDetailRepository mockTestDetailRepository;


    public MockTestResultEntity initMockTestEntity(
            MockTestPartRequest mockTestPartRequest,
            MockTestTotalCountResponse mockTestTotalCountResponse,
            MockTestEntity mockTestEntity,
            UserEntity userCurrent
    ){

        PartEntity partEntity = partService.getPartToId(mockTestPartRequest.getPartId());

        mockTestTotalCountResponse.setTotalScoreParts(
                mockTestTotalCountResponse.getTotalScoreParts() + PartUtil.totalScoreOfPart(partEntity)
        );

        mockTestTotalCountResponse.setTotalQuestionChildOfParts(
                mockTestTotalCountResponse.getTotalQuestionChildOfParts() + QuestionUtil.totalQuestionChildOf(partEntity.getQuestions())
        );

        MockTestResultEntity mockTestResultEntity = MockTestResultEntity.builder()
                .mockTestResultId(UUID.randomUUID())
                .mockTest(mockTestEntity)
                .userCreate(userCurrent)
                .userUpdate(userCurrent)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .part(partEntity)
                .build();

        return mockTestResultRepository.save(mockTestResultEntity);
    }

    public void addMockTestDetailEntity(
            MockTestResultEntity mockTestResultEntity,
            UserEntity userCurrent,
            MockTestQuestionChildrenRequest mockTestQuestionChildrenRequest,
            MockTestTotalCountResponse mockTestTotalCountResponse
    ){

        MockTestDetailEntity mockTestDetailEntity = MockTestDetailEntity.builder()
                .resultMockTest(mockTestResultEntity)
                .userCreate(userCurrent)
                .userUpdate(userCurrent)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        if (mockTestQuestionChildrenRequest instanceof MockTestSingleChoiceRequest mockTestSingleChoiceRequest) {

            AnswerEntity answerChooseEntity = answerService.getAnswerById(mockTestSingleChoiceRequest.getAnswerChoiceId());

            QuestionEntity questionAnswerChoose = answerChooseEntity.getQuestion();

            boolean isCorrectAnswer = answerChooseEntity.getCorrectAnswer();

            mockTestDetailEntity.setMockTestDetailId(UUID.randomUUID());
            mockTestDetailEntity.setAnswerChoice(answerChooseEntity);
            mockTestDetailEntity.setAnswerContent(answerChooseEntity.getAnswerContent());
            mockTestDetailEntity.setQuestionChild(questionAnswerChoose);
            mockTestDetailEntity.setIsCorrectAnswer(isCorrectAnswer);

            if (isCorrectAnswer) {

                mockTestTotalCountResponse.setTotalAnswersCorrect(
                        mockTestTotalCountResponse.getTotalAnswersCorrect() + 1
                );

                mockTestTotalCountResponse.setTotalScoreCorrect(
                        mockTestTotalCountResponse.getTotalScoreCorrect() + questionAnswerChoose.getQuestionScore()
                );

                mockTestDetailEntity.setAnswerCorrectContent(answerChooseEntity.getAnswerContent());
                mockTestDetailEntity.setScoreAchieved(questionAnswerChoose.getQuestionScore());
            }
            else if (questionAnswerChoose.getAnswers() != null) {

                mockTestTotalCountResponse.setTotalAnswersWrong(
                        mockTestTotalCountResponse.getTotalAnswersWrong() + 1
                );

                AnswerEntity answerCorrectEntity = questionAnswerChoose.getAnswers().stream()
                        .filter(
                                answerEntity -> answerEntity != null && answerEntity.getCorrectAnswer().equals(Boolean.TRUE)
                        ).findFirst()
                        .orElse(null);

                mockTestDetailEntity.setAnswerCorrectContent(answerCorrectEntity != null ? answerCorrectEntity.getAnswerContent() : null);
                mockTestDetailEntity.setScoreAchieved(0);
            }

            mockTestResultEntity.getMockTestDetails().add(
                    mockTestDetailRepository.save(mockTestDetailEntity)
            );

        }
        else if (mockTestQuestionChildrenRequest instanceof MockTestWordsMatchingRequest mockTestWordsMatchingRequest) {

            QuestionEntity questionChildren = questionService.getQuestionById(mockTestWordsMatchingRequest.getQuestionChildrenId());

            QuestionEntity questionMatching = questionService.getQuestionById(mockTestWordsMatchingRequest.getQuestionMatchingId());

            boolean isAnswerCorrectMatching = questionChildren.equals(questionMatching);

            mockTestDetailEntity.setMockTestDetailId(UUID.randomUUID());
            mockTestDetailEntity.setQuestionChild(questionChildren);
            mockTestDetailEntity.setAnswerContent(questionMatching.getQuestionResult());
            mockTestDetailEntity.setAnswerCorrectContent(questionChildren.getQuestionResult());
            mockTestDetailEntity.setIsCorrectAnswer(isAnswerCorrectMatching);

            if (isAnswerCorrectMatching) {

                mockTestTotalCountResponse.setTotalAnswersCorrect(
                        mockTestTotalCountResponse.getTotalAnswersCorrect() + 1
                );

                mockTestTotalCountResponse.setTotalScoreCorrect(
                        mockTestTotalCountResponse.getTotalScoreCorrect() + questionMatching.getQuestionScore()
                );

                mockTestDetailEntity.setScoreAchieved(questionMatching.getQuestionScore());
            }
            else {

                mockTestTotalCountResponse.setTotalAnswersWrong(
                        mockTestTotalCountResponse.getTotalAnswersWrong() + 1
                );

                mockTestDetailEntity.setScoreAchieved(0);
            }

            mockTestResultEntity.getMockTestDetails().add(
                    mockTestDetailRepository.save(mockTestDetailEntity)
            );
        }
        else if (mockTestQuestionChildrenRequest instanceof MockTestFillInBlankRequest mockTestFillInBlankRequest) {

            QuestionEntity questionFillInBlank = questionService.getQuestionById(mockTestFillInBlankRequest.getQuestionChildrenId());

            if (questionFillInBlank.getAnswers() == null) return;

            List<AnswerEntity> answerQuestionFillInBlankList = questionFillInBlank.getAnswers().stream().sorted(
                    Comparator.comparing(AnswerEntity::getAnswerContent)
            ).toList();

            int scoreFillTrue = questionFillInBlank.getQuestionScore() / answerQuestionFillInBlankList.size();

            String[] answersFillRequest = mockTestFillInBlankRequest.getAnswersFill();

            int answersFillRequestListSize = answersFillRequest.length;

            for (int k = 0; k < answersFillRequestListSize; k++) {

                String answerFillRequestContent = answersFillRequest[k];

                if (answerFillRequestContent == null) answerFillRequestContent = "";

                AnswerEntity answerFillEntity = answerQuestionFillInBlankList.get(k);

                String answerFillEntityContent = answerFillEntity.getAnswerContent().split(String.format("%d_", k + 1))[1];

                boolean isCorrectContentFill = answerFillEntityContent.equals(answerFillRequestContent);

                mockTestDetailEntity.setMockTestDetailId(UUID.randomUUID());
                mockTestDetailEntity.setQuestionChild(questionFillInBlank);
                mockTestDetailEntity.setAnswerContent(answerFillRequestContent);
                mockTestDetailEntity.setAnswerCorrectContent(answerFillEntityContent);
                mockTestDetailEntity.setIsCorrectAnswer(isCorrectContentFill);

                if (isCorrectContentFill) {

                    mockTestTotalCountResponse.setTotalAnswersCorrect(
                            mockTestTotalCountResponse.getTotalAnswersCorrect() + 1
                    );

                    mockTestTotalCountResponse.setTotalScoreCorrect(
                            mockTestTotalCountResponse.getTotalScoreCorrect() + scoreFillTrue
                    );

                    mockTestDetailEntity.setAnswerChoice(answerFillEntity);
                    mockTestDetailEntity.setScoreAchieved(scoreFillTrue);
                }
                else {

                    mockTestTotalCountResponse.setTotalAnswersWrong(
                            mockTestTotalCountResponse.getTotalAnswersWrong() + 1
                    );
                    mockTestDetailEntity.setAnswerChoice(null);
                    mockTestDetailEntity.setScoreAchieved(0);
                }

                mockTestResultEntity.getMockTestDetails().add(
                        mockTestDetailRepository.save(mockTestDetailEntity)
                );
            }
        }
    }

}
