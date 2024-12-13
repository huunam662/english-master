package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.Mapper.UserAnswerMapper;
import com.example.englishmaster_be.Mapper.UserBlankAnswerMapper;
import com.example.englishmaster_be.Model.Request.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.Model.Request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.Model.Request.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Common.dto.response.ResponseModel;
import com.example.englishmaster_be.Model.Response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.Model.Response.ScoreAnswerResponse;
import com.example.englishmaster_be.Model.Response.UserAnswerResponse;
import com.example.englishmaster_be.Model.Response.UserBlankAnswerResponse;
import com.example.englishmaster_be.entity.UserAnswerEntity;
import com.example.englishmaster_be.entity.UserAnswerMatchingEntity;
import com.example.englishmaster_be.entity.UserBlankAnswerEntity;
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
@RequestMapping("/user-answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerController {

    UserAnswerService userAnswerService;



    @GetMapping("/check-blank")
    @MessageResponse("AnswerEntity Checking")
    public void checkAnswer(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerBlank(questionId,userId);
    }

    @GetMapping("/check-multiple-choice")
    @MessageResponse("AnswerEntity Checking")
    public void checkAnswerMultipleChoice(
            @RequestParam(value = "user_id") UUID userId,
            @RequestParam(value = "question_id") UUID questionId
    ){

        userAnswerService.checkCorrectAnswerMultipleChoice(questionId,userId);
    }

    @GetMapping("/check-correct")
    @MessageResponse("Check answer successfully")
    public ScoreAnswerResponse checkCorrectAnswer(
            @RequestParam(value = "question_id") UUID questionId
    ) {

        return userAnswerService.scoreAnswer(questionId);
    }


    @PostMapping("/create-user-answer")
    @MessageResponse("Create user answer successfully")
    public UserAnswerResponse createUserAnswer(@RequestBody UserAnswerRequest userAnswerRequest) {

        UserAnswerEntity answer = userAnswerService.saveUserAnswer(userAnswerRequest);

        return UserAnswerMapper.INSTANCE.toUserAnswerResponse(answer);
    }

    @PostMapping("/create-user-answer-blank")
    @MessageResponse("Create user answer successfully")
    public UserBlankAnswerResponse createUserAnswerBlank(@RequestBody AnswerBlankRequest request) {

        UserBlankAnswerEntity answer = userAnswerService.createUserBlankAnswer(request);

        return UserBlankAnswerMapper.INSTANCE.toUserBlankAnswerResponse(answer);
    }

    @PostMapping("/create-user-answer-matching")
    @MessageResponse("Create user answer successfully")
    public AnswerMatchingBasicResponse createUserAnswerMatching(@RequestBody AnswerMatchingQuestionRequest request) {

        UserAnswerMatchingEntity userAnswerMatchingEntity = userAnswerService.createUserMatchingAnswer(request);

        return AnswerMatchingMapper.INSTANCE.toAnswerMatchingBasicResponse(userAnswerMatchingEntity);
    }





}
