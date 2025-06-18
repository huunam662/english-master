package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestInforResponse;
import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test.repository.jdbc.MockTestJdbcRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jdbc.MockTestDetailJdbcRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jpa.MockTestDetailRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jdbc.MockTestResultJdbcRepository;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestResultEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.mock_test.dto.request.*;
import com.example.englishmaster_be.domain.mock_test.dto.response.IMockTestToUserResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.speaking_submission.repository.jpa.SpeakingErrorRepository;
import com.example.englishmaster_be.domain.speaking_submission.repository.jpa.SpeakingSubmissionRepository;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test.repository.jpa.MockTestRepository;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.shared.service.mailer.MailerService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.common.constant.error.Error;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j(topic = "MOCK-TEST-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestService implements IMockTestService {

    MockTestRepository mockTestRepository;

    IUserService userService;

    ITopicService topicService;

    IAnswerService answerService;

    AnswerRepository answerRepository;

    IQuestionService questionService;

    MockTestDetailRepository mockTestDetailRepository;

    MockTestResultJdbcRepository mockTestResultJdbcRepository;

    MockTestDetailJdbcRepository mockTestDetailJdbcRepository;

    MockTestJdbcRepository mockTestJdbcRepository;

    SpeakingSubmissionRepository speakingSubmissionRepository;

    SpeakingErrorRepository speakingErrorRepository;

    MailerService mailerService;


    @Override
    public MockTestEntity getMockTestById(UUID mockTestId) {

        return mockTestRepository.findMockTestById(mockTestId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.MOCK_TEST_NOT_FOUND)
                );
    }

    @Transactional
    @Override
    public MockTestKeyResponse saveMockTest(MockTestRequest mockTestRequest) {
        // Kiểm tra dữ liệu đầu vào
        if (mockTestRequest == null) {
            throw new ErrorHolder(Error.BAD_REQUEST, "Mock test request is null");
        }
        if (mockTestRequest.getTopicId() == null) {
            throw new ErrorHolder(Error.BAD_REQUEST, "Topic id in mock test request is null");
        }
        if (mockTestRequest.getWorkTimeTopic() == null || mockTestRequest.getWorkTimeTopic().isBlank()) {
            throw new ErrorHolder(Error.BAD_REQUEST, "Work time topic is null or empty");
        }
        if (mockTestRequest.getWorkTimeFinal() == null || mockTestRequest.getWorkTimeFinal().isBlank()) {
            throw new ErrorHolder(Error.BAD_REQUEST, "Work time final is null or empty");
        }

        // Lấy user hiện hành và topic của bài thi
        UserEntity currentUser = userService.currentUser();
        TopicEntity topicEntity = topicService.getTopicById(mockTestRequest.getTopicId());

        // Chuyển đổi chuỗi thời gian sang LocalTime theo định dạng "HH:mm:ss"
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime workTimeTopic;
        LocalTime workTimeFinal;
        try {
            workTimeTopic = LocalTime.parse(mockTestRequest.getWorkTimeTopic(), timeFormatter);
            workTimeFinal = LocalTime.parse(mockTestRequest.getWorkTimeFinal(), timeFormatter);
        } catch (DateTimeParseException e) {
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid time format. Expected HH:mm:ss");
        }

        // Tính khoảng thời gian giữa workTimeFinal và workTimeTopic
        // Lưu ý: Nếu workTimeFinal lớn hơn workTimeTopic thì Duration sẽ âm. Bạn có thể điều chỉnh theo nghiệp vụ.
        Duration duration = Duration.between(workTimeFinal, workTimeTopic);
        LocalTime finishTime = LocalTime.MIN.plus(duration);

        // Khởi tạo MockTestEntity (createAt, updateAt được tự động cập nhật bởi Hibernate)
        UUID mockTestId = mockTestJdbcRepository.insertMockTest(workTimeTopic, finishTime, currentUser.getUserId(), topicEntity.getTopicId());

        // Lưu và trả về key đối tượng MockTestEntity vừa tạo
        return MockTestKeyResponse.builder()
                .mockTestId(mockTestId)
                .build();
    }




    @Override
    public MockTestEntity findMockTestToId(UUID mockTestId) {

        return mockTestRepository.findByMockTestId(mockTestId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.MOCK_TEST_NOT_FOUND)
                );
    }




    @Override
    public List<MockTestEntity> getAllMockTestByYearMonthAndDay(TopicEntity topic, String year, String month, String day) {
        if (day == null && month != null) {
            return mockTestRepository.findAllByYearMonth(year, month, topic);
        }
        if (month == null) {
            return mockTestRepository.findAllByYear(year, topic);
        }
        return mockTestRepository.findAllByYearMonthAndDay(year, month, day, topic);
    }

    @Override
    public List<MockTestEntity> getAllMockTestToTopic(TopicEntity topic) {
        return mockTestRepository.findAllByTopic(topic);
    }

    @Override
    public List<IMockTestToUserResponse> getListMockTestToUser() {
        UserEntity user = userService.currentUser();
        List<IMockTestToUserResponse> projections = mockTestRepository.findExamResultForUser(user.getUserId());
        return projections;
    }

    @Transactional
    @Override
    public MockTestKeyResponse addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId) {

        Assert.notNull(mockTestId, "Mock test id is required.");
        Assert.notNull(listAnswerId, "Answers to submit is required.");

        UserEntity userSubmitAnswers = userService.currentUser();
        MockTestEntity mockTest = getMockTestById(mockTestId);
        UserEntity userOfMockTest = mockTest.getUser();
        TopicEntity topicOfMockTest = mockTest.getTopic();

        if(topicOfMockTest == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Unknown topic of mock test");

        if(userOfMockTest == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Unknown user of mock test");

        if(!userSubmitAnswers.getUserId().equals(userOfMockTest.getUserId()))
            throw new ErrorHolder(Error.UNAUTHORIZED, "User current mustn't owner of mock test");

        INumberAndScoreQuestionTopic numberAndScoreQuestionTopic = questionService.getNumberAndScoreQuestionTopic(topicOfMockTest.getTopicId());

        List<AnswerEntity> answersSubmit = answerRepository.findAnswersInAnswerIds(topicOfMockTest.getTopicId(), listAnswerId);

        int totalQuestionsFinish = listAnswerId.size();
        int totalQuestionsSkip = numberAndScoreQuestionTopic.getNumberQuestions() - totalQuestionsFinish;
        int totalAnswersCorrect = 0;
        int totalAnswersWrong = 0;
        int totalScoreMockTest = 0;

        List<MockTestResultEntity> mockTestResults = new ArrayList<>();
        List<MockTestDetailEntity> mockTestDetails = new ArrayList<>();

        Map<QuestionEntity, AnswerEntity> questionChildAnswerGroup = answersSubmit.stream()
                .collect(Collectors.toMap(
                        AnswerEntity::getQuestion,
                        answer -> answer
                ));

        Map<PartEntity, List<QuestionEntity>> partQuestionChildsGroup = questionChildAnswerGroup.keySet().stream()
                .collect(Collectors.groupingBy(QuestionEntity::getPart));

        Set<PartEntity> partsSubmit = partQuestionChildsGroup.keySet();
        for(PartEntity part: partsSubmit) {

            int totalCorrectOfPart = 0;
            int totalScoreOfPart = 0;

            MockTestResultEntity mockTestResult = MockTestResultEntity.builder()
                    .mockTestResultId(UUID.randomUUID())
                    .part(part)
                    .mockTest(mockTest)
                    .userCreate(userSubmitAnswers)
                    .userUpdate(userSubmitAnswers)
                    .build();

            List<QuestionEntity> questionChildsOfPart = partQuestionChildsGroup.getOrDefault(part, Collections.emptyList());
            for(QuestionEntity questionChild : questionChildsOfPart) {
                AnswerEntity answerChoice = questionChildAnswerGroup.getOrDefault(questionChild, null);

                if(answerChoice == null) continue;

                boolean isCorrectAnswer = answerChoice.getCorrectAnswer();
                int scoreAchieved = questionChild.getQuestionScore();
                if(isCorrectAnswer){
                    totalCorrectOfPart++;
                    totalScoreOfPart+=scoreAchieved;
                }
                else {
                    totalAnswersWrong++;
                }
                mockTestDetails.add(
                        MockTestDetailEntity.builder()
                                .mockTestDetailId(UUID.randomUUID())
                                .resultMockTest(mockTestResult)
                                .questionChild(questionChild)
                                .answerChoice(answerChoice)
                                .userCreate(userSubmitAnswers)
                                .userUpdate(userSubmitAnswers)
                                .answerContent(answerChoice.getAnswerContent())
                                .isCorrectAnswer(isCorrectAnswer)
                                .scoreAchieved(isCorrectAnswer ? scoreAchieved : 0)
                                .build()
                );
            }

            mockTestResult.setTotalCorrect(totalCorrectOfPart);
            mockTestResult.setTotalScoreResult(totalScoreOfPart);

            mockTestResults.add(mockTestResult);

            totalAnswersCorrect += totalCorrectOfPart;
            totalScoreMockTest += totalScoreOfPart;
        }

        float answersCorrectPercent = BigDecimal.valueOf((float) totalAnswersCorrect / numberAndScoreQuestionTopic.getNumberQuestions())
                        .setScale(2, RoundingMode.HALF_UP).floatValue();

        mockTestRepository.updateMockTest(
                mockTestId, answersCorrectPercent, totalAnswersCorrect, totalAnswersWrong,
                totalQuestionsFinish, totalQuestionsSkip, totalScoreMockTest
        );
        mockTestResultJdbcRepository.batchInsertMockTestResult(mockTestResults);
        mockTestDetailJdbcRepository.batchInsertMockTestDetail(mockTestDetails);

        sendEmailToMock(mockTestId);

        return MockTestKeyResponse.builder()
                .mockTestId(mockTestId)
                .build();
    }


    @Override
    public void sendEmailToMock(UUID mockTestId) {

        CompletableFuture.runAsync(() -> {
            try {
                mailerService.sendResultMockTestEmail(mockTestId);
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally((e) -> {
            log.error(e.getMessage());
            return null;
        });
    }


    @Override
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId) {

        UserEntity currentUser = userService.currentUser();

        MockTestEntity mockTest = findMockTestToId(mockTestId);

        if (!currentUser.equals(mockTest.getUser()))
            throw new ErrorHolder(Error.BAD_REQUEST, "You cannot view other people's tests");

        List<QuestionEntity> questionList = topicService.getQuestionOfPartToTopic(mockTest.getTopic().getTopicId(), partId);

        List<QuestionMockTestResponse> questionMockTestResponseList = new ArrayList<>();

        for (QuestionEntity question : questionList) {

            if (questionService.checkQuestionGroup(question.getQuestionId())) {
                QuestionMockTestResponse questionMockTestResponse = new QuestionMockTestResponse(question);

                List<QuestionEntity> questionGroupList = questionService.listQuestionGroup(question);
                List<QuestionMockTestResponse> questionGroupResponseList = new ArrayList<>();
                for (QuestionEntity questionGroup : questionGroupList) {
                    AnswerEntity answerCorrect = answerService.correctAnswer(questionGroup);
                    AnswerEntity answerChoice = answerService.choiceAnswer(questionGroup, mockTest);
                    QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(questionGroup, answerChoice, answerCorrect);
                    questionGroupResponseList.add(questionGroupResponse);
                    questionMockTestResponse.setQuestionGroup(questionGroupResponseList);
                }

                questionMockTestResponseList.add(questionMockTestResponse);
            } else {
                AnswerEntity answerCorrect = answerService.correctAnswer(question);
                AnswerEntity answerChoice = answerService.choiceAnswer(question, mockTest);
                QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(question, answerChoice, answerCorrect);
                questionMockTestResponseList.add(questionGroupResponse);
            }
        }

        return questionMockTestResponseList;
    }


    @Override
    public MockTestInforResponse getInformationMockTest(UUID mockTestId){

        Assert.notNull(mockTestId, "Mock test id is required.");

        UserEntity currentUser = userService.currentUser();

        MockTestEntity mockTest = mockTestRepository.findMockTestJoinUserTopicTopicType(mockTestId)
                .orElseThrow(() -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Mock test not found."));

        UserEntity ownerOfMockTest = mockTest.getUser();

        if(ownerOfMockTest == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Unknown owner of mock test.");

        if(!currentUser.getUserId().equals(ownerOfMockTest.getUserId()))
            throw new ErrorHolder(Error.UNAUTHORIZED, "User current mustn't owner of mock test");

        TopicEntity topicMockTest = mockTest.getTopic();

        TopicTypeEntity topicType = topicMockTest.getTopicType();

        if(topicType.getTopicTypeName().equalsIgnoreCase("speaking")){
            List<SpeakingSubmissionEntity> speakingSubmissions = speakingSubmissionRepository.findAllByMockTestId(mockTestId);
            List<UUID> speakingSubmissionIds = speakingSubmissions.stream().map(SpeakingSubmissionEntity::getId).toList();
            List<SpeakingErrorEntity> speakingErrors = speakingErrorRepository.findAllSpeakingErrorIn(speakingSubmissionIds);
            Map<UUID, List<SpeakingErrorEntity>> speakingSubmissionErrorGroup = speakingErrors.stream().collect(
                    Collectors.groupingBy(SpeakingErrorEntity::getSpeakingSubmissionId)
            );
            for(SpeakingSubmissionEntity speakingSubmission : speakingSubmissions){
                List<SpeakingErrorEntity> speakingErrorsGet = speakingSubmissionErrorGroup.getOrDefault(speakingSubmission.getId(), null);
                if(speakingErrorsGet == null) continue;
                speakingSubmission.setSpeakingErrors(new HashSet<>(speakingErrorsGet));
            }
            mockTest.setSpeakingSubmissions(new HashSet<>(speakingSubmissions));
            return MockTestMapper.INSTANCE.toMockTestSpeakingResponse(mockTest);
        }
        else{
            List<MockTestDetailEntity> mockTestDetails = mockTestDetailRepository.findMockDetailJoinUpperLayerByMockId(mockTestId);
            Map<MockTestResultEntity, List<MockTestDetailEntity>> mockResultDetailsGroup = mockTestDetails.stream()
                    .collect(Collectors.groupingBy(MockTestDetailEntity::getResultMockTest));
            Set<MockTestResultEntity> mockResultSet = mockResultDetailsGroup.keySet();
            for(MockTestResultEntity mockResult : mockResultSet){
                mockResult.setMockTestDetails(new HashSet<>(mockResultDetailsGroup.getOrDefault(mockResult, Collections.emptyList())));
                mockResult.setMockTest(mockTest);
            }
            mockTest.setMockTestResults(mockResultSet);
            return MockTestMapper.INSTANCE.toMockTestRnListeningInforResponse(mockTest);
        }
    }

}
