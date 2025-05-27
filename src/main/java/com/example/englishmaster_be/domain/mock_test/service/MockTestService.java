package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.shared.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.mock_test.dto.request.*;
import com.example.englishmaster_be.domain.mock_test.dto.response.IMockTestToUserResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailRepository;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test.MockTestRepository;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultRepository;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.helper.MockTestHelper;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.common.constant.error.Error;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.example.englishmaster_be.model.mock_test.QMockTestEntity;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestService implements IMockTestService {

    JPAQueryFactory queryFactory;

    JavaMailSender mailSender;

    ResourceLoader resourceLoader;

    MockTestHelper mockTestUtil;

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
                        () -> new ErrorHolder(Error.MOCK_TEST_NOT_FOUND)
                );
    }

    @Transactional
    @Override
    public MockTestEntity saveMockTest(MockTestRequest mockTestRequest) {
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
        MockTestEntity mockTest = MockTestEntity.builder()
                .workTime(workTimeTopic)
                .finishTime(finishTime)
                .user(currentUser)
                .topic(topicEntity)
                .build();

        // Lưu và trả về đối tượng MockTestEntity vừa tạo
        return mockTestRepository.save(mockTest);
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
    public List<MockTestDetailEntity> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId) {
        // Lấy thông tin người dùng hiện hành và bài thi tương ứng
        UserEntity currentUser = userService.currentUser();
        MockTestEntity mockTest = findMockTestToId(mockTestId);

        // Map để nhóm các MockTestDetail theo partId, đồng thời lưu số câu đúng và điểm của mỗi phần
        Map<UUID, List<MockTestDetailEntity>> partDetailsMap = new HashMap<>();
        Map<UUID, Integer> partCorrectCountMap = new HashMap<>();
        Map<UUID, Integer> partScoreMap = new HashMap<>();

        int totalCorrect = 0;
        int totalScore = 0;

        // Duyệt qua từng id đáp án mà user chọn
        for (UUID answerId : listAnswerId) {
            // Lấy đối tượng Answer; nếu không tìm thấy sẽ ném exception
            AnswerEntity answer = answerService.getAnswerById(answerId);
            if (answer == null) {
                throw new ErrorHolder(Error.BAD_REQUEST, "Answer not found for id: " + answerId);
            }

            // Lấy id của part mà câu hỏi của đáp án này thuộc về
            UUID partId = answer.getQuestion().getPart().getPartId();

            // Tính điểm đạt được: nếu đáp án đúng thì bằng điểm của câu hỏi, ngược lại là 0
            int scoreAchieved = Boolean.TRUE.equals(answer.getCorrectAnswer())
                    ? answer.getQuestion().getQuestionScore()
                    : 0;

            // Tạo đối tượng MockTestDetailEntity với thông tin đáp án và câu hỏi
            MockTestDetailEntity detail = MockTestDetailEntity.builder()
                    .answerChoice(answer)
                    .answerContent(answer.getAnswerContent())
                    // Có thể set thêm các thông tin khác như answerCorrectContent nếu cần
                    .isCorrectAnswer(answer.getCorrectAnswer())
                    .scoreAchieved(scoreAchieved)
                    .questionChild(answer.getQuestion())
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .build();
            // Lưu để có id (nếu cần cho quan hệ)
            detail = mockTestDetailRepository.save(detail);

            // Nhóm detail theo partId
            partDetailsMap.computeIfAbsent(partId, k -> new ArrayList<>()).add(detail);

            // Nếu đáp án đúng, cập nhật số câu đúng và điểm tổng
            if (Boolean.TRUE.equals(answer.getCorrectAnswer())) {
                totalCorrect++;
                totalScore += answer.getQuestion().getQuestionScore();
                partCorrectCountMap.put(partId, partCorrectCountMap.getOrDefault(partId, 0) + 1);
                partScoreMap.put(partId, partScoreMap.getOrDefault(partId, 0) + answer.getQuestion().getQuestionScore());
            }
        }

        // Với mỗi part mà user đã trả lời, tạo MockTestResultEntity
        for (Map.Entry<UUID, List<MockTestDetailEntity>> entry : partDetailsMap.entrySet()) {
            UUID partId = entry.getKey();
            List<MockTestDetailEntity> detailsForPart = entry.getValue();
            int partCorrect = partCorrectCountMap.getOrDefault(partId, 0);
            int partScore = partScoreMap.getOrDefault(partId, 0);

            // Khởi tạo đối tượng kết quả cho phần (ở đây sử dụng PartEntity với partId)
            MockTestResultEntity result = MockTestResultEntity.builder()
                    .mockTest(mockTest)
                    .part(PartEntity.builder().partId(partId).build())
                    .totalScoreParts(partScore)
                    .totalCorrect(partCorrect)
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .build();
            result = mockTestResultRepository.save(result);

            // Liên kết các chi tiết trong phần đó với đối tượng kết quả vừa tạo
            for (MockTestDetailEntity detail : detailsForPart) {
                detail.setResultMockTest(result);
                mockTestDetailRepository.save(detail);
            }
        }

        // Lấy danh sách tất cả các part trong topic của mock test
        List<PartEntity> topicParts = mockTest.getTopic().getParts().stream().toList();
        // Với các part mà user không làm (không có trong partDetailsMap), tạo MockTestResultEntity với số câu đúng, điểm là 0
        for (PartEntity part : topicParts) {
            if (!partDetailsMap.containsKey(part.getPartId())) {
                MockTestResultEntity emptyResult = MockTestResultEntity.builder()
                        .mockTest(mockTest)
                        .part(part)
                        .totalScoreParts(0)
                        .totalCorrect(0)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();
                mockTestResultRepository.save(emptyResult);
            }
        }

        // Cập nhật tổng điểm và số câu đúng vào MockTestEntity.
        int totalQuestions = mockTest.getTopic().getNumberQuestion();
        int totalAttempted = listAnswerId.size();
        mockTest.setTotalScore(totalScore);
        mockTest.setTotalAnswersCorrect(totalCorrect);
        mockTest.setTotalQuestionsSkip(totalQuestions - totalAttempted);
        mockTest.setTotalQuestionsFinish(listAnswerId.size());
        mockTest.setTotalAnswersWrong(totalAttempted - totalCorrect);
        if (totalAttempted > 0) {
            float percent = ((float) totalCorrect / totalQuestions) * 100;
            mockTest.setAnswersCorrectPercent(percent);
        }
        mockTestRepository.save(mockTest);

        // Gửi email kết quả cho user (nếu cần)
        try {
            sendResultEmail(currentUser.getEmail(), mockTest, totalCorrect, partCorrectCountMap, partScoreMap);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }

        // Trả về danh sách tất cả các MockTestDetailEntity đã lưu
        return partDetailsMap.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    protected String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }


    protected void sendResultEmail(String email, MockTestEntity mockTest, int correctAnswer,
                                   Map<UUID, Integer> partCorrectMap,
                                   Map<UUID, Integer> partScoreMap) throws IOException, MessagingException {
        // Tạo đối tượng MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Đọc nội dung mẫu email từ file (ví dụ: test_email.html)
        String templateContent = readTemplateContent("test_email.html");

        // Thay thế các placeholder trong template
        templateContent = templateContent.replace("{{nameToeic}}", mockTest.getTopic().getTopicName());
        templateContent = templateContent.replace("{{userName}}", mockTest.getUser().getName());
        templateContent = templateContent.replace("{{correctAnswer}}", String.valueOf(correctAnswer));
        templateContent = templateContent.replace("{{score}}", mockTest.getTotalScore() + "");
        templateContent = templateContent.replace("{{timeAnswer}}", mockTest.getFinishTime()  + "");
        // Nếu cần, bạn có thể thay thế thêm các placeholder khác như tổng điểm, thời gian trả lời,...

        // Xây dựng nội dung hiển thị kết quả của từng phần dựa trên Map
        StringBuilder partsHtml = new StringBuilder();
        // Nếu hệ thống có nhiều Part, bạn có thể duyệt qua các entry của Map
        for (Map.Entry<UUID, Integer> entry : partCorrectMap.entrySet()) {
            UUID partId = entry.getKey();
            int correctCount = entry.getValue();
            int score = partScoreMap.getOrDefault(partId, 0);

            // Ở đây hiển thị theo mẫu: "Part {ID} - Số câu đúng: {correctCount} - Số điểm: {score}"
            // Nếu có tên Part hoặc thứ tự Part, bạn có thể thay thế phần hiển thị này.
            String partHtml = "<p>Part " + partService.getPartToId(partId).getPartName() + ": Số câu đúng: " + correctCount
                    + " - Số điểm: " + score + "</p>";
            partsHtml.append(partHtml);
        }
        templateContent = templateContent.replace("{{parts}}", partsHtml.toString());

        // Cấu hình thông tin email
        helper.setTo(email);
        helper.setSubject("Thông tin bài thi");
        helper.setText(templateContent, true);

        // Gửi email
        mailSender.send(message);
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
    public MockTestEntity getInformationMockTest(UUID mockTestId){
        return findMockTestToId(mockTestId);
    }
}
