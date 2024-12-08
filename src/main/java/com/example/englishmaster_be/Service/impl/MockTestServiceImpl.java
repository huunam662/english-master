package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.DTO.MockTest.MockTestFilterRequest;
import com.example.englishmaster_be.DTO.MockTest.SaveMockTestDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.Exception.Error;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestServiceImpl implements IMockTestService {

    JPAQueryFactory queryFactory;

    JavaMailSender mailSender;

    ResourceLoader resourceLoader;

    MockTestRepository mockTestRepository;

    DetailMockTestRepository detailMockTestRepository;

    ResultMockTestRepository resultMockTestRepository;

    IUserService userService;

    ITopicService topicService;

    IAnswerService answerService;

    IPartService partService;

    IQuestionService questionService;


    @Override
    public MockTest findMockTestById(UUID mockTestId) {

        return mockTestRepository.findByMockTestId(mockTestId)
                .orElseThrow(
                        () -> new CustomException(Error.MOCK_TEST_NOT_FOUND)
                );
    }

    @Transactional
    @Override
    public MockTestResponse saveMockTest(SaveMockTestDTO saveMockTestDTO) {

        User user = userService.currentUser();

        MockTest mockTest = new MockTest(saveMockTestDTO);

        mockTest.setTopic(topicService.findTopicById(saveMockTestDTO.getTopic_id()));
        mockTest.setUser(user);
        mockTest.setUserCreate(user);
        mockTest.setUserUpdate(user);

        mockTest = mockTestRepository.save(mockTest);

        return new MockTestResponse(mockTest);
    }

    @Override
    public List<MockTest> getTop10MockTest(int index) {

        Sort sortOption = Sort.by(Sort.Order.desc("updateAt"));

        Pageable pageable = PageRequest.of(index, 10, sortOption);

        Page<MockTest> mockTestPage = mockTestRepository.findAll(pageable);

        return mockTestPage.getContent();
    }

    @Override
    public List<MockTest> getTop10MockTestToUser(int index, User user) {

        Sort sortOption = Sort.by(Sort.Order.desc("updateAt"));

        Pageable pageable = PageRequest.of(index, 10, sortOption);

        Page<MockTest> mockTestPage = mockTestRepository.findAllByUser(user, pageable);

        return mockTestPage.getContent();
    }

    @Override
    public MockTest findMockTestToId(UUID mockTestId) {
        return mockTestRepository.findByMockTestId(mockTestId).orElseThrow(() -> new CustomException(Error.MOCK_TEST_NOT_FOUND));
    }

    @Override
    public List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect, MockTest mockTest) {

        Page<DetailMockTest> detailMockTestPage = detailMockTestRepository.findAllByMockTest(mockTest, PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));

        log.info("Repository returned: {}", detailMockTestPage.getContent());
        List<DetailMockTest> detailMockTests = detailMockTestPage.getContent();
        Iterator<DetailMockTest> iterator = detailMockTests.iterator();

        // Dùng Iterator để tránh ConcurrentModificationException
        while (iterator.hasNext()) {
            DetailMockTest detailMockTest = iterator.next();
            if (detailMockTest.getAnswer().isCorrectAnswer() != isCorrect) {
                iterator.remove();
            }
        }

        return detailMockTests;
    }


    @Override
    public int countCorrectAnswer(UUID mockTestId) {
        int count = 0;
        MockTest mockTest = findMockTestToId(mockTestId);
        for (DetailMockTest detailMockTest : mockTest.getDetailMockTests()) {
            if (detailMockTest.getAnswer().isCorrectAnswer()) {
                count = count + 1;
            }
        }
        return count;
    }

    @Override
    public List<MockTest> getAllMockTestByYearMonthAndDay(Topic topic, String year, String month, String day) {
        if (day == null && month != null) {
            return mockTestRepository.findAllByYearMonth(year, month, topic);
        }
        if (month == null) {
            return mockTestRepository.findAllByYear(year, topic);
        }
        return mockTestRepository.findAllByYearMonthAndDay(year, month, day, topic);
    }

    @Override
    public List<MockTest> getAllMockTestToTopic(Topic topic) {
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
                                                 .select(QMockTest.mockTest.count())
                                                 .from(QMockTest.mockTest)
                                                 .fetchOne()
                                        ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QMockTest.mockTest.updateAt.desc();
        else orderSpecifier = QMockTest.mockTest.updateAt.asc();

        JPAQuery<MockTest> query = queryFactory
                                    .selectFrom(QMockTest.mockTest)
                                    .orderBy(orderSpecifier)
                                    .offset(filterResponse.getOffset())
                                    .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(MockTestResponse::new).toList()
        );

        return filterResponse;
    }

    @Override
    public List<MockTestResponse> getListMockTestToUser(int index, UUID userId) {

        User user = userService.findUserById(userId);

        List<MockTest> listMockTest = getTop10MockTestToUser(index, user);

        return listMockTest.stream().map(MockTestResponse::new).toList();
    }

    @Transactional
    @SneakyThrows
    @Override
    public List<DetailMockTestResponse> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId) {

        User user = userService.currentUser();
        MockTest mockTest = findMockTestToId(mockTestId);
        List<DetailMockTestResponse> detailMockTestList = new ArrayList<>();
        int totalCorrect = 0;
        int totalScore = 0;
        int[] correctAnswers = new int[7];
        int[] scores = new int[7];
        List<UUID> partInTopic = new ArrayList<>();
        int i = 1;
        for (UUID answerId : listAnswerId) {
            Answer answer = answerService.findAnswerToId(answerId);
            UUID part = answer.getQuestion().getPart().getPartId();
            if (!partInTopic.contains(part)) {
                partInTopic.add(part);
            }
            log.warn("STT: {},Answer ID: {}, Part ID: {}", i, answer.getAnswerId(), answer.getQuestion().getPart().getPartId());
            i++;
            DetailMockTest detailMockTest = new DetailMockTest(mockTest, answer);
            detailMockTest.setUserCreate(user);
            detailMockTest.setUserUpdate(user);
            detailMockTestRepository.save(detailMockTest);
            detailMockTestList.add(new DetailMockTestResponse(detailMockTest));
            if (answer.isCorrectAnswer()) {
                totalCorrect++;
                int questionScore = answer.getQuestion().getQuestionScore();
                totalScore += questionScore;

                int partIndex = getPartIndex(answer.getQuestion().getPart().getPartId());
                if (partIndex != -1) {
                    correctAnswers[partIndex]++;
                    scores[partIndex] += questionScore;
                }
            }
        }
        for (UUID partId : partInTopic) {
            int partIndex = getPartIndex(partId);
            if (partIndex != -1) {
                saveResultMockTest(mockTest, partId, correctAnswers[partIndex], scores[partIndex], user);
            }
        }

        mockTest.setScore(totalScore);
        mockTest.setCorrectAnswers(totalCorrect);
        mockTestRepository.save(mockTest);
        sendResultEmail(user.getEmail(), mockTest, totalCorrect, correctAnswers, scores, partInTopic);

        return detailMockTestList;
    }


    protected String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }


    protected void sendResultEmail(String email, MockTest mockTest, int correctAnswer, int[] corrects, int[] scores, List<UUID> listPartId) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String templateContent = readTemplateContent("test_email.html");

        templateContent = templateContent.replace("{{nameToeic}}", mockTest.getTopic().getTopicName());
        templateContent = templateContent.replace("{{userName}}", mockTest.getUser().getName());
        templateContent = templateContent.replace("{{correctAnswer}}", String.valueOf(correctAnswer));
        templateContent = templateContent.replace("{{score}}", String.valueOf(mockTest.getScore()));
        templateContent = templateContent.replace("{{timeAnswer}}", String.valueOf(mockTest.getTime()));

        StringBuilder partsHtml = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            if (listPartId.contains(getPartUUID(i))) {
                String partHtml = "<p>Số câu đúng Part " + (i + 1) + ": " + corrects[i] + "<br>Số điểm Part " + (i + 1) + ": " + scores[i] + "</p>";
                partsHtml.append(partHtml);
            }
        }

        templateContent = templateContent.replace("{{parts}}", partsHtml.toString());

        helper.setTo(email);
        helper.setSubject("Thông tin bài thi");
        helper.setText(templateContent, true);

        mailSender.send(message);
    }

    protected void saveResultMockTest(MockTest mockTest, UUID partUUID, int correctAnswers, int score, User user) {
        ResultMockTest resultMockTest = new ResultMockTest();
        resultMockTest.setMockTest(mockTest);
        resultMockTest.setPart(partService.getPartToId(partUUID));
        resultMockTest.setCorrectAnswer(correctAnswers);
        resultMockTest.setScore(score);
        resultMockTest.setCreateAt(LocalDateTime.now());
        resultMockTest.setUpdateAt(LocalDateTime.now());
        resultMockTest.setUserCreate(user);
        resultMockTest.setUserUpdate(user);
        resultMockTestRepository.save(resultMockTest);
    }


    protected int getPartIndex(UUID partId) {
        Map<UUID, Integer> partIdToIndexMap = Map.of(
                UUID.fromString("5e051716-1b41-4385-bfe6-3e350d5acb06"), 0, // Part 1
                UUID.fromString("9509bfa5-0403-48db-bee1-1af41cfc73df"), 1, // Part 2
                UUID.fromString("2496a543-49c3-4580-80b6-c9984e4142e1"), 2, // Part 3
                UUID.fromString("3b4d6b90-fc31-484e-afe3-3a21162b6454"), 3, // Part 4
                UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"), 4, // Part 5
                UUID.fromString("22b25c09-33db-4e3a-b228-37b331b39c96"), 5, // Part 6
                UUID.fromString("2416aa89-3284-4315-b759-f3f1b1d5ff3f"), 6  // Part 7
        );
        return partIdToIndexMap.getOrDefault(partId, -1);
    }

    protected UUID getPartUUID(int index) {
        List<UUID> partUUIDs = List.of(
                UUID.fromString("5e051716-1b41-4385-bfe6-3e350d5acb06"), // Part 1
                UUID.fromString("9509bfa5-0403-48db-bee1-1af41cfc73df"), // Part 2
                UUID.fromString("2496a543-49c3-4580-80b6-c9984e4142e1"), // Part 3
                UUID.fromString("3b4d6b90-fc31-484e-afe3-3a21162b6454"), // Part 4
                UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"), // Part 5
                UUID.fromString("22b25c09-33db-4e3a-b228-37b331b39c96"), // Part 6
                UUID.fromString("2416aa89-3284-4315-b759-f3f1b1d5ff3f")  // Part 7
        );
        return partUUIDs.get(index);
    }

    @Override
    public List<DetailMockTestResponse> getListCorrectAnswer(int index, boolean isCorrect, UUID mockTestId) {

        MockTest mockTest = findMockTestToId(mockTestId);

        List<DetailMockTest> detailMockTestList = getTop10DetailToCorrect(index, isCorrect, mockTest);

        if (isCorrect)
            MessageResponseHolder.setMessage("Get top 10 Answer correct successfully");
        else
            MessageResponseHolder.setMessage("Get top 10 Answer wrong successfully");

        return detailMockTestList.stream().map(DetailMockTestResponse::new).toList();
    }

    @SneakyThrows
    @Override
    public void sendEmailToMock(UUID mockTestId) {

        User user = userService.currentUser();

        MockTest mockTest = findMockTestToId(mockTestId);

        int correctAnswer = countCorrectAnswer(mockTestId);
        int[] corrects = new int[7];
        int[] scores = new int[7];
        List<UUID> listPartId = new ArrayList<>();

        List<ResultMockTest> resultMockTestList = resultMockTestRepository.findByMockTest_MockTestId(mockTest.getMockTestId());

        for (ResultMockTest resultMockTest : resultMockTestList) {
            int partIndex = getPartIndex(resultMockTest.getPart().getPartId());
            corrects[partIndex] = correctAnswer;
            scores[partIndex] = resultMockTest.getScore();
            listPartId.add(resultMockTest.getPart().getPartId());
        }

        sendResultEmail(user.getEmail(), mockTest, correctAnswer, corrects, scores, listPartId);
    }

    @Override
    public PartMockTestResponse getPartToMockTest(UUID mockTestId) {

        User currentUser = userService.currentUser();

        MockTest mockTest = findMockTestToId(mockTestId);

        if (!currentUser.equals(mockTest.getUser()))
            throw new BadRequestException("You cannot view other people's tests");

        PartMockTestResponse partMockTestResponse = PartMockTestResponse.builder()
                .topicId(mockTest.getTopic().getTopicId())
                .topicName(mockTest.getTopic().getTopicName())
                .topicTime(mockTest.getTopic().getWorkTime())
                .build();

        partMockTestResponse.setParts(
                mockTest.getTopic().getParts().stream()
                        .sorted(Comparator.comparing(Part::getCreateAt))
                        .map(
                        partItem -> {

                            int totalQuestion = topicService.totalQuestion(partItem, mockTest.getTopic().getTopicId());

                            PartResponse partResponse = PartMapper.INSTANCE.partEntityToPartResponse(partItem);

                            partResponse.setTotalQuestion(totalQuestion);

                            return partResponse;
                        }
                ).toList()
        );

        return partMockTestResponse;
    }

    @Override
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId) {

        User currentUser = userService.currentUser();

        MockTest mockTest = findMockTestToId(mockTestId);

        if (!currentUser.equals(mockTest.getUser()))
            throw new BadRequestException("You cannot view other people's tests");

        List<Question> questionList = topicService.getQuestionOfPartToTopic(mockTest.getTopic().getTopicId(), partId);

        List<QuestionMockTestResponse> questionMockTestResponseList = new ArrayList<>();

        for (Question question : questionList) {

            if (questionService.checkQuestionGroup(question.getQuestionId())) {
                QuestionMockTestResponse questionMockTestResponse = new QuestionMockTestResponse(question);

                List<Question> questionGroupList = questionService.listQuestionGroup(question);
                List<QuestionMockTestResponse> questionGroupResponseList = new ArrayList<>();
                for (Question questionGroup : questionGroupList) {
                    Answer answerCorrect = answerService.correctAnswer(questionGroup);
                    Answer answerChoice = answerService.choiceAnswer(questionGroup, mockTest);
                    QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(questionGroup, answerChoice, answerCorrect);
                    questionGroupResponseList.add(questionGroupResponse);
                    questionMockTestResponse.setQuestionGroup(questionGroupResponseList);
                }

                questionMockTestResponseList.add(questionMockTestResponse);
            } else {
                Answer answerCorrect = answerService.correctAnswer(question);
                Answer answerChoice = answerService.choiceAnswer(question, mockTest);
                QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(question, answerChoice, answerCorrect);
                questionMockTestResponseList.add(questionGroupResponse);
            }
        }

        return questionMockTestResponseList;
    }
}
