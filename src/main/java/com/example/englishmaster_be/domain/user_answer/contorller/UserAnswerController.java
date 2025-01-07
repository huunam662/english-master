package com.example.englishmaster_be.domain.user_answer.contorller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerBlankResponse;
import com.example.englishmaster_be.domain.user_answer.service.IUserAnswerService;
import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.mapper.UserAnswerMapper;
import com.example.englishmaster_be.mapper.UserBlankAnswerMapper;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest1;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerScoreResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerResponse;
import com.example.englishmaster_be.model.user_answer.UserAnswerEntity;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingEntity;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public void createUserAnswer(@RequestBody List<UserAnswerRequest> requests) {

        userAnswerService.createUserAnswer(requests);

    }


    @PostMapping("/create-user-answer")
    @DefaultMessage("Create user answer successfully")
    public UserAnswerResponse createUserAnswer(@RequestBody UserAnswerRequest1 userAnswerRequest1) {

        UserAnswerEntity answer = userAnswerService.saveUserAnswer(userAnswerRequest1);

        return UserAnswerMapper.INSTANCE.toUserAnswerResponse(answer);
    }

    @PostMapping("/create-user-answer-blank")
    @DefaultMessage("Create user answer successfully")
    public UserAnswerBlankResponse createUserAnswerBlank(@RequestBody AnswerBlankRequest request) {

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
