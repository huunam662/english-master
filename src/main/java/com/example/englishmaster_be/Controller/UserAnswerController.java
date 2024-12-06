package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.UserAnswer;
import com.example.englishmaster_be.Model.UserBlankAnswer;
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


//    @GetMapping("/check-blank")
//    public ResponseEntity<ResponseModel> checkAnswer(@RequestParam(value = "question_id") UUID questionId){
//
//        service.checkCorrectAnswerBlank(questionId);
//
//        ResponseModel responseModel = ResponseModel.builder()
//                .message("Answer Checking")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
//    }
//
//    @GetMapping("/check-multiple-choice")
//    public ResponseEntity<ResponseModel> checkAnswer1(@RequestParam(value = "question_id") UUID questionId){
//
//        service.checkCorrectAnswerMultipleChoice(questionId);
//
//        ResponseModel responseModel = ResponseModel.builder()
//                .message("Answer Checking")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
//    }

    @GetMapping("/check-correct")
    public ResponseEntity<ResponseModel> checkCorrectAnswer(@RequestParam(value = "question_id") UUID questionId) {
        Map<String, Integer> score=service.scoreAnswer(questionId);
        ResponseModel responseModel= ResponseModel.builder()
                .message("Check answer successfully")
                .status(HttpStatus.OK)
                .responseData(score)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PostMapping("/create-user-answer")
    public ResponseEntity<ResponseModel> createUserAnswer(@RequestBody UserAnswerRequest request) {
        UserAnswer answer = service.createUserAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .status(HttpStatus.OK)
                .responseData(answer)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @PostMapping("/create-user-answer-blank")
    public ResponseEntity<ResponseModel> createUserAnswerBlank(@RequestBody AnswerBlankRequest request) {
        UserBlankAnswer answer = service.createUserBlankAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .status(HttpStatus.OK)
                .responseData(answer)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @PostMapping("/create-user-answer-matching")
    public ResponseEntity<ResponseModel> createUserAnswerMatching(@RequestBody AnswerMatchingRequest request) {
        Map<String, String> map= service.createUserMatchingAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .responseData(map)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }









}
