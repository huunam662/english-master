package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.answer.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {
    @Autowired
    private IAnswerService IAnswerService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IQuestionService IQuestionService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createAnswer(@RequestBody CreateAnswerDTO createAnswerDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            boolean check = true;
            Question question = IQuestionService.findQuestionById(createAnswerDTO.getQuestionId());

            for (Answer answerCheck : question.getAnswers()) {
                if (answerCheck.isCorrectAnswer() && createAnswerDTO.isCorrectAnswer()) {
                    check = false;
                }
            }
            if (check) {
                Answer answer = new Answer(createAnswerDTO);
                answer.setQuestion(question);
                answer.setUserCreate(user);
                answer.setUserUpdate(user);

                IAnswerService.createAnswer(answer);

                AnswerResponse answerResponse = new AnswerResponse(answer);

                responseModel.setMessage("Create answer successfully");
                responseModel.setResponseData(answerResponse);
            } else {
                responseModel.setMessage("Had correct answer");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("Create answer fail: " + e.getMessage());
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateAnswer(@PathVariable UUID answerId, @RequestBody UpdateAnswerDTO updateAnswerDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            boolean check = true;
            Question question = IQuestionService.findQuestionById(updateAnswerDTO.getQuestionId());

            for (Answer answerCheck : question.getAnswers()) {
                if (answerCheck.isCorrectAnswer() && updateAnswerDTO.isCorrectAnswer() && !answerCheck.getAnswerId().equals(answerId)) {
                    check = false;
                }
            }
            if (check) {
                Answer answer = IAnswerService.findAnswerToId(answerId);
                answer.setAnswerContent(updateAnswerDTO.getAnswerContent());
                answer.setCorrectAnswer(updateAnswerDTO.isCorrectAnswer());
                answer.setExplainDetails(updateAnswerDTO.getExplainDetails());
                answer.setQuestion(question);
                answer.setUpdateAt(LocalDateTime.now());
                answer.setUserUpdate(user);

                IAnswerService.createAnswer(answer);

                AnswerResponse answerResponse = new AnswerResponse(answer);

                responseModel.setMessage("Update answer successfully");
                responseModel.setResponseData(answerResponse);
            } else {
                responseModel.setMessage("Had correct answer");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("Update answer fail: " + e.getMessage());
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @DeleteMapping(value = "/{answerId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteAnswer(@PathVariable UUID answerId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Answer answer = IAnswerService.findAnswerToId(answerId);
            IAnswerService.deleteAnswer(answer);

            responseModel.setMessage("Delete answer successfully");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("Delete answer fail: " + e.getMessage());
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @GetMapping(value = "/{answerId:.+}/getDetailAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getDetailAnswer(@PathVariable UUID answerId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Answer answer = IAnswerService.findAnswerToId(answerId);

            AnswerResponse answerResponse = new AnswerResponse(answer);

            responseModel.setMessage("Detail answer successfully");
            responseModel.setResponseData(answerResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("Detail answer fail: " + e.getMessage());
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> checkCorrectAnswer(@PathVariable UUID answerId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            boolean check = IAnswerService.checkCorrectAnswer(answerId);
            int score = IAnswerService.scoreAnswer(answerId);
            JSONObject responseObject = new JSONObject();
            responseObject.put("correctAnswer", check);
            responseObject.put("scoreAnswer", score);

            responseModel.setMessage("Check answer successfully");
            responseModel.setResponseData(responseObject);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("Check answer fail: " + e.getMessage());
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

}
