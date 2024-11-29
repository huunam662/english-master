package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.impl.UserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-answer")
@RequiredArgsConstructor
public class UserAnswerController {
    private final UserAnswerService service;


    @GetMapping("/check-blank")
    public ResponseEntity<ResponseModel> checkAnswer(@RequestParam(value = "user_id") UUID userId, @RequestParam(value = "question_id") UUID questionId){

        service.checkCorrectAnswerBlank(questionId,userId);

        ResponseModel responseModel = ResponseModel.builder()
                .message("Answer Checking")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/check-multiple-choice")
    public ResponseEntity<ResponseModel> checkAnswer1(@RequestParam(value = "user_id") UUID userId, @RequestParam(value = "question_id") UUID questionId){

        service.checkCorrectAnswerMultipleChoice(questionId,userId);

        ResponseModel responseModel = ResponseModel.builder()
                .message("Answer Checking")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/check-correct")
    public ResponseEntity<ResponseModel> checkCorrectAnswer(@RequestParam(value = "user_id") UUID userId, @RequestParam(value = "question_id") UUID questionId) {
        Map<String, Integer> score =service.scoreAnswer(questionId,userId);
        ResponseModel responseModel= new ResponseModel();
        responseModel.setStatus(HttpStatus.OK);
        responseModel.setMessage("Check answer successfully");
        responseModel.setResponseData(score);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/check-correct")
    public ResponseEntity<ResponseModel> checkCorrectAnswer(@RequestParam(value = "user_id") UUID userId, @RequestParam(value = "question_id") UUID questionId) {
        Map<String, Integer> score =service.scoreAnswer(questionId,userId);
        ResponseModel responseModel= new ResponseModel();
        responseModel.setStatus("success");
        responseModel.setMessage("Check answer successfully");
        responseModel.setResponseData(score);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }




}
