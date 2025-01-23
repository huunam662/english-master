package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.mock_test.dto.request.*;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestTotalCountResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.helper.MockTestHelper;
import com.example.englishmaster_be.helper.PartHelper;
import com.example.englishmaster_be.helper.QuestionHelper;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailRepository;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test.MockTestRepository;
import com.example.englishmaster_be.model.mock_test.QMockTestEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.PartQueryFactory;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultRepository;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.MockTestUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.money.CurrencyValidatorForMonetaryAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestService implements IMockTestService {

    JPAQueryFactory queryFactory;

    JavaMailSender mailSender;

    ResourceLoader resourceLoader;

    MockTestUtil mockTestUtil;

    MockTestRepository mockTestRepository;

    MockTestDetailRepository mockTestDetailRepository;

    MockTestResultRepository mockTestResultRepository;

    IUserService userService;

    ITopicService topicService;

    IAnswerService answerService;

    IPartService partService;

    IQuestionService questionService;


    @Override
    public MockTestEntity findMockTestById(UUID mockTestId) {

        return mockTestRepository.findByMockTestId(mockTestId)
                .orElseThrow(
                        () -> new CustomException(ErrorEnum.MOCK_TEST_NOT_FOUND)
                );
    }

    @Transactional
    @Override
    public MockTestEntity saveMockTest(MockTestRequest mockTestRequest) {

        if(mockTestRequest == null)
            throw new BadRequestException("Mock test request is null");

        if(mockTestRequest.getParts() == null)
            throw new BadRequestException("Parts of topic in Mock test request is null");

        UserEntity userCurrent = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(mockTestRequest.getTopicId());

        List<MockTestPartRequest> mockTestPartRequestList = mockTestRequest.getParts().stream()
                .filter(Objects::nonNull).toList();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime workTimeTopic = LocalTime.parse(mockTestRequest.getWorkTimeTopic(), dateTimeFormatter);

        LocalTime workTimeFinal = LocalTime.parse(mockTestRequest.getWorkTimeFinal(), dateTimeFormatter);

        LocalTime finishTime =  LocalTime.MIN.plus(
                Duration.between(workTimeFinal, workTimeTopic)
        );

        MockTestEntity mockTestEntity = MockTestEntity.builder()
                .mockTestId(UUID.randomUUID())
                .workTime(workTimeTopic)
                .finishTime(finishTime)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(userCurrent)
                .topic(topicEntity)
                .build();

        mockTestEntity = mockTestRepository.save(mockTestEntity);

        if(mockTestEntity.getMockTestResults() == null)
            mockTestEntity.setMockTestResults(new ArrayList<>());

        MockTestTotalCountResponse mockTestTotalCountResponse = MockTestTotalCountResponse.builder()
                .totalQuestionsChildMockTest(
                        MockTestHelper.totalQuestionOfPartRequestList(mockTestRequest.getParts())
                )
                .build();

        for (MockTestPartRequest mockTestPartRequest : mockTestPartRequestList) {

            MockTestResultEntity mockTestResultEntity = mockTestUtil.initMockTestEntity(
                    mockTestPartRequest,
                    mockTestTotalCountResponse,
                    mockTestEntity,
                    userCurrent
            );

            mockTestEntity.getMockTestResults().add(mockTestResultEntity);

            if (mockTestResultEntity.getMockTestDetails() == null)
                mockTestResultEntity.setMockTestDetails(new ArrayList<>());

            List<MockTestQuestionParentRequest> mockTestQuestionParentRequestList = mockTestPartRequest.getQuestionParentAnswers().stream().filter(Objects::nonNull).toList();

            for (MockTestQuestionParentRequest mockTestQuestionParentRequest : mockTestQuestionParentRequestList) {

                List<MockTestQuestionChildrenRequest> mockTestQuestionChildrenRequestList = mockTestQuestionParentRequest.getQuestionChildrenAnswers();

                if (mockTestQuestionChildrenRequestList == null) continue;

                for (MockTestQuestionChildrenRequest mockTestQuestionChildrenRequest : mockTestQuestionChildrenRequestList) {

                    mockTestUtil.addMockTestDetailEntity(
                            mockTestResultEntity,
                            userCurrent,
                            mockTestQuestionChildrenRequest,
                            mockTestTotalCountResponse
                    );
                }
            }
        }

        mockTestEntity.setTotalScoreParts(mockTestTotalCountResponse.getTotalScoreParts());
        mockTestEntity.setTotalQuestionsChoose(mockTestTotalCountResponse.getTotalQuestionsChildMockTest());
        mockTestEntity.setTotalQuestionsWork(mockTestTotalCountResponse.getTotalQuestionChildOfParts());
        mockTestEntity.setTotalAnswersCorrect(mockTestTotalCountResponse.getTotalAnswersCorrect());
        mockTestEntity.setTotalAnswersWrong(mockTestTotalCountResponse.getTotalAnswersWrong());
        mockTestEntity.setTotalScoreFinish(mockTestTotalCountResponse.getTotalScoreFinish());
        mockTestEntity.setTotalQuestionsSkip(
                mockTestEntity.getTotalQuestionsWork() - mockTestEntity.getTotalQuestionsChoose()
        );

        float answersCorrectPercent = BigDecimal.valueOf(
                    (float) mockTestEntity.getTotalScoreFinish() / mockTestEntity.getTotalScoreParts()
                )
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();

        mockTestEntity.setAnswersCorrectPercent(answersCorrectPercent);

        return mockTestRepository.save(mockTestEntity);
    }

    @Override
    public List<MockTestEntity> getTop10MockTest(int index) {

        Sort sortOption = Sort.by(Sort.Order.desc("updateAt"));

        Pageable pageable = PageRequest.of(index, 10, sortOption);

        Page<MockTestEntity> mockTestPage = mockTestRepository.findAll(pageable);

        return mockTestPage.getContent();
    }

    @Override
    public List<MockTestEntity> getTop10MockTestToUser(int index, UserEntity user) {

        Sort sortOption = Sort.by(Sort.Order.desc("updateAt"));

        Pageable pageable = PageRequest.of(index, 10, sortOption);

        Page<MockTestEntity> mockTestPage = mockTestRepository.findAllByUser(user, pageable);

        return mockTestPage.getContent();
    }

    @Override
    public MockTestEntity findMockTestToId(UUID mockTestId) {

        return mockTestRepository.findByMockTestId(mockTestId)
                .orElseThrow(
                        () -> new CustomException(ErrorEnum.MOCK_TEST_NOT_FOUND)
                );
    }

    @Override
    public List<MockTestDetailEntity> getTop10DetailToCorrect(int index, boolean isCorrect, MockTestEntity mockTest) {

        return null;

//        Page<MockTestDetailEntity> detailMockTestPage = detailMockTestRepository.findAllByMockTest(mockTest, PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));
//
//        log.info("Repository returned: {}", detailMockTestPage.getContent());
//        List<MockTestDetailEntity> detailMockTests = detailMockTestPage.getContent();
//        Iterator<MockTestDetailEntity> iterator = detailMockTests.iterator();

        // Dùng Iterator để tránh ConcurrentModificationException
//        while (iterator.hasNext()) {
//            MockTestDetailEntity detailMockTest = iterator.next();
//            if (detailMockTest.getAnswer().getCorrectAnswer() != isCorrect) {
//                iterator.remove();
//            }
//        }

//        return detailMockTests;
    }


    @Override
    public int countCorrectAnswer(UUID mockTestId) {
        int count = 0;
//        MockTestEntity mockTest = findMockTestToId(mockTestId);
//        for (MockTestDetailEntity detailMockTest : mockTest.getDetailMockTests()) {
//            if (detailMockTest.getAnswer().getCorrectAnswer()) {
//                count = count + 1;
//            }
//        }
        return count;
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
    public FilterResponse<?> getListMockTestOfAdmin(MockTestFilterRequest filterRequest) {

        FilterResponse<MockTestResponse> filterResponse = FilterResponse.<MockTestResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        long totalElements = Optional.ofNullable(
                                         queryFactory
                                                 .select(QMockTestEntity.mockTestEntity.count())
                                                 .from(QMockTestEntity.mockTestEntity)
                                                 .fetchOne()
                                        ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QMockTestEntity.mockTestEntity.updateAt.desc();
        else orderSpecifier = QMockTestEntity.mockTestEntity.updateAt.asc();

        JPAQuery<MockTestEntity> query = queryFactory
                                    .selectFrom(QMockTestEntity.mockTestEntity)
                                    .orderBy(orderSpecifier)
                                    .offset(filterResponse.getOffset())
                                    .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                MockTestMapper.INSTANCE.toMockTestResponseList(query.fetch())
        );

        return filterResponse;
    }

    @Override
    public List<MockTestEntity> getListMockTestToUser(int index, UUID userId) {

        UserEntity user = userService.getUserById(userId);

        return getTop10MockTestToUser(index, user);
    }

    @Transactional
    @SneakyThrows
    @Override
    public List<MockTestDetailEntity> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId) {

        return null;

//        UserEntity user = userService.currentUser();
//        MockTestEntity mockTest = findMockTestToId(mockTestId);
//        List<MockTestDetailEntity> detailMockTestList = new ArrayList<>();
//        int totalCorrect = 0;
//        int totalScore = 0;
//        int[] correctAnswers = new int[7];
//        int[] scores = new int[7];
//        List<UUID> partInTopic = new ArrayList<>();
//        int i = 1;
//        for (UUID answerId : listAnswerId) {
//            AnswerEntity answer = answerService.getAnswerById(answerId);
//            UUID part = answer.getQuestion().getPart().getPartId();
//            if (!partInTopic.contains(part)) {
//                partInTopic.add(part);
//            }
//            log.warn("STT: {},AnswerEntity ID: {}, PartEntity ID: {}", i, answer.getAnswerId(), answer.getQuestion().getPart().getPartId());
//            i++;
//            MockTestDetailEntity detailMockTest = MockTestDetailEntity.builder()
//                    .mockTest(mockTest)
//                    .answer(answer)
//                    .userCreate(user)
//                    .userUpdate(user)
//                    .build();
//            detailMockTest = detailMockTestRepository.save(detailMockTest);
//            detailMockTestList.add(detailMockTest);
//            if (answer.getCorrectAnswer()) {
//                totalCorrect++;
//                int questionScore = answer.getQuestion().getQuestionScore();
//                totalScore += questionScore;
//
//                int partIndex = getPartIndex(answer.getQuestion().getPart().getPartId());
//                if (partIndex != -1) {
//                    correctAnswers[partIndex]++;
//                    scores[partIndex] += questionScore;
//                }
//            }
//        }
//        for (UUID partId : partInTopic) {
//            int partIndex = getPartIndex(partId);
//            if (partIndex != -1) {
//                saveResultMockTest(mockTest, partId, correctAnswers[partIndex], scores[partIndex], user);
//            }
//        }
//
//        mockTest.setScore(totalScore);
//        mockTest.setCorrectAnswers(totalCorrect);
//        mockTestRepository.save(mockTest);
//        sendResultEmail(user.getEmail(), mockTest, totalCorrect, correctAnswers, scores, partInTopic);
//
//        return detailMockTestList;
    }


    protected String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }


    protected void sendResultEmail(String email, MockTestEntity mockTest, int correctAnswer, int[] corrects, int[] scores, List<UUID> listPartId) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String templateContent = readTemplateContent("test_email.html");

        templateContent = templateContent.replace("{{nameToeic}}", mockTest.getTopic().getTopicName());
        templateContent = templateContent.replace("{{userName}}", mockTest.getUser().getName());
        templateContent = templateContent.replace("{{correctAnswer}}", String.valueOf(correctAnswer));
