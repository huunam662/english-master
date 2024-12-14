package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.mapper.UserAnswerMapper;
import com.example.englishmaster_be.mapper.UserBlankAnswerMapper;
import com.example.englishmaster_be.model.request.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.model.request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.model.request.Answer.UserAnswerRequest;
import com.example.englishmaster_be.model.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.model.response.ScoreAnswerResponse;
import com.example.englishmaster_be.model.response.UserAnswerResponse;
import com.example.englishmaster_be.model.response.UserBlankAnswerResponse;
import com.example.englishmaster_be.entity.UserAnswerEntity;
import com.example.englishmaster_be.entity.UserAnswerMatchingEntity;
import com.example.englishmaster_be.entity.UserBlankAnswerEntity;
import com.example.englishmaster_be.service.impl.UserAnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Tag(name = "User Answer")
@RestController
@RequestMapping("/user-answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerController {

    UserAnswerService userAnswerService;



    @GetMapping("/check-blank")
    @DefaultMessage("AnswerEntity Checking")
    public void checkAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerBlank(questionId,userId);
    }

    @GetMapping("/check-multiple-choice")
    @DefaultMessage("AnswerEntity Checking")
    public void checkAnswerMultipleChoice(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerMultipleChoice(questionId,userId);
    }

    @GetMapping("/check-correct")
    @DefaultMessage("Check answer successfully")
    public ScoreAnswerResponse checkCorrectAnswer(
            @RequestParam(value = "question_id") UUID questionId
    ) {

        return userAnswerService.scoreAnswer(questionId);
    }


    @PostMapping("/create-user-answer")
    @DefaultMessage("Create user answer successfully")
    public UserAnswerResponse createUserAnswer(@RequestBody UserAnswerRequest userAnswerRequest) {

        UserAnswerEntity answer = userAnswerService.saveUserAnswer(userAnswerRequest);

        return UserAnswerMapper.INSTANCE.toUserAnswerResponse(answer);
    }

    @PostMapping("/create-user-answer-blank")
    @DefaultMessage("Create user answer successfully")
    public UserBlankAnswerResponse createUserAnswerBlank(@RequestBody AnswerBlankRequest request) {

        UserBlankAnswerEntity answer = userAnswerService.createUserBlankAnswer(request);

        return UserBlankAnswerMapper.INSTANCE.toUserBlankAnswerResponse(answer);
    }

    @PostMapping("/create-user-answer-matching")
    @DefaultMessage("Create user answer successfully")
    public AnswerMatchingBasicResponse createUserAnswerMatching(@RequestBody AnswerMatchingQuestionRequest request) {

        UserAnswerMatchingEntity userAnswerMatchingEntity = userAnswerService.createUserMatchingAnswer(request);

        return AnswerMatchingMapper.INSTANCE.toAnswerMatchingBasicResponse(userAnswerMatchingEntity);
    }





}
