package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.dto.answer.CreateListAnswerDTO;
import com.example.englishmaster_be.helper.GetExtension;
import com.example.englishmaster_be.dto.question.*;
import com.example.englishmaster_be.dto.*;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.service.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IPartService IPartService;
    @Autowired
    private IQuestionService IQuestionService;
    @Autowired
    private IContentService IContentService;
    @Autowired
    private IAnswerService IAnswerService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createQuestion(@RequestBody CreateQuestionDTO createQuestionDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = new Question(createQuestionDTO);

            User user = IUserService.currentUser();
            question.setUserCreate(user);
            question.setUserUpdate(user);

            question.setPart(IPartService.getPartToId(createQuestionDTO.getPartId()));

            IQuestionService.createQuestion(question);

            QuestionResponse questionResponse = new QuestionResponse(question);

            responseModel.setMessage("Create question successfully");
            responseModel.setResponseData(questionResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{questionId:.+}/uploadfile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> uploadFileQuestion(@PathVariable UUID questionId, @ModelAttribute UploadMultiFileDTO uploadMultiFileDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            Question question = IQuestionService.findQuestionById(questionId);
            Arrays.asList(uploadMultiFileDTO.getContentData()).stream().forEach(file -> {
                String filename = IFileStorageService.nameFile(file);

                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserCreate(user);
                content.setUserUpdate(user);

                question.setUpdateAt(LocalDateTime.now());
                question.setUserUpdate(user);
                question.getContentCollection().add(content);
                IContentService.uploadContent(content);
                IFileStorageService.save(file, filename);
            });

            IQuestionService.uploadFileQuestion(question);

            QuestionResponse questionResponse = new QuestionResponse(question);
            responseModel.setMessage("Upload question successfully");
            responseModel.setResponseData(questionResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Upload file question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{questionId:.+}/updatefile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateFileQuestion(@PathVariable UUID questionId, @ModelAttribute UploadFileDTO uploadFileDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            String filename = IFileStorageService.nameFile(uploadFileDTO.getContentData());
            Content content = IContentService.getContentToContentId(questionId);

            IFileStorageService.delete(content.getContentData());

            content.setContentData(filename);
            content.setContentType(GetExtension.typeFile(filename));
            content.setUpdateAt(LocalDateTime.now());
            content.setUserUpdate(user);

            IContentService.uploadContent(content);

            Question question = IContentService.getContentToContentId(questionId).getQuestion();
            question.setUpdateAt(LocalDateTime.now());
            question.setUserUpdate(user);

            IQuestionService.uploadFileQuestion(question);
            IFileStorageService.save(uploadFileDTO.getContentData(), filename);

            QuestionResponse questionResponse = new QuestionResponse(question);
            responseModel.setMessage("Update file question successfully");
            responseModel.setResponseData(questionResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update file question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/create/groupQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createGroupQuestion(@RequestBody CreateGroupQuestionDTO createGroupQuestionDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = new Question(createGroupQuestionDTO.getQuestionContent(), createGroupQuestionDTO.getQuestionScore());

            User user = IUserService.currentUser();
            question.setQuestionGroup(IQuestionService.findQuestionById(createGroupQuestionDTO.getQuestionGroupId()));
            question.setQuestionNumberical(createGroupQuestionDTO.getQuestionNumberical());
            question.setQuestionExplainVn(createGroupQuestionDTO.getQuestionExplainVn());
            question.setQuestionExplainEn(createGroupQuestionDTO.getQuestionExplainEn());
            question.setUserCreate(user);
            question.setUserUpdate(user);

            question.setPart(IPartService.getPartToId(IQuestionService.findQuestionById(createGroupQuestionDTO.getQuestionGroupId()).getPart().getPartId()));

            IQuestionService.createQuestion(question);

            QuestionResponse questionResponse = new QuestionResponse(question);

            responseModel.setMessage("Create question successfully");
            responseModel.setResponseData(questionResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @GetMapping(value = "/{partId:.+}/listTop10Question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getTop10Question(@PathVariable UUID partId, @RequestParam int indexp) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Question> questionList = IQuestionService.getTop10Question(indexp, partId);

            List<QuestionResponse> questionResponseList = new ArrayList<>();

            for (Question question : questionList) {
                QuestionResponse questionResponse = new QuestionResponse(question);
                questionResponseList.add(questionResponse);
            }

            responseModel.setMessage("List top 10 question successfully");
            responseModel.setResponseData(questionResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List top 10 question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{questionId:.+}/checkQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> checkQuestionGroup(@PathVariable UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            boolean check = IQuestionService.checkQuestionGroup(IQuestionService.findQuestionById(questionId));
            if (check) {
                responseModel.setMessage("Question has question group");

            } else {
                exceptionResponseModel.setMessage("Question doesn't have question group");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            exceptionResponseModel.setMessage("Don't check question " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{questionId:.+}/listQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getQuestionGroupToQuestion(@PathVariable UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            List<Question> questionList = IQuestionService.listQuestionGroup(question);
            JSONArray questionArray = new JSONArray();

            for (Question questionItem : questionList) {
                JSONObject questionObject = new JSONObject();
                questionObject.put("questionId", questionItem.getQuestionId());
                questionObject.put("partId", questionItem.getPart().getPartId());
                questionObject.put("questionGroup", questionItem.getQuestionGroup().getQuestionId());
                questionArray.add(questionObject);
            }

            responseModel.setMessage("List question group successfully");
            responseModel.setResponseData(questionArray);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List question group fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getAnswerToQuestion(@PathVariable UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            Collection<Answer> answerCollection = question.getAnswers();

            JSONArray answerArray = new JSONArray();
            for (Answer answer : answerCollection) {
                JSONObject answerObject = new JSONObject();
                answerObject.put("answerId", answer.getAnswerId());
                answerArray.add(answerObject);

            }

            responseModel.setMessage("List answer to question successfully");
            responseModel.setResponseData(answerArray);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List answer to question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{questionId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteQuestion(@PathVariable UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = IQuestionService.findQuestionById(questionId);
            IQuestionService.deleteQuestion(question);
            for (Content content : question.getContentCollection()) {
                IFileStorageService.delete(content.getContentData());
            }

            responseModel.setMessage("Delete question successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{questionId:.+}/editQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> editQuestion(@PathVariable UUID questionId, @ModelAttribute CreateQuestionDTO createQuestionDTO) {
        ResponseModel responseModel = new ResponseModel();

        User user = IUserService.currentUser();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            question.setQuestionContent(createQuestionDTO.getQuestionContent());
            question.setQuestionScore(createQuestionDTO.getQuestionScore());
            question.setUserUpdate(user);

            IQuestionService.createQuestion(question);


            if (createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()) {
                for (CreateListAnswerDTO createListAnswerDTO : createQuestionDTO.getListAnswer()) {
                    Answer answer = IAnswerService.findAnswerToId(createListAnswerDTO.getIdAnswer());
                    answer.setQuestion(question);
                    answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                    answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                    answer.setUserUpdate(user);

                    IAnswerService.createAnswer(answer);
                }
            }

            if (createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty()) {
                for (CreateQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                    Question questionChild = IQuestionService.findQuestionById(createQuestionChildDTO.getQuestionId());
                    questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                    questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                    questionChild.setUserUpdate(user);

                    for (CreateListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
                        Answer answer = IAnswerService.findAnswerToId(createListAnswerDTO.getIdAnswer());
                        answer.setQuestion(questionChild);
                        answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                        answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                        answer.setUserUpdate(user);

                        IAnswerService.createAnswer(answer);

                    }
                    IQuestionService.createQuestion(questionChild);
                }
            }

            if (createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()) {
                for (Content content : question.getContentCollection()) {
                    if (content.getContentType().equals("IMAGE")) {
                        question.getContentCollection().remove(content);
                        IContentService.delete(IContentService.getContentToContentId(content.getContentId()));
                        IFileStorageService.delete(content.getContentData());

                    }
                }
                String filename = IFileStorageService.nameFile(createQuestionDTO.getContentImage());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserUpdate(user);
                content.setUserCreate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                IContentService.uploadContent(content);
                IFileStorageService.save(createQuestionDTO.getContentImage(), filename);
            }
            if (createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()) {
                for (Content content : question.getContentCollection()) {
                    if (content.getContentType().equals("AUDIO")) {
                        question.getContentCollection().remove(content);
                        IContentService.delete(IContentService.getContentToContentId(content.getContentId()));
                        IFileStorageService.delete(content.getContentData());
                    }
                }
                String filename = IFileStorageService.nameFile(createQuestionDTO.getContentAudio());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserUpdate(user);
                content.setUserCreate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                IContentService.uploadContent(content);
                IFileStorageService.save(createQuestionDTO.getContentAudio(), filename);
            }

            IQuestionService.createQuestion(question);

            Question question1 = IQuestionService.findQuestionById(questionId);
            QuestionResponse questionResponse = new QuestionResponse(question1);

            if (IQuestionService.checkQuestionGroup(question1)) {
                List<Question> questionGroupList = IQuestionService.listQuestionGroup(question1);
                List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                for (Question questionGroup : questionGroupList) {
                    QuestionResponse questionGroupResponse;

                    Answer answerCorrect = IAnswerService.correctAnswer(questionGroup);
                    questionGroupResponse = new QuestionResponse(questionGroup, answerCorrect);
                    questionGroupResponseList.add(questionGroupResponse);
                    questionResponse.setQuestionGroup(questionGroupResponseList);
                }
            } else {
                Answer answerCorrect = IAnswerService.correctAnswer(question1);
                questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
            }
            responseModel.setResponseData(questionResponse);

            responseModel.setMessage("Update question to topic successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update question to topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{questionId:.+}/content")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getContentToQuestion(@PathVariable UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);

            QuestionResponse questionResponse = new QuestionResponse(question);

            responseModel.setMessage("Show content question successfully");
            responseModel.setResponseData(questionResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show content question fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

}
