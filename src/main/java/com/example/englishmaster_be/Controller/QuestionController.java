package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.DTO.Question.*;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONArray;
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
    @PreAuthorize("hasRole('USER')")
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
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/uploadfile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> uploadFileQuestion(@ModelAttribute UploadMultiFileDTO uploadMultiFileDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            Question question = IQuestionService.findQuestionById(uploadMultiFileDTO.getId());
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
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Upload file question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/updatefile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> updateFileQuestion(@ModelAttribute UploadFileDTO uploadFileDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            String filename = IFileStorageService.nameFile(uploadFileDTO.getContentData());
            Content content = IContentService.getContentToContentId(uploadFileDTO.getId());

            IFileStorageService.delete(content.getContentData());

            content.setContentData(filename);
            content.setContentType(GetExtension.typeFile(filename));
            content.setUpdateAt(LocalDateTime.now());
            content.setUserUpdate(user);

            IContentService.uploadContent(content);

            Question question = IContentService.getContentToContentId(uploadFileDTO.getId()).getQuestion();
            question.setUpdateAt(LocalDateTime.now());
            question.setUserUpdate(user);

            IQuestionService.uploadFileQuestion(question);
            IFileStorageService.save(uploadFileDTO.getContentData(), filename);

            QuestionResponse questionResponse = new QuestionResponse(question);
            responseModel.setMessage("Update file question successfully");
            responseModel.setResponseData(questionResponse);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update file question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/create/groupQuestion")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createGroupQuestion(@RequestBody CreateGroupQuestionDTO createGroupQuestionDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = new Question(createGroupQuestionDTO.getQuestionContent(), createGroupQuestionDTO.getQuestionScore());

            User user = IUserService.currentUser();
            question.setQuestionGroup(IQuestionService.findQuestionById(createGroupQuestionDTO.getQuestionGroupId()));
            question.setQuestionNumberical(createGroupQuestionDTO.getQuestionNumberical());
            question.setUserCreate(user);
            question.setUserUpdate(user);

            question.setPart(IPartService.getPartToId(IQuestionService.findQuestionById(createGroupQuestionDTO.getQuestionGroupId()).getPart().getPartId()));

            IQuestionService.createQuestion(question);

            QuestionResponse questionResponse = new QuestionResponse(question);

            responseModel.setMessage("Create question successfully");
            responseModel.setResponseData(questionResponse);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    @GetMapping(value = "/listTop10Question")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getTop10Question(@RequestParam int indexp, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Question> questionList = IQuestionService.getTop10Question(indexp, partId);

            List<QuestionResponse> questionResponseList = new ArrayList<>();

            for(Question question : questionList){
                QuestionResponse questionResponse = new QuestionResponse(question);
                questionResponseList.add(questionResponse);
            }

            responseModel.setMessage("List top 10 question successfully");
            responseModel.setResponseData(questionResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("List top 10 question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/checkQuestionGroup")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> checkQuestionGroup(@RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            boolean check = IQuestionService.checkQuestionGroup(IQuestionService.findQuestionById(questionId));
            if(check){
                responseModel.setMessage("Question has question group");
                responseModel.setResponseData(true);
                responseModel.setStatus("success");
            }else {
                responseModel.setMessage("Question doesn't have question group");
                responseModel.setResponseData(false);
                responseModel.setStatus("success");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Don't check question " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listQuestionGroup")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getQuestionGroupToQuestion(@RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            List<Question> questionList = IQuestionService.listQuestionGroup(question);

            List<QuestionResponse> questionResponseList = new ArrayList<>();

            for(Question questionItem : questionList){
                QuestionResponse questionResponse = new QuestionResponse(questionItem);
                questionResponseList.add(questionResponse);
            }

            responseModel.setMessage("List question group successfully");
            responseModel.setResponseData(questionResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("List question group fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listAnswer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getAnswerToQuestion(@RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            Collection<Answer> answerCollection = question.getAnswers();

            JSONArray answerArray = new JSONArray();
            for(Answer answer : answerCollection){
                AnswerResponse answerResponse = new AnswerResponse(answer);
                answerArray.add(answerResponse);

            }

            responseModel.setMessage("List answer to question successfully");
            responseModel.setResponseData(answerArray);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("List answer to question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/deleteQuestion")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deleteQuestion(@RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = IQuestionService.findQuestionById(questionId);
            IQuestionService.deleteQuestion(question);
            for(Content content : question.getContentCollection()){
                IFileStorageService.delete(content.getContentData());
            }

            responseModel.setMessage("Delete question successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete question fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
