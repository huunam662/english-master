package com.example.englishmaster_be.domain.mock_test.mock_test.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerRepository;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestInforRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestPageView;
import com.example.englishmaster_be.domain.mock_test.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.req.MockTestReq;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.repository.MockTestDslRepository;
import com.example.englishmaster_be.domain.mock_test.mock_test.repository.MockTestJdbcRepository;
import com.example.englishmaster_be.domain.mock_test.mock_test.repository.MockTestRepository;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.repository.ReadingListeningSubmissionJdbcRepository;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.repository.ReadingListeningSubmissionRepository;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestKeyRes;
import com.example.englishmaster_be.domain.exam.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestToUserView;
import com.example.englishmaster_be.domain.exam.question.service.IQuestionService;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.repository.SpeakingErrorRepository;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.repository.SpeakingSubmissionRepository;
import com.example.englishmaster_be.domain.exam.topic.topic.service.ITopicService;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.auth.service.mailer.MailerService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class MockTestService implements IMockTestService {

    private final MockTestRepository mockTestRepository;
    private final IUserService userService;
    private final ITopicService topicService;
    private final AnswerRepository answerRepository;
    private final IQuestionService questionService;
    private final ReadingListeningSubmissionRepository mockTestDetailRepository;
    private final ReadingListeningSubmissionJdbcRepository mockTestDetailJdbcRepository;
    private final MockTestJdbcRepository mockTestJdbcRepository;
    private final SpeakingSubmissionRepository speakingSubmissionRepository;
    private final SpeakingErrorRepository speakingErrorRepository;
    private final MailerService mailerService;
    private final MockTestDslRepository mockTestDslRepository;

    @Lazy
    public MockTestService(MockTestDslRepository mockTestDslRepository, MockTestRepository mockTestRepository, IUserService userService, ITopicService topicService, AnswerRepository answerRepository, IQuestionService questionService, ReadingListeningSubmissionRepository mockTestDetailRepository, ReadingListeningSubmissionJdbcRepository mockTestDetailJdbcRepository, MockTestJdbcRepository mockTestJdbcRepository, SpeakingSubmissionRepository speakingSubmissionRepository, SpeakingErrorRepository speakingErrorRepository, MailerService mailerService) {
        this.mockTestRepository = mockTestRepository;
        this.userService = userService;
        this.topicService = topicService;
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.mockTestDetailRepository = mockTestDetailRepository;
        this.mockTestDetailJdbcRepository = mockTestDetailJdbcRepository;
        this.mockTestJdbcRepository = mockTestJdbcRepository;
        this.speakingSubmissionRepository = speakingSubmissionRepository;
        this.speakingErrorRepository = speakingErrorRepository;
        this.mailerService = mailerService;
        this.mockTestDslRepository = mockTestDslRepository;
    }

    @Override
    public MockTestEntity getMockTestById(UUID mockTestId) {
        return mockTestRepository.findMockTestById(mockTestId)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Mock test not found.")
                );
    }

    @Transactional
    @Override
    public MockTestKeyRes saveMockTest(MockTestReq mockTestRequest) {
        // Kiểm tra dữ liệu đầu vào
        if (mockTestRequest == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Mock test request is null");
        }
        if (mockTestRequest.getTopicId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Topic id in mock test request is null");
        }
        if (mockTestRequest.getWorkTimeTopic() == null || mockTestRequest.getWorkTimeTopic().isBlank()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Work time topic is null or empty");
        }
        if (mockTestRequest.getWorkTimeFinal() == null || mockTestRequest.getWorkTimeFinal().isBlank()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Work time final is null or empty");
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
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid time format. Expected HH:mm:ss");
        }

        // Tính khoảng thời gian giữa workTimeFinal và workTimeTopic
        // Lưu ý: Nếu workTimeFinal lớn hơn workTimeTopic thì Duration sẽ âm. Bạn có thể điều chỉnh theo nghiệp vụ.
        Duration duration = Duration.between(workTimeFinal, workTimeTopic);
        LocalTime finishTime = LocalTime.MIN.plus(duration);

        // Khởi tạo MockTestEntity (createAt, updateAt được tự động cập nhật bởi Hibernate)
        UUID mockTestId = mockTestJdbcRepository.insertMockTest(workTimeTopic, finishTime, currentUser.getUserId(), topicEntity.getTopicId());

        // Lưu và trả về key đối tượng MockTestEntity vừa tạo
        return new MockTestKeyRes(mockTestId);
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
    public List<IMockTestToUserView> getListMockTestToUser() {
        UserEntity user = userService.currentUser();
        return mockTestRepository.findExamResultForUser(user.getUserId());
    }

    @Transactional
    @Override
    public MockTestKeyRes addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId) {

        UserEntity userSubmitAnswers = userService.currentUser();
        MockTestEntity mockTest = getMockTestById(mockTestId);
        UserEntity userOfMockTest = mockTest.getUser();
        TopicEntity topicOfMockTest = mockTest.getTopic();

        if(topicOfMockTest == null)
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Unknown topic of mock test");

        if(userOfMockTest == null)
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Unknown user of mock test");

        if(!userSubmitAnswers.getUserId().equals(userOfMockTest.getUserId()))
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User current mustn't owner of mock test");

        INumberAndScoreQuestionTopic numberAndScoreQuestionTopic = questionService.getNumberAndScoreQuestionTopic(topicOfMockTest.getTopicId());

        List<AnswerEntity> answersSubmit = answerRepository.findAnswersInAnswerIds(topicOfMockTest.getTopicId(), listAnswerId);

        int totalQuestionsFinish = listAnswerId.size();
        int totalQuestionsSkip = numberAndScoreQuestionTopic.getNumberQuestions() - totalQuestionsFinish;
        int totalAnswersCorrect = 0;
        int totalAnswersWrong = 0;
        int totalScoreMockTest = 0;

        List<ReadingListeningSubmissionEntity> mockTestDetails = new ArrayList<>();
        for(AnswerEntity answerChoice : answersSubmit) {
            if(answerChoice == null) continue;
            boolean isCorrectAnswer = answerChoice.getCorrectAnswer();
            QuestionEntity questionChild = answerChoice.getQuestion();
            int scoreAchieved = questionChild.getQuestionScore();
            if(isCorrectAnswer){
                totalAnswersCorrect++;
                totalScoreMockTest+=scoreAchieved;
            }
            else {
                totalAnswersWrong++;
            }
            ReadingListeningSubmissionEntity mockTestDetail = new ReadingListeningSubmissionEntity();
            mockTestDetail.setId(UUID.randomUUID());
            mockTestDetail.setMockTest(mockTest);
            mockTestDetail.setAnswerChoice(answerChoice);
            mockTestDetail.setAnswerContent(answerChoice.getAnswerContent());
            mockTestDetail.setIsCorrectAnswer(isCorrectAnswer);
            mockTestDetail.setScoreAchieved(isCorrectAnswer ? scoreAchieved : 0);
            mockTestDetails.add(mockTestDetail);
        }

        float answersCorrectPercent = BigDecimal.valueOf((float) totalAnswersCorrect / numberAndScoreQuestionTopic.getNumberQuestions())
                        .setScale(2, RoundingMode.HALF_UP).floatValue();

        mockTestRepository.updateMockTest(
                mockTestId, answersCorrectPercent, totalAnswersCorrect, totalAnswersWrong,
                totalQuestionsFinish, totalQuestionsSkip, totalScoreMockTest
        );
        mockTestDetailJdbcRepository.batchInsert(mockTestDetails);

        sendEmailToMock(mockTestId);

        return new MockTestKeyRes(mockTestId);
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
    public MockTestInforRes getInformationMockTest(UUID mockTestId){

        UserEntity currentUser = userService.currentUser();

        MockTestEntity mockTest = mockTestRepository.findMockTestJoinUserTopicTopicType(mockTestId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Mock test not found."));

        UserEntity ownerOfMockTest = mockTest.getUser();

        if(ownerOfMockTest == null)
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Unknown owner of mock test.");

        if(!currentUser.getUserId().equals(ownerOfMockTest.getUserId()))
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User current mustn't owner of mock test");

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
                speakingSubmission.setSpeakingErrors(new LinkedHashSet<>(speakingErrorsGet));
            }
            mockTest.setSpeakingSubmissions(new LinkedHashSet<>(speakingSubmissions));
            return MockTestMapper.INSTANCE.toMockTestSpeakingResponse(mockTest);
        }
        else{
            List<ReadingListeningSubmissionEntity> mockTestDetails = mockTestDetailRepository.findMockDetailJoinUpperLayerByMockId(mockTestId);
            mockTest.setReadingListeningSubmissions(new LinkedHashSet<>(mockTestDetails));
            return MockTestMapper.INSTANCE.toMockTestRnListeningInforResponse(mockTest);
        }
    }

    @Override
    public Page<IMockTestPageView> getMockTestPage(PageOptionsReq optionsReq) {
        return mockTestDslRepository.findPageMockTestUser(optionsReq);
    }

}
