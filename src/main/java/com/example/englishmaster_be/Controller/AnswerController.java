package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

            Question question = IQuestionService.findQuestionById(createAnswerDTO.getQuestionId());
            Answer answer = new Answer(createAnswerDTO);
            answer.setQuestion(question);
            answer.setUserCreate(user);
            answer.setUserUpdate(user);

            IAnswerService.createAnswer(answer);

            AnswerResponse answerResponse = new AnswerResponse(answer);

            responseModel.setMessage("Create answer successfully");

            responseModel.setResponseData(answerResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create answer fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

}
