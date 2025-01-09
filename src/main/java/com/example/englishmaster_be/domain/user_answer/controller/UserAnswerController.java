package com.example.englishmaster_be.domain.user_answer.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerResponse;
import com.example.englishmaster_be.domain.user_answer.service.IUserAnswerService;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerScoreResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "User Answer")
@RestController
@RequestMapping("/user-answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerController {

    IUserAnswerService userAnswerService;



    @GetMapping("/check-blank")
    @DefaultMessage("Answer Checked")
    public void checkAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerBlank(questionId,userId);
    }

    @GetMapping("/check-multiple-choice")
    @DefaultMessage("Answer Checked")
    public void checkAnswerMultipleChoice(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerMultipleChoice(questionId,userId);
    }

    @GetMapping("/check-correct")
    @DefaultMessage("Check answer successfully")
    public UserAnswerScoreResponse checkCorrectAnswer(
            @RequestParam(value = "question_id") UUID questionId
    ) {

        return userAnswerService.scoreAnswer(questionId);
    }

    @PostMapping("/create-list-user-answer")
    @DefaultMessage("Create list user answer successfully")
    public ResponseEntity<UserAnswerResponse> createUserAnswer(@RequestBody List<UserAnswerRequest> requests) {

        return ResponseEntity.ok(userAnswerService.createUserAnswer(requests));

    }


}
