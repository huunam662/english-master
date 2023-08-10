package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
    @PreAuthorize("hasRole('USER')")
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
                responseModel.setStatus("success");
            } else {
                responseModel.setMessage("Had correct answer");
                responseModel.setStatus("success");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER')")
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
                responseModel.setStatus("success");
            } else {
                responseModel.setMessage("Had correct answer");
                responseModel.setStatus("success");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{answerId:.+}/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deleteAnswer(@PathVariable UUID answerId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Answer answer = IAnswerService.findAnswerToId(answerId);
            IAnswerService.deleteAnswer(answer);

            responseModel.setMessage("Delete answer successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{answerId:.+}/getDetailAnswer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getDetailAnswer(@PathVariable UUID answerId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Answer answer = IAnswerService.findAnswerToId(answerId);

            AnswerResponse answerResponse = new AnswerResponse(answer);

            responseModel.setMessage("Detail answer successfully");
            responseModel.setResponseData(answerResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Detail answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER')")
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
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Check answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

}
