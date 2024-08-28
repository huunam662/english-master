package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.MockTest.CreateMockTestDTO;
import com.example.englishmaster_be.DTO.MockTest.CreateResultMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.MockTestRepository;
import com.example.englishmaster_be.Repository.ResultMockTestRepository;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/mockTest")
@SuppressWarnings("unchecked")
public class MockTestController {
    @Autowired
    private MockTestRepository mockTestRepository;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IQuestionService IQuestionService;
    @Autowired
    private IMockTestService IMockTestService;
    @Autowired
    private IAnswerService IAnswerService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ResultMockTestRepository resultMockTestRepository;

    @Autowired
    private IPartService IPartService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createMockTest(@RequestBody CreateMockTestDTO createMockTestDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = new MockTest(createMockTestDTO);

            mockTest.setTopic(ITopicService.findTopicById(createMockTestDTO.getTopic_id()));
            mockTest.setUser(user);
            mockTest.setUserCreate(user);
            mockTest.setUserUpdate(user);

            IMockTestService.createMockTest(mockTest);

            MockTestResponse mockTestResponse = new MockTestResponse(mockTest);

            responseModel.setMessage("Create mock test successfully");

            responseModel.setResponseData(mockTestResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listMockTest")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listTop10MockTest(@RequestParam int index) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<MockTest> listMockTest = IMockTestService.getTop10MockTest(index);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for (MockTest mockTest : listMockTest) {
                MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
                mockTestResponseList.add(mockTestResponse);
            }


            responseModel.setMessage("Get top 10 mock test successfully");

            responseModel.setResponseData(mockTestResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listMockTestAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listMockTestOfAdmin(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                             @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
                                                             @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                             @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection) {
        ResponseModel responseModel = new ResponseModel();
        try {
            JSONObject responseObject = new JSONObject();
            OrderSpecifier<?> orderSpecifier;


            if (Sort.Direction.DESC.equals(sortDirection)) {
                orderSpecifier = QMockTest.mockTest.updateAt.desc();
            } else {
                orderSpecifier = QMockTest.mockTest.updateAt.asc();
            }

            JPAQuery<MockTest> query = queryFactory.selectFrom(QMockTest.mockTest)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalRecords", totalRecords);
            responseObject.put("totalPages", totalPages);

            List<MockTest> mockTestList = query.fetch();

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();

            for (MockTest mockTest : mockTestList) {
                MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
                mockTestResponseList.add(mockTestResponse);
            }

            responseObject.put("listMockTest", mockTestResponseList);
            responseModel.setMessage("List mock test successful");
            responseModel.setResponseData(responseObject);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("List mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{userId:.+}/listTestToUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listMockTestToUser(@RequestParam int index, @PathVariable UUID userId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.findUserById(userId);

            List<MockTest> listMockTest = IMockTestService.getTop10MockTestToUser(index, user);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for (MockTest mockTest : listMockTest) {
                MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
                mockTestResponseList.add(mockTestResponse);
            }


            responseModel.setMessage("Get top 10 mock test of user successfully");
            responseModel.setResponseData(mockTestResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test of user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/{mockTestId:.+}/submitResult")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addAnswerToMockTest(@PathVariable UUID mockTestId, @RequestBody List<UUID> listAnswerId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);
            List<DetailMockTestResponse> detailMockTestList = new ArrayList<>();

            int totalCorrect = 0;
            int totalScore = 0;

            // Arrays to store scores and correct answers for each part
            int[] correctAnswers = new int[7];
            int[] scores = new int[7];

            List<UUID> partInTopic = new ArrayList<>();

            for (UUID answerId : listAnswerId) {
                Answer answer = IAnswerService.findAnswerToId(answerId);
                UUID part = answer.getQuestion().getPart().getPartId();
                if (!partInTopic.contains(part)) {
                    partInTopic.add(part);
                }
                DetailMockTest detailMockTest = new DetailMockTest(mockTest, answer);
                detailMockTest.setUserCreate(user);
                detailMockTest.setUserUpdate(user);
                IMockTestService.createDetailMockTest(detailMockTest);

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

            // Save the results for each part that is in the topic
            for (UUID partId : partInTopic) {
                int partIndex = getPartIndex(partId);
                if (partIndex != -1) {
                    saveResultMockTest(mockTest, partId, correctAnswers[partIndex], scores[partIndex], user);
                }
            }

            mockTest.setScore(totalScore);
            mockTest.setCorrectAnswers(totalCorrect);
            mockTestRepository.save(mockTest);

            // Pass the arrays to sendResultEmail method
            sendResultEmail(user.getEmail(), mockTest, totalCorrect, correctAnswers, scores, partInTopic);

            responseModel.setMessage("Create detail mock test successfully");
            responseModel.setResponseData(detailMockTestList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create detail mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    private int getPartIndex(UUID partId) {
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

    private UUID getPartUUID(int index) {
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

    private void saveResultMockTest(MockTest mockTest, UUID partUUID, int correctAnswers, int score, User user) {
        ResultMockTest resultMockTest = new ResultMockTest();
        resultMockTest.setMockTest(mockTest);
        resultMockTest.setPart(IPartService.getPartToId(partUUID));
        resultMockTest.setCorrectAnswer(correctAnswers);
        resultMockTest.setScore(score);
        resultMockTest.setCreateAt(LocalDateTime.now());
        resultMockTest.setUpdateAt(LocalDateTime.now());
        resultMockTest.setUserCreate(user);
        resultMockTest.setUserUpdate(user);
        resultMockTestRepository.save(resultMockTest);
    }


    @GetMapping(value = "/{mockTestId:.+}/listCorrectAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listCorrectAnswer(@RequestParam int index, @RequestParam boolean isCorrect, @PathVariable UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            List<DetailMockTest> detailMockTestList = IMockTestService.getTop10DetailToCorrect(index, isCorrect, mockTest);

            if (detailMockTestList != null) {
                List<DetailMockTestResponse> detailMockTestResponseList = new ArrayList<>();
                for (DetailMockTest detailMockTest : detailMockTestList) {
                    DetailMockTestResponse detailMockTestResponse = new DetailMockTestResponse(detailMockTest);
                    detailMockTestResponseList.add(detailMockTestResponse);
                }
                responseModel.setResponseData(detailMockTestResponseList);
            }

            if (isCorrect) {
                responseModel.setMessage("Get top 10 answer correct successfully");
            } else {
                responseModel.setMessage("Get top 10 answer wrong successfully");
            }
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test of user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{mockTestId:.+}/sendEmail")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> sendEmailToMock(@PathVariable UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            int correctAnswer = IMockTestService.countCorrectAnswer(mockTestId);
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

            responseModel.setMessage("Send email successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Send email fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{mockTestId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getPartToMockTest(@PathVariable UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User currentUser = IUserService.currentUser();

            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            if (!currentUser.equals(mockTest.getUser())) {
                responseModel.setMessage("You cannot view other people's tests");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            List<Part> partList = mockTest.getTopic().getParts().stream().sorted(Comparator.comparing(Part::getCreateAt)).toList();

            JSONObject responseObject = new JSONObject();
            JSONArray responseArray = new JSONArray();

            responseObject.put("TopicName", mockTest.getTopic().getTopicName());
            responseObject.put("TopicTime", mockTest.getTopic().getWorkTime());
            responseObject.put("TopicId", mockTest.getTopic().getTopicId());

            for (Part part : partList) {
                int totalQuestion = ITopicService.totalQuestion(part, mockTest.getTopic().getTopicId());

                PartResponse partResponse = new PartResponse(part, totalQuestion);
                responseArray.add(partResponse);
            }

            responseObject.put("Part", responseArray);
            responseModel.setMessage("Show part to mock test successfully");

            responseModel.setResponseData(responseObject);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show part to mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{mockTestId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getQuestionOfToMockTest(@PathVariable UUID mockTestId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User currentUser = IUserService.currentUser();

            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            if (!currentUser.equals(mockTest.getUser())) {
                responseModel.setMessage("You cannot view other people's tests");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            List<Question> questionList = ITopicService.getQuestionOfPartToTopic(mockTest.getTopic().getTopicId(), partId);
            List<QuestionMockTestResponse> questionMockTestResponseList = new ArrayList<>();

            for (Question question : questionList) {

                if (IQuestionService.checkQuestionGroup(question)) {
                    QuestionMockTestResponse questionMockTestResponse = new QuestionMockTestResponse(question);

                    List<Question> questionGroupList = IQuestionService.listQuestionGroup(question);
                    List<QuestionMockTestResponse> questionGroupResponseList = new ArrayList<>();
                    for (Question questionGroup : questionGroupList) {
                        Answer answerCorrect = IAnswerService.correctAnswer(questionGroup);
                        Answer answerChoice = IAnswerService.choiceAnswer(questionGroup, mockTest);
                        QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(questionGroup, answerChoice, answerCorrect);
                        questionGroupResponseList.add(questionGroupResponse);
                        questionMockTestResponse.setQuestionGroup(questionGroupResponseList);
                    }

                    questionMockTestResponseList.add(questionMockTestResponse);
                } else {
                    Answer answerCorrect = IAnswerService.correctAnswer(question);
                    Answer answerChoice = IAnswerService.choiceAnswer(question, mockTest);
                    QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(question, answerChoice, answerCorrect);
                    questionMockTestResponseList.add(questionGroupResponse);
                }
            }

            responseModel.setMessage("Show question of part to mock test successfully");
            responseModel.setResponseData(questionMockTestResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show question of part to mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    private void sendResultEmail(String email, MockTest mockTest, int correctAnswer, int[] corrects, int[] scores, List<UUID> listPartId) throws IOException, MessagingException {
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
                String partHtml = "<p>Số câu đúng part " + (i + 1) + ": " + corrects[i] + "<br>Số điểm part " + (i + 1) + ": " + scores[i] + "</p>";
                partsHtml.append(partHtml);
            }
        }

        templateContent = templateContent.replace("{{parts}}", partsHtml.toString());

        helper.setTo(email);
        helper.setSubject("Thông tin bài thi");
        helper.setText(templateContent, true);

        mailSender.send(message);
    }


    private String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }
}
