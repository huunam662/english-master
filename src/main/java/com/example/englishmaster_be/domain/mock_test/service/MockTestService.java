package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.mock_test.model.QMockTestEntity;
import com.example.englishmaster_be.domain.mock_test.repository.jdbc.MockTestJdbcRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jdbc.MockTestDetailJdbcRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jpa.MockTestDetailRepository;
import com.example.englishmaster_be.domain.mock_test_result.repository.jdbc.MockTestResultJdbcRepository;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.topic.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestResultEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.mock_test.dto.request.*;
import com.example.englishmaster_be.domain.mock_test.dto.response.IMockTestToUserResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test.repository.jpa.MockTestRepository;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.shared.service.mailer.MailerService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.common.constant.error.Error;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j(topic = "MOCK-TEST-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestService implements IMockTestService {

    JPAQueryFactory queryFactory;

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

    MailerService mailerService;


    @Override
    public MockTestEntity getMockTestById(UUID mockTestId) {

        return mockTestRepository.findMockTestJoinUserAndTopic(mockTestId)
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
                        () -> new ErrorHolder(Error.MOCK_TEST_NOT_FOUND)
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

        INumberAndScoreQuestionTopic numberAndScoreQuestionTopic = topicService.getNumberAndScoreQuestionTopic(topicOfMockTest.getTopicId());

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

        float answersCorrectPercent = (float) totalAnswersCorrect / numberAndScoreQuestionTopic.getNumberQuestions();
        answersCorrectPercent = Math.round(answersCorrectPercent * 100f) / 100f;

        mockTestRepository.updateMockTest(
                mockTestId, answersCorrectPercent, totalAnswersCorrect, totalAnswersWrong,
                totalQuestionsFinish, totalQuestionsSkip, totalScoreMockTest
        );
        mockTestResultJdbcRepository.batchInsertMockTestResult(mockTestResults);
        mockTestDetailJdbcRepository.batchInsertMockTestDetail(mockTestDetails);

        try{
            sendEmailToMock(mockTestId);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

        return MockTestKeyResponse.builder()
                .mockTestId(mockTestId)
                .build();
    }



//    protected void saveResultMockTest(MockTestEntity mockTest, UUID partUUID, int correctAnswers, int score, UserEntity user) {
//
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
//        mockTestResultRepository.save(resultMockTest);
//    }


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

        return detailMockTestList;
    }

    @SneakyThrows
    @Override
    public void sendEmailToMock(UUID mockTestId) {

        mailerService.sendResultEmail(mockTestId);
    }

    @Override
    public MockTestPartResponse getPartToMockTest(UUID mockTestId) {

        return null;

//        UserEntity currentUser = userService.currentUser();
//
//        MockTestEntity mockTest = findMockTestToId(mockTestId);
//
//        if (!currentUser.equals(mockTest.getUser()))
//            throw new ErrorHolder(Error.BAD_REQUEST, )("You cannot view other people's tests");
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
    public MockTestEntity getInformationMockTest(UUID mockTestId){

        Assert.notNull(mockTestId, "Mock test id is required.");

        UserEntity currentUser = userService.currentUser();

        MockTestEntity mockTest = getMockTestById(mockTestId);

        UserEntity ownerOfMockTest = mockTest.getUser();

        if(ownerOfMockTest == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Unknown owner of mock test.");

        if(!currentUser.getUserId().equals(ownerOfMockTest.getUserId()))
            throw new ErrorHolder(Error.UNAUTHORIZED, "User current mustn't owner of mock test");

        List<MockTestDetailEntity> mockTestDetails = mockTestDetailRepository.findMockDetailJoinQuestionAnswerMockResultPartByMockId(mockTestId);

        Map<MockTestResultEntity, List<MockTestDetailEntity>> mockResultDetailsGroup = mockTestDetails.stream()
                .collect(Collectors.groupingBy(MockTestDetailEntity::getResultMockTest));

        Set<MockTestResultEntity> mockResultSet = mockResultDetailsGroup.keySet();
        for(MockTestResultEntity mockResult : mockResultSet){
            mockResult.setMockTestDetails(new HashSet<>(mockResultDetailsGroup.getOrDefault(mockResult, Collections.emptyList())));
            mockResult.setMockTest(mockTest);
        }

        mockTest.setMockTestResults(mockResultSet);

        return mockTest;
    }

}
