package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
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
    public Map<String, Integer> checkCorrectAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ) {

        return userAnswerService.scoreAnswer(questionId,userId);
    }


}
