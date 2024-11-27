package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseModel;
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
        ResponseModel responseModel=new ResponseModel();
        boolean check=service.checkCorrectAnswerBlank(questionId,userId);
        responseModel.setMessage("Answer Checking");
        responseModel.setStatus("success");
        responseModel.setResponseData(check);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/check-multiple-choice")
    public ResponseEntity<ResponseModel> checkAnswer1(@RequestParam(value = "user_id") UUID userId, @RequestParam(value = "question_id") UUID questionId){
        ResponseModel responseModel=new ResponseModel();
        boolean check= service.checkCorrectAnswerMultipleChoice(questionId,userId);
        responseModel.setResponseData(check);
        responseModel.setStatus("success");
        responseModel.setMessage("Answer Checking");
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