//        templateContent = templateContent.replace("{{score}}", String.valueOf(mockTest.getScore()));
//        templateContent = templateContent.replace("{{timeAnswer}}", String.valueOf(mockTest.getTime()));

        StringBuilder partsHtml = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            if (listPartId.contains(getPartUUID(i))) {
                String partHtml = "<p>Số câu đúng PartEntity " + (i + 1) + ": " + corrects[i] + "<br>Số điểm PartEntity " + (i + 1) + ": " + scores[i] + "</p>";
                partsHtml.append(partHtml);
            }
        }

        templateContent = templateContent.replace("{{parts}}", partsHtml.toString());

        helper.setTo(email);
        helper.setSubject("Thông tin bài thi");
        helper.setText(templateContent, true);

        mailSender.send(message);
    }

    protected void saveResultMockTest(MockTestEntity mockTest, UUID partUUID, int correctAnswers, int score, UserEntity user) {

//        PartEntity part = partService.getPartToId(partUUID);
//
//        MockTestResultEntity resultMockTest = MockTestResultEntity.builder()
//                .mockTest(mockTest)
//                .part(part)
//                .correctAnswer(correctAnswers)
//                .score(score)
//                .createAt(LocalDateTime.now())
//                .updateAt(LocalDateTime.now())
//                .userCreate(user)
//                .userUpdate(user)
//                .build();
//
//        resultMockTestRepository.save(resultMockTest);
    }


    protected int getPartIndex(UUID partId) {
        Map<UUID, Integer> partIdToIndexMap = Map.of(
                UUID.fromString("5e051716-1b41-4385-bfe6-3e350d5acb06"), 0, // PartEntity 1
                UUID.fromString("9509bfa5-0403-48db-bee1-1af41cfc73df"), 1, // PartEntity 2
                UUID.fromString("2496a543-49c3-4580-80b6-c9984e4142e1"), 2, // PartEntity 3
                UUID.fromString("3b4d6b90-fc31-484e-afe3-3a21162b6454"), 3, // PartEntity 4
                UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"), 4, // PartEntity 5
                UUID.fromString("22b25c09-33db-4e3a-b228-37b331b39c96"), 5, // PartEntity 6
                UUID.fromString("2416aa89-3284-4315-b759-f3f1b1d5ff3f"), 6  // PartEntity 7
        );
        return partIdToIndexMap.getOrDefault(partId, -1);
    }

    protected UUID getPartUUID(int index) {
        List<UUID> partUUIDs = List.of(
                UUID.fromString("5e051716-1b41-4385-bfe6-3e350d5acb06"), // PartEntity 1
                UUID.fromString("9509bfa5-0403-48db-bee1-1af41cfc73df"), // PartEntity 2
                UUID.fromString("2496a543-49c3-4580-80b6-c9984e4142e1"), // PartEntity 3
                UUID.fromString("3b4d6b90-fc31-484e-afe3-3a21162b6454"), // PartEntity 4
                UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"), // PartEntity 5
                UUID.fromString("22b25c09-33db-4e3a-b228-37b331b39c96"), // PartEntity 6
                UUID.fromString("2416aa89-3284-4315-b759-f3f1b1d5ff3f")  // PartEntity 7
        );
        return partUUIDs.get(index);
    }

    @Override
    public List<MockTestDetailEntity> getListCorrectAnswer(int index, boolean isCorrect, UUID mockTestId) {

        MockTestEntity mockTest = findMockTestToId(mockTestId);

        List<MockTestDetailEntity> detailMockTestList = getTop10DetailToCorrect(index, isCorrect, mockTest);

        if (isCorrect)
            MessageResponseHolder.setMessage("Get top 10 AnswerEntity correct successfully");
        else
            MessageResponseHolder.setMessage("Get top 10 AnswerEntity wrong successfully");

        return detailMockTestList;
    }

    @SneakyThrows
    @Override
    public void sendEmailToMock(UUID mockTestId) {

//        UserEntity user = userService.currentUser();
//
//        MockTestEntity mockTest = findMockTestToId(mockTestId);
//
//        int correctAnswer = countCorrectAnswer(mockTestId);
//        int[] corrects = new int[7];
//        int[] scores = new int[7];
//        List<UUID> listPartId = new ArrayList<>();
//
//        List<MockTestResultEntity> resultMockTestList = resultMockTestRepository.findByMockTest_MockTestId(mockTest.getMockTestId());
//
//        for (MockTestResultEntity resultMockTest : resultMockTestList) {
//            int partIndex = getPartIndex(resultMockTest.getPart().getPartId());
//            corrects[partIndex] = correctAnswer;
//            scores[partIndex] = resultMockTest.getScore();
//            listPartId.add(resultMockTest.getPart().getPartId());
//        }
//
//        sendResultEmail(user.getEmail(), mockTest, correctAnswer, corrects, scores, listPartId);
    }

    @Override
    public MockTestPartResponse getPartToMockTest(UUID mockTestId) {

        return null;

//        UserEntity currentUser = userService.currentUser();
//
//        MockTestEntity mockTest = findMockTestToId(mockTestId);
//
//        if (!currentUser.equals(mockTest.getUser()))
//            throw new BadRequestException("You cannot view other people's tests");
//
//        MockTestPartResponse partMockTestResponse = MockTestMapper.INSTANCE.toPartMockTestResponse(mockTest);
//
//        partMockTestResponse.setParts(
//                mockTest.getTopic().getParts().stream()
//                        .sorted(Comparator.comparing(PartEntity::getCreateAt))
//                        .map(
//                        partItem -> {
//
//                            int totalQuestion = topicService.totalQuestion(partItem, mockTest.getTopic().getTopicId());
//
//                            PartResponse partResponse = PartMapper.INSTANCE.toPartResponse(partItem);
//
//                            partResponse.setTotalQuestion(totalQuestion);
//
//                            return partResponse;
//                        }
//                ).toList()
//        );
//
//        return partMockTestResponse;
    }

    @Override
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId) {

        UserEntity currentUser = userService.currentUser();

        MockTestEntity mockTest = findMockTestToId(mockTestId);

        if (!currentUser.equals(mockTest.getUser()))
            throw new BadRequestException("You cannot view other people's tests");

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
}
