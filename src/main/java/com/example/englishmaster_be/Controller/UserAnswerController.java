package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.UserAnswer;
import com.example.englishmaster_be.Model.UserBlankAnswer;
import com.example.englishmaster_be.Service.impl.UserAnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;


@Tag(name = "User Answer")
@RestController
@RequestMapping("/api/user-answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerController {

    UserAnswerService userAnswerService;



    @GetMapping("/check-blank")
    @MessageResponse("Answer Checking")
    public void checkAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerBlank(questionId,userId);
    }

    @GetMapping("/check-multiple-choice")
    @MessageResponse("Answer Checking")
    public void checkAnswer1(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerMultipleChoice(questionId,userId);
    }

    @GetMapping("/check-correct")
    @MessageResponse("Check answer successfully")
    public Map<String, Object> checkCorrectAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ) {

        return userAnswerService.scoreAnswer(questionId, userId);
    }


    @PostMapping("/create-user-answer")
    public ResponseEntity<ResponseModel> createUserAnswer(@RequestBody UserAnswerRequest request) {
        UserAnswer answer = userAnswerService.createUserAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .status(HttpStatus.OK)
                .responseData(answer)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @PostMapping("/create-user-answer-blank")
    public ResponseEntity<ResponseModel> createUserAnswerBlank(@RequestBody AnswerBlankRequest request) {
        UserBlankAnswer answer = userAnswerService.createUserBlankAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .status(HttpStatus.OK)
                .responseData(answer)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @PostMapping("/create-user-answer-matching")
    public ResponseEntity<ResponseModel> createUserAnswerMatching(@RequestBody AnswerMatchingRequest request) {
        Map<String, String> map= userAnswerService.createUserMatchingAnswer(request);
        ResponseModel responseModel = ResponseModel.builder()
                .message("Create user answer successfully")
                .responseData(map)
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }





}
