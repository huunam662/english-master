package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.MockTest.CreateMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.MockTestRepository;
import com.example.englishmaster_be.Service.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createMockTest(@RequestBody CreateMockTestDTO createMockTestDTO){
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listTop10MockTest(@RequestParam int index){
        ResponseModel responseModel = new ResponseModel();
        try {
            List<MockTest> listMockTest = IMockTestService.getTop10MockTest(index);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for(MockTest mockTest : listMockTest){
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

    @GetMapping(value = "/{userId:.+}/listTestToUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listMockTestToUser(@RequestParam int index, @PathVariable UUID userId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.findUserById(userId);

            List<MockTest> listMockTest = IMockTestService.getTop10MockTestToUser(index, user);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for(MockTest mockTest : listMockTest){
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> addAnswerToMockTest(@PathVariable UUID mockTestId, @RequestBody List<UUID> listAnswerId  ){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            int totalCorrect = 0;
            int score = 0;

            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);
            List<DetailMockTestResponse> detailMockTestList = new ArrayList<>();
            for(UUID answerId : listAnswerId){
                Answer answer = IAnswerService.findAnswerToId(answerId);

                DetailMockTest detailMockTest = new DetailMockTest(mockTest, answer);
                detailMockTest.setUserCreate(user);
                detailMockTest.setUserUpdate(user);

                IMockTestService.createDetailMockTest(detailMockTest);

                DetailMockTestResponse detailMockTestResponse = new DetailMockTestResponse(detailMockTest);
                if(answer.isCorrectAnswer()){
                    totalCorrect++;
                    score = score + answer.getQuestion().getQuestionScore();
                }
                detailMockTestList.add(detailMockTestResponse);
            }

            mockTest.setScore(score);
            mockTestRepository.save(mockTest);

            sendResultEmail(user.getEmail(), mockTest, totalCorrect);
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

    @GetMapping(value = "/{mockTestId:.+}/listCorrectAnswer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listCorrectAnswer(@RequestParam int index, @RequestParam boolean isCorrect, @PathVariable UUID mockTestId){
        ResponseModel responseModel = new ResponseModel();
        try {
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            List<DetailMockTest> detailMockTestList = IMockTestService.getTop10DetailToCorrect(index, isCorrect, mockTest);

            if(detailMockTestList != null){
                List<DetailMockTestResponse> detailMockTestResponseList = new ArrayList<>();
                for(DetailMockTest detailMockTest : detailMockTestList){
                    DetailMockTestResponse detailMockTestResponse = new DetailMockTestResponse(detailMockTest);
                    detailMockTestResponseList.add(detailMockTestResponse);
                }
                responseModel.setResponseData(detailMockTestResponseList);
            }

            if(isCorrect){
                responseModel.setMessage("Get top 10 answer correct successfully");
            }else {
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> sendEmailToMock(@PathVariable UUID mockTestId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            int correctAnswer = IMockTestService.countCorrectAnswer(mockTestId);

            sendResultEmail(user.getEmail(), mockTest, correctAnswer);

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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getPartToMockTest(@PathVariable UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User currentUser = IUserService.currentUser();

            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            if(!currentUser.equals(mockTest.getUser())){
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

            for(Part part : partList ){
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getQuestionOfToMockTest(@PathVariable UUID mockTestId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User currentUser = IUserService.currentUser();

            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            if(!currentUser.equals(mockTest.getUser())){
                responseModel.setMessage("You cannot view other people's tests");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            List<Question> questionList = ITopicService.getQuestionOfPartToTopic(mockTest.getTopic().getTopicId(), partId);
            List<QuestionMockTestResponse> questionMockTestResponseList = new ArrayList<>();

            for(Question question : questionList ){

                if(IQuestionService.checkQuestionGroup(question)){
                    QuestionMockTestResponse questionMockTestResponse = new QuestionMockTestResponse(question);

                    List<Question> questionGroupList = IQuestionService.listQuestionGroup(question);
                    List<QuestionMockTestResponse> questionGroupResponseList = new ArrayList<>();
                    for(Question questionGroup : questionGroupList){
                        Answer answerCorrect = IAnswerService.correctAnswer(questionGroup);
                        Answer answerChoice = IAnswerService.choiceAnswer(questionGroup, mockTest);
                        QuestionMockTestResponse questionGroupResponse = new QuestionMockTestResponse(questionGroup, answerChoice, answerCorrect);
                        questionGroupResponseList.add(questionGroupResponse);
                        questionMockTestResponse.setQuestionGroup(questionGroupResponseList);
                    }

                    questionMockTestResponseList.add(questionMockTestResponse);
                }else {
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

    private void sendResultEmail(String email, MockTest mockTest, int correctAnswer) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        String templateContent = readTemplateContent("test_email.html");
        templateContent = templateContent.replace("{{nameToeic}}", mockTest.getTopic().getTopicName());
        templateContent = templateContent.replace("{{userName}}", mockTest.getUser().getName());
        templateContent = templateContent.replace("{{correctAnswer}}", String.valueOf(correctAnswer));
        templateContent = templateContent.replace("{{score}}", String.valueOf(mockTest.getScore()));
        templateContent = templateContent.replace("{{timeAnswer}}", String.valueOf(mockTest.getTime()));

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
